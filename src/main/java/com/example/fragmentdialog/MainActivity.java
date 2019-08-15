package com.example.fragmentdialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.example.fragmentdialog.fragmentdialog.MainDialog;
import com.example.fragmentdialog.fragmentdialog.WaitDialog;
import com.example.fragmentdialog.util.SpUtil;
import com.example.httpclient.util.ToastUtil;

import java.util.ArrayList;
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

        GooglePay();

        Button google = findViewById(R.id.b_google);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> skuList = new ArrayList<>();
                skuList.add("S5P_32_2017121911525325q0");

                final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                mBillingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {

                                for (SkuDetails skuDetails : skuDetailsList) {
                                    String sku = skuDetails.getSku();
                                    String price = skuDetails.getPrice();
                                    ToastUtil.showToast(MainActivity.this,price);
                                }
                            }
                        });
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
