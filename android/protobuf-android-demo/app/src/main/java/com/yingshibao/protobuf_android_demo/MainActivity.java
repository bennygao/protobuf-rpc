package com.yingshibao.protobuf_android_demo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.TextFormat;
import com.yingshibao.app.idl.Barrage;
import com.yingshibao.app.idl.None;
import com.yingshibao.app.idl.Push;
import com.yingshibao.app.idl.RegisterResult;
import com.yingshibao.app.idl.UserInfo;
import com.yingshibao.app.idl.UserManager;

import java.util.concurrent.LinkedBlockingQueue;


import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.RpcSession;
import cc.devfun.pbrpc.nio.NioClientEndpoint;
import cc.devfun.pbrpc.nio.NioClientSession;


public class MainActivity extends ActionBarActivity {
    Button btnTestSync, btnTestAsync;
    TextView txtContents;
    LinkedBlockingQueue<Integer> commandQueue;
    Handler handler;

    final static int TEST_SYNC = 1;
    final static int TEST_ASYNC = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtContents = (TextView) findViewById(R.id.txt_contents);
        txtContents.setMovementMethod(ScrollingMovementMethod.getInstance());
        commandQueue = new LinkedBlockingQueue<>();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                GeneratedMessage response = (GeneratedMessage) msg.obj;
                txtContents.append("\n--------\n");
                txtContents.append(TextFormat.printToUnicodeString(response));

                super.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                NioClientEndpoint endpoint = new NioClientEndpoint();
                endpoint.registerService(new UserManager());
                endpoint.registerService(new Push(new Push.Impl() {
                    @Override
                    public None pushBarrage(Barrage barrage, RpcSession session) {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = barrage;
                        handler.sendMessage(msg);
                        return null;
                    }
                }));
                endpoint.connect("test.yingshibao.com", 10000);
                try {
                    endpoint.start();
                } catch (Throwable t) {
                    Log.e("protobuf-rpc", "start endpoint exception.", t);
                    return;
                }

                UserManager.Client client = new UserManager.Client(new NioClientSession(endpoint));
                UserInfo userInfo = UserInfo.newBuilder().setChannelName("360应用商店")
                        .setPhone("13810773316").setExamType(1).setNickName("Johnn")
                        .build();
                while (true) {
                    try {
                        Integer command = commandQueue.take();
                        if (command == TEST_SYNC) {
                            RegisterResult result = client.registerNewUser(userInfo);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        } else {
                            client.registerNewUser(userInfo, new Endpoint.Callback() {
                                @Override
                                public void onResponse(GeneratedMessage generatedMessage) {
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.obj = generatedMessage;
                                    handler.sendMessage(msg);
                                }

                                @Override
                                public void onError(Endpoint.RpcError rpcError) {
                                    Log.e("protobuf-rpc", "rpc error: " + rpcError);
                                }
                            });
                        }
                    } catch (Throwable t) {
                        Log.e("protobuf-rpc", "thread exception.");
                    }
                }
            }
        }).start();

        btnTestSync = (Button) findViewById(R.id.btn_testSync);
        btnTestSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commandQueue.add(TEST_SYNC);
            }
        });

        btnTestAsync = (Button) findViewById(R.id.btn_testAsync);
        btnTestAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commandQueue.add(TEST_ASYNC);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
