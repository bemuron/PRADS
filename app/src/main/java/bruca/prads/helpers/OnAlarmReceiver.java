package bruca.prads.helpers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

/**
 * Created by Emo on 6/17/2017.
 */

public class OnAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = ComponentInfo.class.getCanonicalName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received wake up from alarm manager.");

        //int rowid = intent.getExtras().getInt(FeedReaderDbHelper.KEY_ID);
        int rowid = intent.getIntExtra("note-ID",0);

        WakeReminderIntentService.acquireStaticLock(context);

        Intent i = new Intent(context, ReminderService.class);
        i.putExtra("note-ID", rowid);
        context.startService(i);

    }
}
