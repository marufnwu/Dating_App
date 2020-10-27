package site.zawag.work;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import site.zawag.work.app.App;
import site.zawag.work.common.ActivityBase;
import site.zawag.work.util.CustomRequest;
import site.zawag.work.util.Security;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BalanceActivity extends ActivityBase implements RewardedVideoAdListener {

    Toolbar mToolbar;

    // See documentation!

    static final String ITEM_SKU_1 = "site.com.site.zawag.iap1";            // Change to: yourdomain.com.iap1
    static final String ITEM_SKU_2 = "site.com.site.zawag.iap2";            // Change to: yourdomain.com.iap2
    static final String ITEM_SKU_3 = "site.com.site.zawag.iap3";            // Change to: yourdomain.com.iap3
    static final String ITEM_SKU_4 = "android.test.purchased";                   // Not edit! For testing - free purchases

    Button mBuy1Button, mBuy2Button, mBuy3Button, mBuy4Button;
    TextView mLabelCredits;

    Button mRewardedAdButton;

    private RewardedVideoAd mRewardedVideoAd;

    private Boolean loading = false;

    private BillingClient mBillingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_balance);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (loading) {

            showpDialog();
        }

        mLabelCredits = findViewById(R.id.labelCredits);

        mBuy1Button = findViewById(R.id.iap1_google_btn);
        mBuy2Button = findViewById(R.id.iap2_google_btn);
        mBuy3Button = findViewById(R.id.iap3_google_btn);
        mBuy4Button = findViewById(R.id.iap4_google_btn);      // For test Google Pay Button

        mRewardedAdButton = findViewById(R.id.rewarded_ad_btn);

        if (!GOOGLE_PAY_TEST_BUTTON) {

            mBuy4Button.setVisibility(View.GONE);
        }

        mBuy1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchBilling(ITEM_SKU_1);
            }
        });

        mBuy2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchBilling(ITEM_SKU_2);
            }
        });

        mBuy3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchBilling(ITEM_SKU_3);
            }
        });

        mBuy4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchBilling(ITEM_SKU_4);
            }
        });

        mRewardedAdButton.setVisibility(View.GONE);

        if (App.getInstance().getAllowRewardedAds() == 1) {

            mRewardedAdButton.setVisibility(View.GONE);

            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener(this);

            loadRewardedVideoAd();
        }

        mRewardedAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRewardedVideoAd.isLoaded()) {

                    mRewardedVideoAd.show();
                }
            }
        });

        update();

        setupBilling();
    }

    @Override
    protected void onResume() {

        if (mRewardedVideoAd != null) {

            mRewardedVideoAd.resume(this);
        }

        super.onResume();

        update();
    }

    private void setupBilling() {

        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {

            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

                if (purchases != null) {

                    for (Purchase purchase : purchases) {

                        switch (purchase.getSku()) {

                            case ITEM_SKU_1:

                                if (verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {

                                    App.getInstance().setBalance(App.getInstance().getBalance() + 30);
                                    payment(30, true);
                                }

                                break;

                            case ITEM_SKU_2:

                                if (verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {

                                    App.getInstance().setBalance(App.getInstance().getBalance() + 70);
                                    payment(70, true);
                                }

                                break;

                            case ITEM_SKU_3:

                                if (verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {

                                    App.getInstance().setBalance(App.getInstance().getBalance() + 120);
                                    payment(120, true);
                                }

                                break;

                            case ITEM_SKU_4:

                                App.getInstance().setBalance(App.getInstance().getBalance() + 100);
                                payment(100, true);

                                break;
                        }

                        consume(purchase.getPurchaseToken(), purchase.getDeveloperPayload());
                    }
                }
            }
        }).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    // The BillingClient is ready. You can query purchases here.

                    querySkuDetails();

                    Log.e("onBillingSetupFinished", "WORKS");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

                Log.e("onBillingSetupFinished", "NOT WORKS");
            }
        });
    }

    private void consume(String purchaseToken, String payload) {

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .setDeveloperPayload(payload)
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String outToken) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    // For example, increase the number of coins inside the user's basket.
                }
            }
        };

        mBillingClient.consumeAsync(consumeParams, listener);
    }

    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(ITEM_SKU_1);
        skuList.add(ITEM_SKU_2);
        skuList.add(ITEM_SKU_3);

        if (GOOGLE_PAY_TEST_BUTTON) {

            skuList.add(ITEM_SKU_4);
        }

        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    if (skuDetailsList != null) {

                        for (SkuDetails skuDetails : skuDetailsList) {

                            mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                        }
                    }
                }
            }
        });
    }

    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(skuId))
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    private boolean verifyValidSignature(String signedData, String signature) {

        try {

            return Security.verifyPurchase(BILLING_KEY, signedData, signature);

        } catch (IOException e) {

            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);

            return false;
        }
    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        return purchasesResult.getPurchasesList();
    }


    public void payment(final int cost, final Boolean showSuccess) {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_ADD_FUNDS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                if (response.has("balance")) {

                                    App.getInstance().setBalance(response.getInt("balance"));
                                }

                                if (showSuccess) {

                                    success();
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();

                            update();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("funds", Integer.toString(cost));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    // Fortumo related glue-code
    private static final int REQUEST_CODE = 1234; // Can be anything


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

            if (data == null) {

                return;
            }

        } else {

//            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
//
                //super.onActivityResult(requestCode, resultCode, data);
//            }
        }
    }


    public void update() {

        mLabelCredits.setText(getString(R.string.label_credits) + " (" + App.getInstance().getBalance() + ")");
    }

    public void success() {

        Toast.makeText(BalanceActivity.this, getString(R.string.msg_success_purchase), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }

    @Override
    protected void onStart() {

        super.onStart();

        if (!mBillingClient.isReady()) {

            setupBilling();
        }
    }

    @Override
    protected void onPause() {

        if (mRewardedVideoAd != null) {

            mRewardedVideoAd.pause(this);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (mRewardedVideoAd != null) {

            mRewardedVideoAd.destroy(this);
        }

        super.onDestroy();

        hidepDialog();

        mBillingClient.endConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_deactivate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }




    private void loadRewardedVideoAd() {

        mRewardedAdButton.setText(getString(R.string.msg_loading));

        mRewardedVideoAd.loadAd(getString(R.string.rewarded_ad_unit_id), new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {

        // Reward the user.

        Log.d("Rewarded Video", "onRewarded");

        Toast.makeText(BalanceActivity.this, getString(R.string.msg_success_rewarded), Toast.LENGTH_SHORT).show();

        mRewardedAdButton.setVisibility(View.GONE);

        App.getInstance().setBalance(App.getInstance().getBalance() + reward.getAmount());
        payment(reward.getAmount(), false);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

        Log.d("Rewarded Video", "onRewardedVideoAdLeftApplication");

        // user click by link in video ad
    }

    @Override
    public void onRewardedVideoAdClosed() {

        Log.d("Rewarded Video", "onRewardedVideoAdClosed");

        mRewardedAdButton.setVisibility(View.GONE);

        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {

        Log.d("Rewarded Video", "onRewardedVideoAdFailedToLoad");

        mRewardedAdButton.setVisibility(View.GONE);

        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {

        Log.d("Rewarded Video", "onRewardedVideoAdLoaded");

        mRewardedAdButton.setText(getString(R.string.action_view_rewarded_video_ad));
        mRewardedAdButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRewardedVideoAdOpened() {

        Log.d("Rewarded Video", "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {

        Log.d("Rewarded Video", "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted() {

        Log.d("Rewarded Video", "onRewardedVideoCompleted");
    }
}