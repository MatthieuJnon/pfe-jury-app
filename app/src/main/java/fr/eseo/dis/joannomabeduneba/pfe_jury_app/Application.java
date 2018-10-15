package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.content.Context;

public class Application extends android.app.Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        Application.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Application.context;
    }
}
