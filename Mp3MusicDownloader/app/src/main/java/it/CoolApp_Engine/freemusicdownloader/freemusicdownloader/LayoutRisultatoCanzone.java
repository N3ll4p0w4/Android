package it.CoolApp_Engine.freemusicdownloader.freemusicdownloader;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by luca.daminato on 30/12/2017.
 */

public class LayoutRisultatoCanzone {

    private RelativeLayout layout;
    private TextView nomeCanzone;
    private ImageButton downloadButton;
    private TextView downloadText;
    private ProgressBar progressBar;
    private TextView percentualeDownload;
    private Canzone canzone;

    private Activity activity;
    private Context context;
    private String tag;
    private WebView webView;
    private MediaPlayer mediaPlayer;
    private MusicPlayerController musicPlayerController;

    private Thread threadPercentuale;

    private SharedPreferences pref;

    private boolean canDelete;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private File destinationDir;
    private File destinationFile;
    private long downloadID;

    private int dpAltezzaCompatta = 50, dpAltezzaEspansa = 70;
    private int durataAnim = 250;

    @SuppressLint("ResourceType")
    public LayoutRisultatoCanzone(Canzone canzone, String tag, Activity activity, WebView webView, MusicPlayerController musicPlayerController) {
        this.canzone = canzone;
        this.tag = tag;
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.webView = webView;
        this.musicPlayerController = musicPlayerController;
        pref = context.getApplicationContext().getSharedPreferences(context.getResources().getString(R.string.shredPrefsImpostazioni), MODE_PRIVATE);
        destinationDir = new File(pref.getString(context.getResources().getString(R.string.pathDownloadFile), null));
        destinationFile = new File(destinationDir, canzone.getNome()+".mp3");
        canDelete = true;
        //LAYOUT
        layout = new RelativeLayout(context);
        layout.setTag("layout"+tag);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getPixel(dpAltezzaCompatta));
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getPixel(dpAltezzaCompatta));
        params2.setMargins(0, getPixel(5), 0, 0);
        layout.setLayoutParams(params2);
        layout.setBackgroundColor(context.getResources().getColor(R.color.sfondoLayoutCanzone));
        //downloadButton
        downloadButton = new ImageButton(context);
        downloadButton.setId(1);
        downloadButton.setTag("downlaodButton"+tag);
        layout.addView(downloadButton);
        if(!destinationFile.exists())
            downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_download2));
        else {
            downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_play));
        }
        downloadButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            params = new RelativeLayout.LayoutParams(getPixel(50), getPixel(50));
        else
            params = new RelativeLayout.LayoutParams(getPixel(45), getPixel(50));
        params.setMargins(0, 0, getPixel(15), 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        downloadButton.setLayoutParams(params);
        setDownloadButtonOnClick();
        //nomeCanzone
        nomeCanzone = new TextView(context);
        nomeCanzone.setId(2);
        layout.addView(nomeCanzone);
        nomeCanzone.setText(canzone.getNome());
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getPixel(40));
        params.setMargins(getPixel(10), getPixel(5), getPixel(65), 0);
        nomeCanzone.setLayoutParams(params);
        nomeCanzone.setGravity(Gravity.CENTER_VERTICAL);
        nomeCanzone.setTextColor(context.getResources().getColor(R.color.nero));
        nomeCanzone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        //downloadText
        downloadText = new TextView(context);
        downloadText.setId(3);
        downloadText.setTag("downloadText"+tag);
        downloadText.setText("Downloading...");
        layout.addView(downloadText);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_LEFT, nomeCanzone.getId());
        params.addRule(RelativeLayout.BELOW, downloadButton.getId());
        downloadText.setLayoutParams(params);
        downloadText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        downloadText.setTextColor(context.getResources().getColor(R.color.nero));
        //percentualeDownload
        percentualeDownload = new TextView(context);
        percentualeDownload.setId(4);
        percentualeDownload.setTag("percentualeDownload"+tag);
        percentualeDownload.setText("0%");
        layout.addView(percentualeDownload);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_START, downloadButton.getId());
        params.addRule(RelativeLayout.ALIGN_END, downloadButton.getId());
        params.addRule(RelativeLayout.BELOW, downloadButton.getId());
        percentualeDownload.setLayoutParams(params);
        percentualeDownload.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        percentualeDownload.setGravity(Gravity.CENTER);
        percentualeDownload.setTextColor(context.getResources().getColor(R.color.nero));
        //progressBar
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setId(5);
        progressBar.setTag("progressBar"+tag);
        layout.addView(progressBar);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(getPixel(10), 0, getPixel(10), 0);
        params.addRule(RelativeLayout.START_OF, percentualeDownload.getId());
        params.addRule(RelativeLayout.END_OF, downloadText.getId());
        params.addRule(RelativeLayout.BELOW, downloadButton.getId());
        progressBar.setLayoutParams(params);
        progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.verdeScuro), PorterDuff.Mode.SRC_IN);;
        progressBar.setProgress(0);
    }

    private void setDownloadButtonOnClick(){
        downloadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (downloadButton.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.icon_download2).getConstantState())) {
                    if (Internet.isConnected(context)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.linkCanzone = canzone.getDownload();
                                webView.loadUrl("javascript:window.location.href = \"" + canzone.getDownload() + "\";");
                            }
                        });
                    } else {
                        //NonConnesso
                    }
                } else if (downloadButton.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.icon_cancel).getConstantState())) {
                    stopDownload();
                } else if (downloadButton.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.icon_play).getConstantState())) {
                    mediaPlayer = MediaPlayer.create(context, Uri.fromFile(destinationFile));
                    musicPlayerController.startLayoutRisultatoCanzone(LayoutRisultatoCanzone.this);
                } else if (downloadButton.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.icon_pause).getConstantState())) {
                    musicPlayerController.stopLayoutRisultatoCanzone();
                }
            }
        });
    }

    public void setPlayIcon(){
        downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_play));
        layout.setTag("layout"+tag);
        canDelete = true;
    }

    public void setPauseIcon(){
        downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_pause));
        layout.setTag("layout"+tag+"Keep");
        canDelete = false;
    }

    public void setLayoutTag(String tag){
        layout.setTag("layout"+this.tag+"Keep"+tag);
    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public TextView getNomeCanzone() {
        return nomeCanzone;
    }

    public ImageButton getDownloadButton() {
        return downloadButton;
    }

    public TextView getDownloadText() {
        return downloadText;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getPercentualeDownload() {
        return percentualeDownload;
    }

    public Canzone getCanzone() {
        return canzone;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean canDelete() {
        return canDelete;
    }

    public void startDownload(String url){
        Uri source = Uri.parse(url);
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        request = new DownloadManager.Request(source);

        if (destinationFile.exists()) {
            Toast.makeText(context, context.getResources().getString(R.string.giaScaricata), Toast.LENGTH_SHORT).show();
        } else {
            if(canDelete) {
                canDelete = false;
                layout.setTag("layout"+tag+"Keep");
                request.setTitle(canzone.getNome()+".mp3");
                request.setDestinationUri(Uri.fromFile(destinationFile));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                downloadID = downloadManager.enqueue(request);
                espandi();
                downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cancel));
                Toast.makeText(context, context.getResources().getString(R.string.downloadIniziato), Toast.LENGTH_SHORT).show();
                threadPercentuale = new Thread() {
                    public void run() {
                        boolean downloading = true;
                        boolean error = false;
                        try {
                            while (downloading) {
                                DownloadManager.Query q = new DownloadManager.Query();
                                q.setFilterById(downloadID);
                                Cursor cursor = downloadManager.query(q);
                                cursor.moveToFirst();
                                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL)
                                    downloading = false;
                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED){
                                    downloading = false;
                                    error = true;
                                }
                                final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                                progressBar.setProgress((int) dl_progress);
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        percentualeDownload.setText(dl_progress + "%");
                                    }
                                });
                                cursor.close();
                                Thread.sleep(1000);
                            }
                        } catch (Exception e){
                            this.interrupt();
                        }

                        if(!downloading && !error) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, context.getResources().getString(R.string.downloadFinito), Toast.LENGTH_SHORT).show();
                                    canDelete = true;
                                    layout.setTag("layout"+tag);
                                    rimpicciolisci();
                                    downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_play));
                                    mediaPlayer = MediaPlayer.create(context, Uri.parse(destinationFile.getPath()));
                                }
                            });
                        } else if(error) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, context.getResources().getString(R.string.downloadErrore), Toast.LENGTH_SHORT).show();
                                    layout.setTag("layout"+tag);
                                    canDelete = true;
                                    downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_download2));
                                }
                            });
                        }
                    }
                };
                threadPercentuale.start();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.downloadGiaIniziato), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void stopDownload(){
        if(downloadManager != null) {
            threadPercentuale.interrupt();
            layout.setTag("layout"+tag);
            downloadManager.remove(downloadID);
            rimpicciolisci();
            canDelete = true;
            downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_download2));
            Toast.makeText(context, context.getResources().getString(R.string.downloadStoppato), Toast.LENGTH_SHORT).show();
        }
    }

    public void setProgress(int percent){
        percentualeDownload.setText(percent+"%");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            progressBar.setProgress(percent, true);
        else
            progressBar.setProgress(percent);
    }

    private int getPixel(int dp){
        return (int)(dp*context.getResources().getDisplayMetrics().density);
    }

    private int getDp(int pixel){
        return (int)(pixel/context.getResources().getDisplayMetrics().density);
    }

    public void espandi() {
        int prevHeight  = getDp(layout.getHeight());
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, dpAltezzaEspansa);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layout.getLayoutParams().height = getPixel((int)animation.getAnimatedValue());
                layout.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(durataAnim);
        valueAnimator.start();
    }

    public void rimpicciolisci() {
        int prevHeight  = getDp(layout.getHeight());
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, dpAltezzaCompatta);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layout.getLayoutParams().height = getPixel((int)animation.getAnimatedValue());
                layout.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(durataAnim);
        valueAnimator.start();
    }

}
