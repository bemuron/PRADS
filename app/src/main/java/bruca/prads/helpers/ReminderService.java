package bruca.prads.helpers;

/**
 * Created by Emo on 6/17/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import bruca.prads.R;
import bruca.prads.activities.DisplayNoteActivity;

public class ReminderService extends WakeReminderIntentService {

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Log.d("ReminderService", "Doing work.");
        //int rowId = intent.getExtras().getInt(FeedReaderDbHelper.KEY_ID);
        int rowId = intent.getIntExtra("note-ID",0);

        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, DisplayNoteActivity.class);
        notificationIntent.putExtra("note-ID", rowId);

        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        //PendingIntent.FLAG_UPDATE_CURRENT

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        //builder.setTicker("this is ticker text");
        builder.setContentTitle("Focus Goal Review");
        builder.setContentText("A goal needs to be reviewed");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        //builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        //builder.build();

        //myNotication = builder.getNotification();
        //score_explanation = builder.build();
        // An issue could occur if user ever enters over 2,147,483,647 tasks. (Max int value).
        // I highly doubt this will ever happen. But is good to score_explanation.
        //int id = rowId;
        mgr.notify(rowId, builder.build());
/*
        Notification score_explanation=new Notification(android.R.drawable.stat_sys_warning, getString(R.string.notify_new_task_message), System.currentTimeMillis());
        score_explanation.setLatestEventInfo(this, getString(R.string.notify_new_task_title), getString(R.string.notify_new_task_message), pi);
        score_explanation.defaults |= Notification.DEFAULT_SOUND;
        score_explanation.flags |= Notification.FLAG_AUTO_CANCEL;
*/



    }
}
