package com.test.audiotracktest;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Player {

    private final Context mContext;

    public Player(Context context){
        mContext = context;
    }

    public void playFile() {
        new Thread(new PlaybackRunnable()).start();
    }

    class PlaybackRunnable implements Runnable {
        @Override
        public void run() {
            InputStream is = null;
            try {

                //Load file
                is = mContext.getResources().openRawResource(
                        mContext.getResources().getIdentifier("th_oial_32_float_48khz",
                                "raw", mContext.getPackageName()));
                float[] fMusic = toFloatArray(IOUtils.toByteArray(is));

                //Setup audiotrack
                AudioTrack track;
                int bufferSize = AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_FLOAT);
                track = new AudioTrack(AudioManager.STREAM_MUSIC, 48000,
                        AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_FLOAT,
                        bufferSize, AudioTrack.MODE_STREAM);

                track.play();
                int offsetSize = 0;

                //Cycle through float array writing frames to track
                if(fMusic != null){
                    while (offsetSize < fMusic.length) {
                        if ((offsetSize + bufferSize) < fMusic.length) {
                        } else {
                            bufferSize = fMusic.length - offsetSize;
                        }
                        //Write to audiotrack
                        track.write(fMusic, offsetSize, bufferSize, AudioTrack.WRITE_BLOCKING);
                        offsetSize += bufferSize;
                    }
                }
                is.close();

            } catch (Exception e){
                if(is != null) {
                    try{
                        is.close();
                    } catch (Exception innerE){
                        //ignore
                    }
                }
            }
        }

        private float[] toFloatArray(byte[] bytes) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());
            FloatBuffer fb = buffer.asFloatBuffer();

            float[] floatArray = new float[fb.limit()];
            fb.get(floatArray);

            return floatArray;
        }
    }
}
