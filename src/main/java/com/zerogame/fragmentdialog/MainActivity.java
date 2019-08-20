package com.zerogame.fragmentdialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;

import com.android.billingclient.api.BillingFlowParams;
//import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.zerogame.fragmentdialog.bean.GooglePay;
import com.zerogame.fragmentdialog.billing.BillingManager;
//import com.zerogame.fragmentdialog.billing.BillingProvider;
import com.zerogame.fragmentdialog.billing.BillingProvider;
import com.zerogame.fragmentdialog.fragmentdialog.MainDialog;
import com.zerogame.fragmentdialog.fragmentdialog.WaitDialog;
import com.zerogame.fragmentdialog.util.SpUtil;
import com.example.httpclient.conf.Constant;
import com.example.httpclient.util.HttpPostUtil;
import com.example.httpclient.util.SignUtil;
import com.example.httpclient.util.ToastUtil;
import com.google.gson.Gson;
import com.zerogame.fragmentdialog.util.WYLog;
import com.zerogame.sdktest.R;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BillingProvider {
    private Button google;
    private BillingManager mBillingManager;
    private BillingProvider mBillingProvider;


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
        mBillingManager=getBillingManager();

        google = findViewById(R.id.b_google);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(SpUtil.getString(MainActivity.this, "account", null))) {
                    if (!SpUtil.getString(MainActivity.this, "account", null).equals("customLogin")) {
                        String path = Constant.getURL(MainActivity.this, "Pay/gg_pay", "49787", "23179fdc1487017f9eb70f1ca3cf17fc", null);
                        String player_server = URLEncoder.encode("一区");
                        String md5_post = SignUtil.MD5("1111111111com.cc11111110LFAS0FJWET439FASLHF3089FGDHO3FDSVCX0");
                        HashMap<String, String> maps = new HashMap<String, String>();
                        maps.put("extend", "1111111111");
                        maps.put("productId", "com.cc");
                        maps.put("ex_no", "111111");
                        maps.put("price", "10");
                        maps.put("player_server", player_server);
                        maps.put("player_role", "zhangsan");
                        maps.put("md5_post", md5_post);

                        new HttpPostUtil().postHttp(path, maps, new HttpPostUtil.Enqueue() {
                            @Override
                            public void success(String s) {

                                Gson gson = new Gson();
                                GooglePay googlePay = gson.fromJson(s, GooglePay.class);
                                if (googlePay.getStatus() == 0) {
                                    GooglePay(googlePay.getDatas().getPay_order_number());
                                }

                            }

                            @Override
                            public void fail(Exception e) {

                            }
                        });
                    } else {
                        ToastUtil.showToast(MainActivity.this, "目前是游客登录");
                    }
                } else {
                    ToastUtil.showToast(MainActivity.this, "您还没有登录");
                }
            }

        });


    }

    private void GooglePay(final String purchaseId) {
        mBillingManager.initiatePurchaseFlow(purchaseId,BillingClient.SkuType.INAPP);

    }


    @Override
    public BillingManager getBillingManager() {
        return new BillingManager(MainActivity.this, new UpdateListener());
    }

    @Override
    public boolean isPremiumPurchased() {
        return false;
    }

    @Override
    public boolean isGoldMonthlySubscribed() {
        return false;
    }

    @Override
    public boolean isTankFull() {
        return false;
    }

    @Override
    public boolean isGoldYearlySubscribed() {
        return false;
    }


    private class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
//            通过商品ID，去查询Google后台是否有该ID的商品
            final List skuList = new ArrayList<>();
            skuList.add("S5P_32_20190820134403cZL5");
            mBillingManager.querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                    if (responseCode != BillingClient.BillingResponse.OK) {

                    } else if (skuDetailsList != null && skuDetailsList.size() > 0) {

                        for (SkuDetails details : skuDetailsList) {

                            //获取到所查商品信息
                        }
                    } else {
                        //没有要消耗的产品
//                        Toast.makeText(PayActivity.this, "没有要查询的产品", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }

        @Override
        public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {

//            Toast.makeText(PayActivity.this, "消耗完成", Toast.LENGTH_LONG).show();
            // Note: We know this is the SKU_GAS, because it's the only one we consume, so we don't
            // check if token corresponding to the expected sku was consumed.
            // If you have more than one sku, you probably need to validate that the token matches
            // the SKU you expect.
            // It could be done by maintaining a map (updating it every time you call consumeAsync)
            // of all tokens into SKUs which were scheduled to be consumed and then looking through
            // it here to check which SKU corresponds to a consumed token.
            if (result == BillingClient.BillingResponse.OK) {
                // Successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                //消耗成功
//                Toast.makeText(PayActivity.this, "消耗成功", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(PayActivity.this, "消耗失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            for (Purchase purchase : purchaseList) {
                //拿到订单信息，做自己的处理，发生到服务端验证订单信息，然后去消耗

                //购买成功，拿着令牌 去消耗
//                Toast.makeText(PayActivity.this, "购买成功：" + purchase.getPurchaseToken(), Toast.LENGTH_LONG).show();
                // We should consume the purchase and fill up the tank once it was consumed
                mBillingManager.consumeAsync(purchase.getPurchaseToken());
            }
        }
    }

}

