package bruca.prads.helpers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

/**
 * Created by Emo on 6/17/2017.
 */

public class OnTestAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = ComponentInfo.class.getCanonicalName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received wake up from alarm manager.");

        String testName = intent.getStringExtra("test_name");

        WakeReminderIntentService.acquireStaticLock(context);

        Intent i = new Intent(context, TestReminderService.class);
        i.putExtra("test_name", testName);
        context.startService(i);

    }
}
