package com.example.pro_servicecall;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pro_activity.IMyAidlInterface;

import java.util.List;

import androidx.annotation.Nullable;

public class MainActivity extends Activity {
    //ServiceConneciton对象
    private  ServiceConnection serviceConnection;
    private Intent exIntent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_main);

        //调用远程服务
        Intent intent=new Intent();
        intent.setAction("com.example.pro_activity.REMOTE_SER");
        //将隐性调用变成显性调用
        exIntent=new Intent(explicitIntent(MainActivity.this,intent));
        //创建serviceConnection对象
        serviceConnection= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //将IBinder转换成接口
                IMyAidlInterface iMyAidlInterface=IMyAidlInterface.Stub.asInterface(service);
                //判断是否链接还存在
                if (iMyAidlInterface!=null&&iMyAidlInterface.asBinder().isBinderAlive()){
                    //通过接口调用远程服务
                    try {
                        iMyAidlInterface.showToast();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(MainActivity.this,"链接已经断开",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(MainActivity.this,"断开链接",Toast.LENGTH_SHORT).show();
            }
        };

        Button button=findViewById(R.id.btn_callremoteservice);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bindService，绑定服务
                bindService(exIntent,serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });
        Button button1=findViewById(R.id.btn_unbindservice);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解除绑定
                unbindService(serviceConnection);
                stopService(exIntent);
            }
        });
    }

    //报错：java.lang.IllegalArgumentException: Service Intent must be explicit: Intent
    //将隐性调用变成显性调用
    public static Intent explicitIntent(Context context,Intent implicitIntent){
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
