package com.salmun.dani.geoport;

import android.app.Application;
import android.content.Context;

/**
 * Created by Dani on 22/11/2017.
 */
public class MainApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, LocaleHelper.getLanguage(base)));
    }
}