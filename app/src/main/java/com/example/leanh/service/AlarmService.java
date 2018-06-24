package com.example.leanh.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;

import com.example.leanh.receiver.AlarmReceiver;
import com.example.leanh.ultil.Constants;

public class AlarmService extends Service {
    MediaPlayer mediaPlayer; // this object to manage media

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO: processing on and off ringtone
        // get string from intent
        String on_Off = intent.getExtras().getString("ON_OFF");
        switch (on_Off) {
            case Constants.ADD_INTENT: // if string like this set start media
                // this is system default alarm alert uri
                Uri uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                // create mediaPlayer object
                mediaPlayer = MediaPlayer.create(this, uri);
                mediaPlayer.start();
                break;
            case Constants.OFF_INTENT:
                // this check if user pressed cancel
                // get the alarm cancel id to check if equal the
                // pendingIntent'trigger id(pendingIntent request code)
                // the AlarmReceiver.pendingIntentId is taken from AlarmReceiver
                // when one pendingIntent trigger
                int alarmId = intent.getExtras().getInt("AlarmId");
                // check if mediaPlayer created or not and if media is playing and id of
                // alarm and trigger pendingIntent is same  then stop music and reset it
                if (mediaPlayer != null && mediaPlayer.isPlaying() && alarmId == AlarmReceiver.pendingId) {
                    // stop media
                    mediaPlayer.stop();
                    // reset it
                    mediaPlayer.reset();
                }
                break;


        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO: Xử lý logic tắt nhạc chuông
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}