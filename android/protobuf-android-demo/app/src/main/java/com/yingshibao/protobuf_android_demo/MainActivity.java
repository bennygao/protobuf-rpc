package com.yingshibao.protobuf_android_demo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yingshibao.app.idl.Barrage;
import com.yingshibao.app.idl.None;
import com.yingshibao.app.idl.Push;
import com.yingshibao.app.idl.RegisterResult;
import com.yingshibao.app.idl.UserInfo;
import com.yingshibao.app.idl.UserManager;

import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.RpcSession;
import cc.devfun.pbrpc.MessagePrinter;
import cc.devfun.pbrpc.nio.NioClientEndpoint;
import cc.devfun.pbrpc.nio.NioClientSession;

public class MainActivity extends ActionBarActivity {
    Button btnTestSync, btnTestAsync;
    TextView txtContents;
    NioClientEndpoint endpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtContents = (TextView) findViewById(R.id.txt_contents);
        txtContents.setMovementMethod(ScrollingMovementMethod.getInstance());

        // 创建Endpoint
        endpoint = new NioClientEndpoint();
        // 注册服务
        endpoint.registerService(new UserManager());
        endpoint.registerService(new Push(new Push.Impl() { // 客户端提供给服务器调用的服务
            @Override
            public None pushBarrage(Barrage barrage, RpcSession session) {
                txtContents.append("\n--------\n");
                txtContents.append(MessagePrinter.print(barrage));
                return null;
            }
        }));

        // 连接服务器
        endpoint.connect("test.yingshibao.com", 10000);
        try {
            // 启动Endpoint
            endpoint.start();
        } catch (Throwable t) {
            Log.e("protobuf-rpc", "start endpoint exception.", t);
        }

        btnTestSync = (Button) findViewById(R.id.btn_testSync);
        btnTestSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建UserManager#registerNewUser的调用参数UserInfo
                UserManager.Client client = new UserManager.Client(new NioClientSession(endpoint));
                UserInfo userInfo = new UserInfo();
                userInfo.channelName = "360应用商店";
                userInfo.phone = "13810773316";
                userInfo.examType = 1;
                userInfo.nickName = "Johnn";

                try {
                    RegisterResult result = client.registerNewUser(userInfo);
                    txtContents.append("\n--------\n");
                    txtContents.append(MessagePrinter.print(result));
                } catch (Exception e) {
                    txtContents.append("\n--------\n");
                    txtContents.append(e.getMessage());
                }
            }
        });

        btnTestAsync = (Button) findViewById(R.id.btn_testAsync);
        btnTestAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建UserManager#registerNewUser的调用参数UserInfo
                UserManager.Client client = new UserManager.Client(new NioClientSession(endpoint));
                UserInfo userInfo = new UserInfo();
                userInfo.channelName = "360应用商店";
                userInfo.phone = "13810773316";
                userInfo.examType = 1;
                userInfo.nickName = "Johnn";

                try {
                    client.registerNewUser(userInfo, new Endpoint.Callback<RegisterResult>() {
                        @Override
                        public void onResponse(RegisterResult result) { // 异步调用成功
                            txtContents.append("\n--------\n");
                            txtContents.append(MessagePrinter.print(result));
                        }

                        @Override
                        public void onError(Endpoint.RpcError rpcError) { // 异步调用出错
                            txtContents.append("\n--------\n");
                            txtContents.append(rpcError.toString());;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
