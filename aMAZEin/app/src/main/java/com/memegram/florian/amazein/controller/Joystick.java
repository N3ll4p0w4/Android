package com.memegram.florian.amazein.controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.memegram.florian.amazein.R;

public class Joystick extends FrameLayout {
    private static final String LOG_TAG = Joystick.class.getSimpleName();

    private static final int MS_STICK_DELAY = 100;
    private static final Interpolator INTERPOLATOR_STICK_RITORNO = new DecelerateInterpolator();

    private int toccoSlop;

    private float centroX, centroY;
    private float radius;

    private View figlioDraggato;
    private boolean dragIndividuato;
    private boolean dragInProgresso;

    private float giuX, giuY;
    private static final int PUNTATORE_INVALIDO = -1;
    private int puntatore_id_attivato = PUNTATORE_INVALIDO;

    private boolean lokkato;

    private boolean partiQuandoTocca = true;
    private boolean forzaQuadrato = true;
    private boolean haUnRaggioFisso = false;

    public enum Movimento {
        NESSUNO,
        ORRIZONTALE,
        VERTICALE
    }

    private Movimento movimento = Movimento.NESSUNO;

    private JoystickListener listener;

    public Joystick(Context context) {
        super(context);
        init(context, null);
    }

    public Joystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Joystick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Joystick(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        toccoSlop = configuration.getScaledTouchSlop();

        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Joystick);
            partiQuandoTocca = a.getBoolean(R.styleable.Joystick_start_on_first_touch, partiQuandoTocca);
            forzaQuadrato = a.getBoolean(R.styleable.Joystick_force_square, forzaQuadrato);
            haUnRaggioFisso = a.hasValue(R.styleable.Joystick_radius);
            if (haUnRaggioFisso) {
                radius = a.getDimensionPixelOffset(R.styleable.Joystick_radius, (int) radius);
            }
            movimento = Movimento.values()[a.getInt(R.styleable.Joystick_motion_constraint,
                    movimento.ordinal())];
            a.recycle();
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centroX = (float) w / 2;
        centroY = (float) h / 2;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed && !haUnRaggioFisso) {
            ricalcolaRaggio(right - left, bottom - top);
        }
    }

    private void ricalcolaRaggio(int W, int H) {
        float metaW = 0;
        float metaH = 0;
        if (haStick()) {
            final View stick = getChildAt(0);
            metaW = (float) stick.getWidth() / 2;
            metaH = (float) stick.getHeight() / 2;
        }

        switch (movimento) {
            case NESSUNO:
                radius = (float) Math.min(W, H) / 2 - Math.max(metaW, metaH);
                break;
            case ORRIZONTALE:
                radius = (float) W / 2 - metaW;
                break;
            case VERTICALE:
                radius = (float) H / 2 - metaH;
                break;
        }
    }

    public void setListener(JoystickListener listener) {
        this.listener = listener;

        if (!haStick()) {
            Log.w(LOG_TAG, LOG_TAG + " has no draggable stick, and is therefore not functional. " +
                    "Consider adding a child view to act as the stick.");
        }
    }

    public void lock() {
        lokkato = true;
    }

    public int getToccoSlop() {
        return toccoSlop;
    }

    public void setToccoSlop(int toccoSlop) {
        this.toccoSlop = toccoSlop;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;

        if (!haUnRaggioFisso) ricalcolaRaggio(getWidth(), getHeight());
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isStartOnFirstTouch() {
        return partiQuandoTocca;
    }

    public void setStartOnFirstTouch(boolean startOnFirstTouch) {
        this.partiQuandoTocca = startOnFirstTouch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (dragIndividuato || !haStick()) return false;

                giuX = event.getX(0);
                giuY = event.getY(0);
                puntatore_id_attivato = event.getPointerId(0);

                onStartDetectingDrag();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (PUNTATORE_INVALIDO == puntatore_id_attivato) break;
                if (dragIndividuato && dragExceedsSlop(event)) {
                    onDragStart();
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId != puntatore_id_attivato)
                    break; // if active pointer, fall through and cancel!
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                onTouchEnded();

                onStopDetectingDrag();
                break;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!isEnabled()) return false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (!dragIndividuato) return false;
                if (partiQuandoTocca) onDragStart();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (PUNTATORE_INVALIDO == puntatore_id_attivato) break;

                if (dragInProgresso) {
                    int pointerIndex = event.findPointerIndex(puntatore_id_attivato);
                    float latestX = event.getX(pointerIndex);
                    float latestY = event.getY(pointerIndex);

                    float deltaX = latestX - giuX;
                    float deltaY = latestY - giuY;

                    onDrag(deltaX, deltaY);
                    return true;
                } else if (dragIndividuato && dragExceedsSlop(event)) {
                    onDragStart();
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId != puntatore_id_attivato)
                    break; // if active pointer, fall through and cancel!
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                onTouchEnded();

                if (dragInProgresso) {
                    onDragStop();
                } else {
                    onStopDetectingDrag();
                }
                return true;
            }
        }

        return false;
    }

    private boolean dragExceedsSlop(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(puntatore_id_attivato);
        final float x = event.getX(pointerIndex);
        final float y = event.getY(pointerIndex);
        final float dx = Math.abs(x - giuX);
        final float dy = Math.abs(y - giuY);

        switch (movimento) {
            case NESSUNO:
                return dx * dx + dy * dy > toccoSlop * toccoSlop;
            case ORRIZONTALE:
                return dx > toccoSlop;
            case VERTICALE:
                return dy > toccoSlop;
        }
        return false;
    }

    private void onTouchEnded() {
        puntatore_id_attivato = PUNTATORE_INVALIDO;
    }

    private boolean haStick() {
        return getChildCount() > 0;
    }

    private void onStartDetectingDrag() {
        dragIndividuato = true;
        if (null != listener) listener.onDown();
    }

    private void onStopDetectingDrag() {
        dragIndividuato = false;
        if (!lokkato && null != listener) listener.onUp();

        lokkato = false;
    }

    private void onDragStart() {
        dragInProgresso = true;
        figlioDraggato = getChildAt(0);
        figlioDraggato.animate().cancel();
        onDrag(0, 0);
    }

    private void onDragStop() {
        dragInProgresso = false;

        if (!lokkato) {
            figlioDraggato.animate()
                    .translationX(0).translationY(0)
                    .setDuration(MS_STICK_DELAY)
                    .setInterpolator(INTERPOLATOR_STICK_RITORNO)
                    .start();
        }

        onStopDetectingDrag();
        figlioDraggato = null;
    }

    private void onDrag(float dx, float dy) {
        float x = giuX + dx - centroX;
        float y = giuY + dy - centroY;

        switch (movimento) {
            case ORRIZONTALE:
                y = 0;
                break;
            case VERTICALE:
                x = 0;
                break;
        }

        float offset = (float) Math.sqrt(x * x + y * y);
        if (x * x + y * y > radius * radius) {
            x = radius * x / offset;
            y = radius * y / offset;
            offset = radius;
        }

        final double radians = Math.atan2(-y, x);
        final float degrees = (float) (180 * radians / Math.PI);

        if (null != listener) listener.onDrag(degrees, 0 == radius ? 0 : offset / radius);

        figlioDraggato.setTranslationX(x);
        figlioDraggato.setTranslationY(y);
    }

    /*
    FORCE SQUARE
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!forzaQuadrato) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if (widthMode == MeasureSpec.EXACTLY && widthSize > 0) {
            size = widthSize;
        } else if (heightMode == MeasureSpec.EXACTLY && heightSize > 0) {
            size = heightSize;
        } else {
            size = widthSize < heightSize ? widthSize : heightSize;
        }

        int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(finalMeasureSpec, finalMeasureSpec);
    }

    /*
    CENTER CHILD BY DEFAULT
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutParams params = new LayoutParams(getContext(), attrs);
        params.gravity = Gravity.CENTER;
        return params;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(@NonNull ViewGroup.LayoutParams p) {
        LayoutParams params = new LayoutParams(p);
        params.gravity = Gravity.CENTER;
        return params;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException(LOG_TAG + " can host only one direct child");
        }

        super.addView(child, index, params);
    }
}
