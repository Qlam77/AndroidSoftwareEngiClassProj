package ca.sclfitness.keeppace;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ca.sclfitness.keeppace.R;

public class PreferenceHelper {

    Context currentContext;
    public PreferenceHelper(Context context) {
        currentContext = context;
    }

    public void setTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentContext);
        String theme = sharedPreferences.getString(currentContext.getString(R.string.key_color), "1");

        if (theme.equals("2")) {
            currentContext.setTheme(android.R.style.Theme_Black_NoTitleBar);
        } else {
            currentContext.setTheme(android.R.style.Theme_Light_NoTitleBar);
        }
    }

    public void setText() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentContext);
        String textType = sharedPreferences.getString(currentContext.getString(R.string.key_text), "1");

        if (textType.equals("2")) {
            TypefaceUtil.overrideFont(currentContext, "SERIF", "fonts/ocraextended.ttf");
        } else {
            TypefaceUtil.overrideFont(currentContext, "SERIF", "fonts/racingsansoneregular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

        }
    }
}
