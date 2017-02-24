package com.example.huimin_zhou.fitrunner.Service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.example.huimin_zhou.fitrunner.Util.FFT;
import com.example.huimin_zhou.fitrunner.Util.WekaClassifier;
import com.example.huimin_zhou.fitrunner.Util.Global;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Lucidity on 17/2/15.
 */

public class SensorService  extends Service implements SensorEventListener {
    private boolean mIsBound = false;
    private Messenger mClient; // track current client
    private Messenger mRecvMsger = new Messenger(new IncomingMessengerHandler());
    private SensorManager mSensorManager;
    private SensorChangedTask mAsyncTask;
    private Object[] mData;

    private static ArrayBlockingQueue<Double> mAccBuffer;
    private int mActivType;

    @Override
    public void onCreate() {
        super.onCreate();
        mActivType = 0;
        mData = new Object[Global.ACCELEROMETER_BLOCK_CAPACITY];
        mAccBuffer = new ArrayBlockingQueue<Double>(Global.ACCELEROMETER_BUFFER_CAPACITY);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_FASTEST);
        mAsyncTask = new SensorChangedTask();
        mAsyncTask.execute();
    }

    /***************************** Service *****************************/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {return START_STICKY;}

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mRecvMsger.getBinder();
    }

    /***************************** Sensor Listener *****************************/
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1] + event.values[2]
                    * event.values[2]);

            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.
            try {
                mAccBuffer.add(new Double(m));
            } catch (IllegalStateException e) {
                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Double(m));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroy() {
        mAsyncTask.cancel(true);
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    /***************************** Sensor Changed Task *****************************/
    private class SensorChangedTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params_0) {
            int blockSize = 0;
            FFT fft = new FFT(Global.ACCELEROMETER_BLOCK_CAPACITY);
            double[] accBlock = new double[Global.ACCELEROMETER_BLOCK_CAPACITY];
            double[] re = accBlock;
            double[] im = new double[Global.ACCELEROMETER_BLOCK_CAPACITY];

            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled() == true) {
                        return null;
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == Global.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0;

                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            double mag = Math.sqrt(re[i] * re[i] + im[i]
                                    * im[i]);
                            mData[i] = mag;
                            im[i] = .0; // Clear the field
                        }
                        mActivType = (int) (WekaClassifier.classify(mData));
                        TrackingService.countActiv(mActivType);
                        sendMsgToUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void sendMsgToUI() {
        Message toSend = Message.obtain(null, Global.MSG_SENSOR_CHANGE);
        Bundle bundle = new Bundle();
        bundle.putInt(Global.KEY_SENSOR_TYPE, mActivType);
        toSend.setData(bundle);
        try {
            if (mIsBound && mClient != null) {
                mClient.send(toSend);
            }
        } catch (RemoteException e) {}
    }

    /***************************** Incoming handler *****************************/
    private class IncomingMessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Global.MSG_REGISTER_SENSOR_CLIENT:
                    // Log.d("register", "sensor");
                    mClient = msg.replyTo;
                    mIsBound = true;
                    break;
                case Global.MSG_UNREGISTER_SENSOR_CLIENT:
                    mClient = null;
                    mIsBound = false;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
