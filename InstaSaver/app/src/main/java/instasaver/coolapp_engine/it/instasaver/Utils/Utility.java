package instasaver.coolapp_engine.it.instasaver.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Utility {

    public static void nascondiTastiera(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Bitmap getBitmapFromUrl(String url){
        URL newurl = null;
        try {
            newurl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = null;
        while(bitmap == null) {
            try {
                bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            } catch (IOException e) {}
        }
        return bitmap;
    }

}
