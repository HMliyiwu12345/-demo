package com.example.fragmentdialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.example.fragmentdialog.fragmentdialog.MainDialog;
import com.example.fragmentdialog.fragmentdialog.WaitDialog;
import com.example.fragmentdialog.util.SpUtil;
import com.example.fragmentdialog.util.WYLog;
import com.example.httpclient.conf.Constant;
import com.example.httpclient.util.HttpPostUtil;
import com.example.httpclient.util.SignUtil;
import com.example.httpclient.util.ToastUtil;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private BillingClient mBillingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!TextUtils.isEmpty(SpUtil.getString(this, "account", null))) {
            WaitDialog waitDialog = WaitDialog.newInstance();
            waitDialog.show(this.getSupportFragmentManager(), "WAIT");

        } else {
            MainDialog mainDialog = MainDialog.newInstance(this, "主界面对话框");

            mainDialog.show(getSupportFragmentManager(), "MAIN");
        }

//        GooglePay();

        Button google = findViewById(R.id.b_google);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(SpUtil.getString(MainActivity.this, "account", null))) {
                    if (!SpUtil.getString(MainActivity.this, "account", null).equals("customLogin")) {
                        String path = Constant.getURL(MainActivity.this, "Pay/gg_pay", "49787", "23179fdc1487017f9eb70f1ca3cf17fc", null);
                        String player_server = URLEncoder.encode("一区");
                        String md5_post= SignUtil.MD5("1111111111com.cc11111110LFAS0FJWET439FASLHF3089FGDHO3FDSVCX0");
                        HashMap<String,String> maps=new HashMap<String,String>();
                        maps.put("extend","1111111111");
                        maps.put("productId","com.cc");
                        maps.put("ex_no","111111");
                        maps.put("price","10");
                        maps.put("player_server",player_server);
                        maps.put("player_role","zhangsan");
                        maps.put("md5_post",md5_post);
//                        String path2 = "extend=1111111111&productId=com.cc&ex_no=111111&price=10&player_server="+player_server+"&player_role=zhangsan&" +
//                                "md5_post="+md5_post;
//                        WYLog.d("path="+path+"?"+path2);

                        new HttpPostUtil().postHttp(path, maps, new HttpPostUtil.Enqueue() {
                            @Override
                            public void success(String s) {

//                                    WYLog.d(s);


                            }

                            @Override
                            public void fail(Exception e) {

                            }
                        });
                    }else{
                        ToastUtil.showToast(MainActivity.this,"目前是游客登录");
                    }
                }else{
                    ToastUtil.showToast(MainActivity.this,"您还没有登录");
                }





            }
        });

    }

    private void GooglePay() {
        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });


    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

    }
}
