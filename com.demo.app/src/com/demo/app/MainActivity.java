
package com.demo.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.demo.app.R;
import com.demo.service.IServiceCallback;
import com.demo.service.Msg;
import com.demo.service.ServiceDemoAidl;

import java.util.List;

public class MainActivity extends Activity {

    TextView mTextView;
    
    private ServiceDemoAidl mService;     // service
    private ServiceConnection mConnection;
    private final Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what) {
                case Msg.SHOW_MSG:
                case Msg.HIDE_MSG:
                    Bundle b = new Bundle();
                    String str;
                    b = msg.getData();
                    str = b.getString("MSGTIP");
                    Log.v("appDemo", str);
                    mTextView.setText(str);
                    break;
                default:
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.msg);
        
        initConnection();
        startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        exitService();
    }

    private void startService() {
        Intent intent = new Intent("com.demo.service.ServiceDemo.START");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);  
        //startService(intent);
    }
    
    private void exitService() {
        try {
            if(mService != null)
            {
                mService.unregisterCallback(mCallback);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if(mConnection != null) {
            unbindService(mConnection);
            mConnection = null;
        }
    }
    
 // 连接service
    private void initConnection() {
        mConnection = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub
                mService = ServiceDemoAidl.Stub.asInterface(service);
                try {
                    if(mService.isInited()) {
                        mService.registerCallback(mCallback);    // 注册消息回调
                    }
                    else {
                        exitService();  // 初始化失败 退出
                    }
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                mService = null;
            }
            
        };
    }
    
    private IServiceCallback mCallback = new IServiceCallback.Stub() {

        @Override
        public void handlerCommEvent(int msgID, int param) throws RemoteException {
            // TODO Auto-generated method stub
            Message msg = new Message();
            
            msg.what = msgID;
            msg.arg1 = param;

            if(mHandler != null)
                mHandler.sendMessage(msg);  
        }

        @Override
        public void handlerSearchEvent(int msgID, List<String> strList) throws RemoteException {
            // TODO Auto-generated method stub
            Message msg = new Message();
            Bundle b = new Bundle();
            
            msg.what = msgID;
            
            switch (msgID) {
                case Msg.SHOW_MSG:
                case Msg.HIDE_MSG:
                    b.putString("MSGTIP",strList.get(0));    // ca提示框消息
                    break;
                default:
                    break;
            }
            
            msg.setData(b);
            if(mHandler != null)
                mHandler.sendMessage(msg);
        }
    };
}
