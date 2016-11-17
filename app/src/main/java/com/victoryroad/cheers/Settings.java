package com.victoryroad.cheers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


public class Settings {
    //First value is how long to have vibration pattern off, then how long to have it on, and so on for the length of the array
    private final long[] vibrationPattern = new long[] {0, 500, 200, 500};

    private SharedPreferences p;
    private PreferenceManager prefManager;

    private MediaPlayer mMediaPlayer;

    private static Settings mSetting = new Settings();

    private Settings() {
        p = new SharedPreferences() {
            @Override
            public Map<String, ?> getAll() {
                return null;
            }

            @Nullable
            @Override
            public String getString(String key, String defValue) {
                return defValue;
            }

            @Nullable
            @Override
            public Set<String> getStringSet(String key, Set<String> defValues) {
                return defValues;
            }

            @Override
            public int getInt(String key, int defValue) {
                return defValue;
            }

            @Override
            public long getLong(String key, long defValue) {
                return defValue;
            }

            @Override
            public float getFloat(String key, float defValue) {
                return defValue;
            }

            @Override
            public boolean getBoolean(String key, boolean defValue) {
                return defValue;
            }

            @Override
            public boolean contains(String key) {
                return false;
            }

            @Override
            public Editor edit() {
                return null;
            }

            @Override
            public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

            }

            @Override
            public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

            }
    }}

    public static Settings getSettings() {
        return mSetting;
    }

    public static void setPreferenceManager(PreferenceManager m) {
        mSetting.setManager(m);
    }

    public void makeCallWithContact(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(this.getContact());
        context.startActivity(intent);
    }

    public void playRingtone(Context context) {
        if(this.wantsReminders() && this.wantsRing()) {

            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(context, this.getRingtone());
                final AudioManager audioManager = (AudioManager) context
                        .getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                }
            } catch (IOException e) {
                System.out.println("OOPS");
            }
        }
    }

    public void vibrate(Context context) {
        if(this.wantsVibrate()) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(vibrationPattern, -1);
        }
    }

    private void setManager(PreferenceManager manager) {
        prefManager = manager;
    }

    private void updateP() {
        if(prefManager == null)
            return;
        p = prefManager.getSharedPreferences();
    }

    public Uri getContact() {
        updateP();
        String uri = p.getString("custom_contact", null);
        if(uri == null)
            return null;

        return Uri.parse(uri);
    }

    public LatLng getHomeLocation() {
        updateP();
        return null;
    }

    public boolean wantsReminders() {
        updateP();
        return p.getBoolean("notifications_new_message", false);
    }

    public int timeBetweenNotifications() {
        updateP();
        return p.getInt("time_between_notifications", -1);
    }

    public boolean wantsRing() {
        updateP();
        return p.getBoolean("notifications_new_message_ring", false);
    }

    public Uri getRingtone() {
        updateP();
        return Uri.parse(p.getString("notifications_new_message_ring", "DEFAULT_SOUND"));
    }

    public boolean wantsVibrate() {
        updateP();
        return p.getBoolean("notifications_new_message_ringtone", false);
    }
}
