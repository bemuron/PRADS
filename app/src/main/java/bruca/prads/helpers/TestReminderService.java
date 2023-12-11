package bruca.prads.helpers;

/**
 * Created by Emo on 6/17/2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import bruca.prads.R;
import bruca.prads.activities.SelfEvaluationTests;

public class TestReminderService extends WakeReminderIntentService {

    public TestReminderService() {
        super("ReminderService");
    }
    // Sets an ID for the notification, so it can be updated
    private int notifyID = 1;

    @Override
    void doReminderWork(Intent intent) {
        Log.d("ReminderService", "Doing work.");

        String test = intent.getStringExtra("test_name");
        String testName = test.substring(0, 1).toUpperCase() + test.substring(1);

        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, SelfEvaluationTests.class);
        notificationIntent.putExtra("test", test);

        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        //PendingIntent.FLAG_UPDATE_CURRENT

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setTicker("this is ticker text");
        if (testName.equals("Who")){
            testName = "General wellbeing";
            builder.setContentTitle(testName+" Self evaluation test due.");
        }else{
            builder.setContentTitle(testName+" Self evaluation test due.");
        }
        builder.setContentText("Your next "+testName + " test is today" );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        //builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();

        //mgr.notify(notifyID, builder.build());

    }
}
