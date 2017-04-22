package com.unotrack.device.demo.unobeacTag;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.minew.device.MinewDevice;
import com.minew.device.MinewDeviceListener;
import com.minew.device.MinewDeviceValue;
import com.unotrack.device.demo.R;
import com.unotrack.device.demo.interfaces.unobeacTagListener;
import com.minew.device.enums.InstrucIndex;
import com.minew.device.enums.ValueIndex;

import java.util.HashMap;

public class unobeacTag {

    public MinewDevice mMinewDevice;
    private unobeacTagListener munobeacTagListener;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private int streamID;
    private boolean isplay;
    private Context mContext;

    public unobeacTag(MinewDevice minewDevice, Context context) {
        mMinewDevice = minewDevice;
        mContext = context;
        mMinewDevice.setMinewDeviceListener(minewDeviceListener);
    }

    public void setunobeacTagListener(unobeacTagListener unobeacTagListener) {
        munobeacTagListener = unobeacTagListener;
    }


    public MinewDeviceListener minewDeviceListener = new MinewDeviceListener() {
        @Override
        public void onUpdateValue(MinewDevice minewDevice, MinewDeviceValue value) {

            if (minewDevice.getValue(ValueIndex.ValueIndex_Connected).isBool()) {
                if (soundPool != null) {
                    soundPool.stop(streamID);
                }
            } else {
                boolean applosealert = minewDevice.getValue(ValueIndex.ValueIndex_AppLoseAlert).isBool();
                if (applosealert) {
                    playSound(0);
                }
            }

            if (munobeacTagListener != null) {
                munobeacTagListener.onUpdateValue(minewDevice, value);
            }
        }

        @Override
        public void onSendData(MinewDevice minewDevice, InstrucIndex index, boolean success) {
            if (munobeacTagListener != null) {
                munobeacTagListener.onSendData(minewDevice, index, success);
            }
        }

        @Override
        public void onReceiveInstructionfromDevice(InstrucIndex index, MinewDevice device) {

            switch (index) {
                case InstrucIndex_ButtonPushed:
                    Log.e("tag", "InstrucIndex_ButtonPushed");
                    if (isplay) {
                        soundPool.stop(streamID);
                        isplay = false;
                    } else {
                        playSound(0);
                        isplay = true;
                    }
                    break;
            }

            if (munobeacTagListener != null) {
                munobeacTagListener.onReceiveInstructionfromDevice(index, device);
            }
        }
    };

    public void initializeAlarm() {
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<>();
        soundPoolMap.put(1, soundPool.load(mContext, R.raw.alarmsound, 2));

    }

    public void playSound(final int loop) {
        if (soundPool != null) {
            soundPool.stop(streamID);
            AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = streamVolumeCurrent / streamVolumeMax;
            streamID = soundPool.play(soundPoolMap.get(1), volume, volume, 1, loop, 1f);
        } else {
            initializeAlarm();
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    float volume = streamVolumeCurrent / streamVolumeMax;
                    streamID = soundPool.play(soundPoolMap.get(1), volume, volume, 1, loop, 1f);
                }
            });
        }

    }
}
