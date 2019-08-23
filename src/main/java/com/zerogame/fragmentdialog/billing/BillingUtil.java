package com.zerogame.fragmentdialog.billing;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class BillingUtil implements PurchasesUpdatedListener {

    private static BillingClient mBillingClient;

    private boolean mIsServiceConnected;

    private final List<Purchase> mPurchases = new ArrayList<>();

    // Default value of mBillingClientResponseCode until BillingManager was not yeat initialized
    public static final int BILLING_MANAGER_NOT_INITIALIZED  = -1;
    private  int mBillingClientResponseCode =BILLING_MANAGER_NOT_INITIALIZED;

    private BillingUpdatesListener mBillingUpdatesListener;
    private Activity mActivity;

    private Set<String> mTokensToBeConsumed;

    /**
     * 获取实例对象
     *
     * @return
     */
    public static BillingUtil getInstance() {
        BillingUtil billingUtil = null;
        if (billingUtil == null) {
            billingUtil = new BillingUtil();
        }
        return billingUtil;
    }

    /**
     * 初始化连接，得到连接
     * @param activity
     * @param updatesListener
     */
    public void clientConnection(Activity activity, BillingUpdatesListener updatesListener) {
        if (mBillingClient == null) {
            mBillingClient = BillingClient.newBuilder(activity).setListener(this).enablePendingPurchases().build();
        }
        mBillingUpdatesListener=updatesListener;
        mActivity=activity;
        startServiceConnection(new Runnable() {
            @Override
            public void run() {
                mBillingUpdatesListener.onBillingClientSetupFinished();
                queryPurchases();
            }
        });

    }

    /**
     * 开始服务连接
     * @param executeOnSuccess
     */
    private void startServiceConnection(final Runnable executeOnSuccess) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if(billingResult.getResponseCode()== BillingClient.BillingResponseCode.OK){
                    mIsServiceConnected=true;
                    executeOnSuccess.run();

                }
                int responseCode=billingResult.getResponseCode();
                mBillingClientResponseCode=responseCode;

            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
            }
        });

    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            for (Purchase purchase : purchases) {
//                handlePurchase(purchase);
                /**
                 * 这里建议进行后台操作
                 */
            }
            mBillingUpdatesListener.onPurchasesUpdated(purchases);
        }else if(billingResult.getResponseCode()== BillingClient.BillingResponseCode.USER_CANCELED){
            Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
        } else {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + billingResult.getResponseCode());
        }
    }

    /**
     * 查询应用内所有商品信息
     * a listener
     */
    public void queryPurchases() {
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                Log.i(TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                        + "ms");
                // If there are subscriptions supported, we add subscription rows as well
                if (areSubscriptionsSupported()) {
                    Purchase.PurchasesResult subscriptionResult
                            = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
                    Log.i(TAG, "Querying purchases and subscriptions elapsed time: "
                            + (System.currentTimeMillis() - time) + "ms");
                    Log.i(TAG, "Querying subscriptions result code: "
                            + subscriptionResult.getResponseCode()
                            + " res: " + subscriptionResult.getPurchasesList().size());

                    if (subscriptionResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        purchasesResult.getPurchasesList().addAll(
                                subscriptionResult.getPurchasesList());
                    } else {
                        Log.e(TAG, "Got an error response trying to query subscription purchases");
                    }
                } else if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "Skipped subscription purchases query since they are not supported");
                } else {
                    Log.w(TAG, "queryPurchases() got an error response code: "
                            + purchasesResult.getResponseCode());
                }
                onQueryPurchasesFinished(purchasesResult);
            }
        };

        executeServiceRequest(queryToExecute);
    }

    /**
     * 如果断开就尝试重连
     * @param runnable
     */
    private void executeServiceRequest(Runnable runnable) {
        if (mIsServiceConnected) {
            runnable.run();
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable);
        }
    }

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     * 外部调用接口
     */
    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();

        //        void onConsumeFinished(String token, @BillingResponse int result);
        void onConsumeFinished(String token, @BillingClient.BillingResponseCode int result);

        void onPurchasesUpdated(List<Purchase> purchases);
    }

    /**
     *
     * 译文：
     * 检查当前客户端是否支持订阅
     * *
     * 注意:此方法不会自动重试RESULT_SERVICE_DISCONNECTED。
     * *它只用于单元测试和querypurchase执行之后，querypurchase已经执行了
     * *实现了一个回退机制。
     * Checks if subscriptions are supported for current client
     * <p>Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     * </p>
     */
    public boolean areSubscriptionsSupported() {
        BillingResult billingResult= mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);

        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: " + billingResult.getResponseCode());
        }
        return billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK;
    }


    /**
     * 初始化开始后查询应用内所有商品，外部给外部接口，更新所有商品
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private void onQueryPurchasesFinished(Purchase.PurchasesResult result) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.getResponseCode()
                    + ") was bad - quitting");
            return;
        }

        Log.d(TAG, "Query inventory was successful.");

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear();
        BillingResult billingResult=BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.OK).build();

        onPurchasesUpdated(billingResult, result.getPurchasesList());
    }

    /**
     * 取消连接
     * Clear the resources
     */
    public void destroy() {
        Log.d(TAG, "Destroying the manager.");

        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
    }


    /**
     * 购买商品
     * Start a purchase flow
     */
    public void initiatePurchaseFlow(final SkuDetails skuDetails, final @BillingClient.SkuType String billingType) {
        initiatePurchaseFlow(skuDetails, null, billingType);
    }

    /**
     * Start a purchase or subscription replace flow
     */
    public void initiatePurchaseFlow(final SkuDetails skuDetails, final ArrayList<String> oldSkus,
                                     final @BillingClient.SkuType String billingType) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSkus != null));
