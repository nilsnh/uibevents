package no.nilsnh.uibevents.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class EventSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = EventSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;

    public EventSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
    }
}
