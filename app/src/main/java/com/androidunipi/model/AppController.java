package com.androidunipi.model;


import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

public class AppController extends Application {
	
    public static final String TAG = AppController.class.getSimpleName();
 

 
    private static AppController mInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
 
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
       // ACRA.init(this);
    }
 
    public static synchronized AppController getInstance() {
        return mInstance;
    }
 

 

    
    
}