package lifesorganizer.coolapp_engine.it.lifesorganizer;

import android.content.Intent;
import android.content.res.ColorStateList;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import es.dmoral.toasty.Toasty;
import lifesorganizer.coolapp_engine.it.lifesorganizer.server.ServerAbstact;
import lifesorganizer.coolapp_engine.it.lifesorganizer.server.ServerComunications;
import lifesorganizer.coolapp_engine.it.lifesorganizer.utils.Utils;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends Activity {

    // UI references.
    private LinearLayout loginForm, registerForm;
    private EditText loginEmail, loginPassword;
    private EditText registerName, registerUsername, registerEmail, registerPassword, registerRepeatPassword;
    private TextView errorText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupWindowAnimations();

        Utils.nascondiTastieraStartup(this);

        loginForm = findViewById(R.id.login_form);
        registerForm = findViewById(R.id.register_form);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        registerName = findViewById(R.id.register_name);
        registerUsername = findViewById(R.id.register_username);
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerRepeatPassword = findViewById(R.id.register_repeat_password);
        errorText = findViewById(R.id.login_error_text);
        progressBar = findViewById(R.id.login_progress_bar);

        setNormalEditTextField(loginEmail);
        setNormalEditTextField(loginPassword);
        setNormalEditTextField(registerName);
        setNormalEditTextField(registerUsername);
        setNormalEditTextField(registerEmail);
        setNormalEditTextField(registerPassword);
        setNormalEditTextField(registerRepeatPassword);

        loginEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL) {
                    loginPassword.requestFocus();
                    return true;
                }
                return false;
            }
        });

        setOnEditorActionListenerNext(loginEmail, loginPassword);
        setOnEditorActionListenerNext(registerName, registerUsername);
        setOnEditorActionListenerNext(registerUsername, registerEmail);
        setOnEditorActionListenerNext(registerEmail, registerPassword);
        setOnEditorActionListenerNext(registerPassword, registerRepeatPassword);

        loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    login(null);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!ServerComunications.isStarted())
            ServerComunications.getInstance().start();
    }

    public void setOnEditorActionListenerNext(EditText first, final EditText second){
        first.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL) {
                    second.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private boolean isNameValid(String name) {
        return !name.isEmpty() && !(name.contains(";"));
    }

    private boolean isUsernameValid(String username) {
        return !username.isEmpty() && !(username.contains(" ")) && !(username.contains(";"));
    }

    private boolean isEmailValid(String email) {
        return !email.isEmpty() && email.contains("@") && !(email.contains(" ")) && !(email.contains(";"));
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && !(password.contains(";"));
    }

    public void login(View view){
        Utils.nascondiTastiera(this);
        if(loginForm.getVisibility() == View.VISIBLE){
            setNormalEditTextField(loginEmail);
            setNormalEditTextField(loginPassword);

            loginEmail.setText(loginEmail.getText().toString().trim());

            errorText.setText("");
            if(!isUsernameValid(loginEmail.getText().toString()) && !isEmailValid(loginEmail.getText().toString())) {
                errorText.append(getResources().getString(R.string.error_invalid_username_email)+" ");
                setErrorEditTextField(loginEmail);
            }
            if(!isPasswordValid(loginPassword.getText().toString())) {
                errorText.append(getResources().getString(R.string.error_invalid_password)+" ");
                setErrorEditTextField(loginPassword);
            }
            Utils.nascondiTastiera(this);
            if(!errorText.getText().toString().isEmpty())
                errorText.setVisibility(View.VISIBLE);
            else {
                errorText.setVisibility(View.GONE);

                disableAllEditText();

                //Scambio dati col server
                ServerAbstact sa = new ServerAbstact() {
                    @Override
                    public void run() {
                        invia("Login");
                        invia(loginEmail.getText().toString());
                        invia(loginPassword.getText().toString());
                        String s = ricevi();
                        if(s.equals("true")) {
                            runOnUiThread(()->{
                                enableAllEditText();
                            });
                            startActivity(new Intent(getApplication(), MyProfile.class));
                            finish();
                        } else {
                            s = ricevi();
                            errorText.setText("");
                            runOnUiThread(()->{
                                enableAllEditText();
                                setNormalEditTextField(loginEmail);
                                setNormalEditTextField(loginPassword);
                            });
                            while(s.contains(";")){
                                String s1 = s.substring(0, s.indexOf(";"));
                                if(s1.equals("username")){
                                    runOnUiThread(()->{
                                        errorText.append(getResources().getString(R.string.error_invalid_username_email)+" ");
                                        setErrorEditTextField(loginEmail);
                                    });
                                } else if(s1.equals("password")){
                                    runOnUiThread(()->{
                                        errorText.append(getResources().getString(R.string.error_incorrect_password));
                                        setErrorEditTextField(loginPassword);
                                    });
                                }
                                s = s.substring(s.indexOf(";")+1);
                            }
                            runOnUiThread(()->{
                                errorText.setVisibility(View.VISIBLE);
                            });
                        }
                    }
                };
                sa.start();
            }
        } else {
            registerForm.setVisibility(View.GONE);
            loginForm.setVisibility(View.VISIBLE);
            errorText.setText("");
            errorText.setVisibility(View.GONE);
        }
    }

    public void register(View view){
        Utils.nascondiTastiera(this);
        if(registerForm.getVisibility() == View.VISIBLE){
            setNormalEditTextField(registerName);
            setNormalEditTextField(registerUsername);
            setNormalEditTextField(registerEmail);
            setNormalEditTextField(registerPassword);
            setNormalEditTextField(registerRepeatPassword);

            registerUsername.setText(registerUsername.getText().toString().toLowerCase().trim());
            registerEmail.setText(registerEmail.getText().toString().trim());

            errorText.setText("");
            if(!isNameValid(registerName.getText().toString())) {
                errorText.append(getResources().getString(R.string.error_invalid_name)+" ");
                setErrorEditTextField(registerName);
            }
            if(!isUsernameValid(registerUsername.getText().toString())) {
                errorText.append(getResources().getString(R.string.error_invalid_username)+" ");
                setErrorEditTextField(registerUsername);
            }
            if(!isEmailValid(registerEmail.getText().toString())) {
                errorText.append(getResources().getString(R.string.error_invalid_email)+" ");
                setErrorEditTextField(registerEmail);
            }
            if(!isPasswordValid(registerPassword.getText().toString())) {
                errorText.append(getResources().getString(R.string.error_invalid_password)+" ");
                setErrorEditTextField(registerPassword);
                setErrorEditTextField(registerRepeatPassword);
            }
            if(!registerPassword.getText().toString().equals(registerRepeatPassword.getText().toString())){
                errorText.append(getResources().getString(R.string.error_invalid_repeat_password)+" ");
                setErrorEditTextField(registerRepeatPassword);
            }
            Utils.nascondiTastiera(this);
            if(!errorText.getText().toString().isEmpty())
                errorText.setVisibility(View.VISIBLE);
            else {
                errorText.setVisibility(View.GONE);

                disableAllEditText();

                //Scambio dati col server
                ServerAbstact sa = new ServerAbstact() {
                    @Override
                    public void run() {
                        invia("Register");
                        invia(registerName.getText().toString());
                        invia(registerUsername.getText().toString());
                        invia(registerEmail.getText().toString());
                        invia(registerPassword.getText().toString());
                        String s = ricevi();
                        if(s.equals("true")) {
                            runOnUiThread(()->{
                                enableAllEditText();
                                Toasty.success(getApplicationContext(), getResources().getString(R.string.registation_completed), Toast.LENGTH_SHORT, true).show();
                            });
                            startActivity(new Intent(getApplication(), MyProfile.class));
                            finish();
                        } else {
                            s = ricevi();
                            errorText.setText("");
                            runOnUiThread(()->{
                                enableAllEditText();
                                setNormalEditTextField(loginEmail);
                                setNormalEditTextField(loginPassword);
                            });
                            while(s.contains(";")){
                                String s1 = s.substring(0, s.indexOf(";"));
                                if(s1.equals("usernameExist")){
                                    runOnUiThread(()->{
                                        errorText.append(getResources().getString(R.string.error_username_already_used)+" ");
                                        setErrorEditTextField(registerUsername);
                                    });
                                } else if(s1.equals("emailExist")){
                                    runOnUiThread(()->{
                                        errorText.append(getResources().getString(R.string.error_email_already_used));
                                        setErrorEditTextField(registerEmail);
                                    });
                                }
                                s = s.substring(s.indexOf(";")+1);
                            }
                            runOnUiThread(()->{
                                errorText.setVisibility(View.VISIBLE);
                            });
                        }
                    }
                };
                sa.start();
            }
        } else {
            loginForm.setVisibility(View.GONE);
            registerForm.setVisibility(View.VISIBLE);
            errorText.setText("");
            errorText.setVisibility(View.GONE);
        }
    }

    public void setErrorEditTextField(final EditText editTextField){
        editTextField.requestFocus();

        TextInputLayout til = ((TextInputLayout)editTextField.getParent().getParent());
        //Colore suggerimento senza premerlo
        til.setHintTextAppearance(R.style.TextAppearence_App_EditText_Red);
        //Linea
        editTextField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        //Suggerimento
        try {
            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{ getResources().getColor(android.R.color.holo_red_dark) }));

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{ getResources().getColor(android.R.color.holo_red_dark) }));
        } catch (Exception e) {
            e.printStackTrace();
        }

        editTextField.clearFocus();
    }

    public void setNormalEditTextField(final EditText editTextField){
        editTextField.requestFocus();

        TextInputLayout til = ((TextInputLayout)editTextField.getParent().getParent());
        //Suggerimento quando focused
        til.setHintTextAppearance(R.style.AppTheme);
        //Linea
        ViewCompat.setBackgroundTintList(editTextField, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        //Suggerimento quando non focused
        try {
            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{ getResources().getColor(R.color.colorAccent) }));

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{ getResources().getColor(R.color.colorAccent) }));
        } catch (Exception e) {
            e.printStackTrace();
        }

        editTextField.clearFocus();
    }

    public void disableAllEditText(){
        EditText[] et = new EditText[]{loginEmail, loginPassword, registerName, registerUsername, registerEmail, registerPassword, registerRepeatPassword};
        for(int i=0; i<et.length; i++){
            et[i].setEnabled(false);
        }
    }

    public void enableAllEditText(){
        EditText[] et = new EditText[]{loginEmail, loginPassword, registerName, registerUsername, registerEmail, registerPassword, registerRepeatPassword};
        for(int i=0; i<et.length; i++){
            et[i].setEnabled(true);
        }
    }

    public void unfocusAllEditText(){
        EditText[] et = new EditText[]{loginEmail, loginPassword, registerName, registerUsername, registerEmail, registerPassword, registerRepeatPassword};
        for(int i=0; i<et.length; i++){
            et[i].setFocusable(false);
            et[i].setFocusableInTouchMode(false);
        }
    }

    public void focusAllEditText(){
        EditText[] et = new EditText[]{loginEmail, loginPassword, registerName, registerUsername, registerEmail, registerPassword, registerRepeatPassword};
        for(int i=0; i<et.length; i++){
            et[i].setFocusable(true);
            et[i].setFocusableInTouchMode(true);
        }
    }

    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(2000);
        slide.setSlideEdge(Gravity.BOTTOM);
        getWindow().setExitTransition(slide);
    }

}

