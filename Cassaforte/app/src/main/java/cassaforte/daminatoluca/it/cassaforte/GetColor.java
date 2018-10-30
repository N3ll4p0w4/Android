package cassaforte.daminatoluca.it.cassaforte;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pigro on 28/01/2018.
 */

public class GetColor {

    public static int getColorPreference(Activity activity){
        String colore = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("colore", "Default");
        switch (colore){
            case "Default": return R.color.bgCassaforte;
            case "Bianco": return R.color.bianco;
            case "Giallo": return R.color.giallo;
            case "Arancione": return R.color.arancione;
            case "Rosso": return R.color.rosso;
            case "Verde": return R.color.verdeScuro;
            case "Blu": return R.color.blu;
            case "Viola": return R.color.viola;
            case "Nero": return R.color.nero;
            default: return R.color.bgCassaforte;
        }
    }
}