//                querySkuDetailsAsync();
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).
                        setOldSkus(oldSkus).build();
                mBillingClient.launchBillingFlow(mActivity, purchaseParams);
            }
        };

        executeServiceRequest(purchaseFlowRequest);
    }


    /**
     * 查询应用内商品详情
     * @param itemType
     * @param skuList
     * @param listener
     */

    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType, final List<String> skuList,
                                     final SkuDetailsResponseListener listener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable queryRequest = new Runnable() {
            @Override
            public void run() {
                // Query the purchase async
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(itemType);
                mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        listener.onSkuDetailsResponse(billingResult, skuDetailsList);
                    }
                });
//                new SkuDetailsResponseListener() {
//                            @Override
//                            public void onSkuDetailsResponse(int responseCode,
//                                                             List<SkuDetails> skuDetailsList) {
//                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
//                            }
//                        });
            }
        };

        executeServiceRequest(queryRequest);
    }


    /**
     * 消耗商品
     * @param consumeAsync
     */

    public void consumeAsync(final ConsumeParams consumeAsync) {
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        if (mTokensToBeConsumed == null) {
            mTokensToBeConsumed = new HashSet<>();
        } else if (mTokensToBeConsumed.contains(consumeAsync.getPurchaseToken())) {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
            return;
        }
        mTokensToBeConsumed.add(consumeAsync.getPurchaseToken());

        // Generating Consume Response listener
        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                mBillingUpdatesListener.onConsumeFinished(purchaseToken, billingResult.getResponseCode());
            }
        };

        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable consumeRequest = new Runnable() {
            @Override
            public void run() {
                // Consume the purchase async
                mBillingClient.consumeAsync(consumeAsync, onConsumeListener);
//                mBillingClient.consumeAsync(, onConsumeListener);
            }
        };

        executeServiceRequest(consumeRequest);
    }

//    /**
//     *
//     * @param purchase
//     */
//    private void handlePurchase(Purchase purchase)  {
//        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
//            Log.i(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
//            return;
//        }
//
//        Log.d(TAG, "Got a verified purchase: " + purchase);
//
//        mPurchases.add(purchase);
//    }
//
//    private boolean verifyValidSignature(String signedData, String signature) {
//        // Some sanity checks to see if the developer (that's you!) really followed the
//        // instructions to run this sample (don't put these checks on your app!)
//        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
//            throw new RuntimeException("Please update your app's public key at: "
//                    + "BASE_64_ENCODED_PUBLIC_KEY");
//        }
//
//        try {
//            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
//        } catch (IOException e) {
//            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
//            return false;
//        }
//    }
}
