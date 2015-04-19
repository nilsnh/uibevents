package no.nilsnh.uibevents.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UibEventsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static UibEventsSyncAdapter uibEventsSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (uibEventsSyncAdapter == null) {
                uibEventsSyncAdapter = new UibEventsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return uibEventsSyncAdapter.getSyncAdapterBinder();
    }
}