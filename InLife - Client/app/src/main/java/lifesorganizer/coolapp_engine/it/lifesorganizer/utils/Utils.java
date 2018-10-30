package lifesorganizer.coolapp_engine.it.lifesorganizer.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.Account;

import static lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.AccountManager.creaAccount;
import static lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.AccountManager.getAccountById;

public class Utils {

    public static void nascondiTastieraStartup(Activity activity){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static void nascondiTastiera(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void mostraTastiera(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    public static Bitmap bitmapFromBytes(byte[] array){
        Bitmap decodedImage = BitmapFactory.decodeByteArray(array, 0, array.length);
        return decodedImage;
    }

    public static byte[] bytesFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }

    public static String bitmapToTempFile(Context context, Bitmap bitmap, String name) {
        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageFile.getAbsolutePath();
    }

    public static Bitmap tempFileToBitmap(String path){
        File file = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return bitmap;
    }

    public static ArrayList<String> getStringsFromStringWithPuntoEVirgola(String s){
        ArrayList<String> stringhe = new ArrayList<>();
        while(s.contains(";")){
            String s1 = s.substring(0, s.indexOf(";"));
            stringhe.add(s1);
            s = s.substring(s.indexOf(";")+1);
        }
        return stringhe;
    }

}
