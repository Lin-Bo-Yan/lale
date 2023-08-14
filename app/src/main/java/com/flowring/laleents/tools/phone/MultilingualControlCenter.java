package com.flowring.laleents.tools.phone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.flowring.laleents.tools.SharedPreferencesUtils;
import com.flowring.laleents.tools.StringUtils;

import java.util.Locale;

public class MultilingualControlCenter {

    public static void setLocaleForMainAppCompat(Context context, String languageCode) {
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        Locale newLocale;
        String[] langCountry = languageCode.split("-");
        if (langCountry.length == 2) {
            newLocale = new Locale(langCountry[0], langCountry[1]);
        } else {
            newLocale = new Locale(languageCode);
        }
        config.setLocale(newLocale);
        context.createConfigurationContext(config);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
