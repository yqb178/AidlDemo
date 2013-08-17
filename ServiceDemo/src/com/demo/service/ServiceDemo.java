package com.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import java.util.ArrayList;


public class ServiceDemo extends Service {

    boolean bInit = false;
    Freq mFreq;
    int mValue = 0;
    
    final static RemoteCallbackList<IServiceCallback> mCallbacks
        = new RemoteCallbackList<IServiceCallback>();
    
    /**
     * Our Handler used to execute operations on the main thread.  This is used
     * to schedule increments of our value.
     */
    private final Handler mHandler = new Handler() {
        @Override 
        public void handleMessage(Message msg) {
            switch (msg.what) {
                
                // It is time to bump the value!
                case Msg.SHOW_MSG: 
                case Msg.HIDE_MSG: {
                    // Up it goes.
                    int value = ++mValue;
                    
                    sendMsg(msg.what);
                    
                    // Repeat every 1 second.
                    if(value%2 == 0)
                        sendMessageDelayed(obtainMessage(Msg.HIDE_MSG), 1*1000);
                    else
                        sendMessageDelayed(obtainMessage(Msg.SHOW_MSG), 1*1000);
                } break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    
    public ServiceDemo() {
        bInit = true;
    }
    
    public Freq getServiceFreq() {
        return new Freq();
    }
    
    public void setServiceFreq(Freq freq) {
        mFreq = freq;
    }
    
    public void sendMsg(int msgId) {
        ArrayList<String> strList = new ArrayList<String>();

        if(msgId == Msg.SHOW_MSG) {
            strList.add("ServiceDemo show msg"); // tip message
        } else {
            strList.add("ServiceDemo hide msg"); // tip message
        }
    
     // Broadcast to all clients the new value.
        final int N = mCallbacks.beginBroadcast();
        try {
            for (int i=0; i<N; i++) {
                mCallbacks.getBroadcastItem(i).handlerSearchEvent(msgId, strList);
            }
            
        } catch (RemoteException e) {
            // The RemoteCallbackList will take care of removing
            // the dead object for us.
        }

        mCallbacks.finishBroadcast();
    }
    
    
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return mBinder;
    }
    
    @Override
    public void onDestroy() {
        
        // Unregister all callbacks.
        mCallbacks.kill();
        
        // Remove the next pending message to increment the counter, stopping
        // the increment loop.
        mHandler.removeMessages(Msg.SHOW_MSG);
        mHandler.removeMessages(Msg.HIDE_MSG);
    }

    private final ServiceDemoAidl.Stub mBinder = new ServiceDemoAidl.Stub() {

        @Override
        public boolean isInited() throws RemoteException {
            // TODO Auto-generated method stub
            return bInit;
        }

        @Override
        public void registerCallback(IServiceCallback cb) throws RemoteException {
            // TODO Auto-generated method stub
            if (cb != null) mCallbacks.register(cb);
            mHandler.sendEmptyMessage(Msg.SHOW_MSG);
        }

        @Override
        public void unregisterCallback(IServiceCallback cb) throws RemoteException {
            // TODO Auto-generated method stub
            if (cb != null) mCallbacks.unregister(cb);
        }

        @Override
        public Freq getFreq() throws RemoteException {
            // TODO Auto-generated method stub
            return getServiceFreq();
        }

        @Override
        public void setFreq(Freq freq) throws RemoteException {
            // TODO Auto-generated method stub
            setServiceFreq(freq);
        }
    
    };
}
