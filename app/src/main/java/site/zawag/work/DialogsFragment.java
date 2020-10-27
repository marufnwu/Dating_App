package site.zawag.work;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import site.zawag.work.adapter.DialogsListAdapter;
import site.zawag.work.app.App;
import site.zawag.work.constants.Constants;
import site.zawag.work.model.Chat;
import site.zawag.work.util.CustomRequest;
import site.zawag.work.view.LineItemDecoration;

public class DialogsFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    RecyclerView mRecyclerView;
    NestedScrollView mNestedView;

    TextView mMessage;
    ImageView mSplash;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<Chat> itemsList;
    private DialogsListAdapter itemsAdapter;

    private int messageCreateAt = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;
    private InterstitialAd fbInsAd;
    private int position;
    private Chat item;

    public DialogsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        fbInsAd = new InterstitialAd(getContext(), getString(R.string.FB_INS_AD_UNIT));

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new DialogsListAdapter(getActivity(), itemsList);

            restore = savedInstanceState.getBoolean("restore");
            messageCreateAt = savedInstanceState.getInt("messageCreateAt");

        } else {

            itemsList = new ArrayList<Chat>();
            itemsAdapter = new DialogsListAdapter(getActivity(), itemsList);

            restore = false;
            messageCreateAt = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialogs, container, false);

        getActivity().setTitle(R.string.nav_messages);

        mItemsContainer = rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = rootView.findViewById(R.id.message);
        mSplash = rootView.findViewById(R.id.splash);

        mNestedView = rootView.findViewById(R.id.nested_view);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loadFbInstAd();

        mRecyclerView.setAdapter(itemsAdapter);

        itemsAdapter.setOnItemClickListener(new DialogsListAdapter.OnItemClickListener() {



            @Override
            public void onItemClick(View view, Chat item, int position) {


                /*position = pos;
                item = item1;*/

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("chatId", item.getId());
                intent.putExtra("profileId", item.getWithUserId());
                intent.putExtra("withProfile", item.getWithUserFullname());

                intent.putExtra("with_user_username", item.getWithUserUsername());
                intent.putExtra("with_user_fullname", item.getWithUserFullname());
                intent.putExtra("with_user_photo_url", item.getWithUserPhotoUrl());

                intent.putExtra("with_user_state", item.getWithUserState());
                intent.putExtra("with_user_verified", item.getWithUserVerify());

                intent.putExtra("blocked", item.getBlocked());

                intent.putExtra("fromUserId", item.getFromUserId());
                intent.putExtra("toUserId", item.getToUserId());

                startActivityForResult(intent, VIEW_CHAT);

                item.setNewMessagesCount(0);

                if (App.getInstance().getMessagesCount() > 0) {

                    App.getInstance().setMessagesCount(App.getInstance().getMessagesCount() - 1);
                    App.getInstance().saveData();
                }

                itemsAdapter.notifyDataSetChanged();

              /*  if (fbInsAd.isAdLoaded()){
                    fbInsAd.show();
                }else {
                    loadFbInstAd();
                    goToChatActivity();
                }*/


            }
        });

        mRecyclerView.setNestedScrollingEnabled(false);

        mNestedView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY < oldScrollY) { // up


                }

                if (scrollY > oldScrollY) { // down


                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    if (!loadingMore && (viewMore) && !(mItemsContainer.isRefreshing())) {

                        mItemsContainer.setRefreshing(true);

                        loadingMore = true;

                        getItems();
                    }
                }
            }
        });

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }

        fbInsAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");



                loadFbInstAd();

                goToChatActivity();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // Inflate the layout for this fragment






        return rootView;
    }

    private void goToChatActivity() {
        //we set here inst ad for user message click
       if (item!=null){
           Intent intent = new Intent(getActivity(), ChatActivity.class);
           intent.putExtra("position", position);
           intent.putExtra("chatId", item.getId());
           intent.putExtra("profileId", item.getWithUserId());
           intent.putExtra("withProfile", item.getWithUserFullname());

           intent.putExtra("with_user_username", item.getWithUserUsername());
           intent.putExtra("with_user_fullname", item.getWithUserFullname());
           intent.putExtra("with_user_photo_url", item.getWithUserPhotoUrl());

           intent.putExtra("with_user_state", item.getWithUserState());
           intent.putExtra("with_user_verified", item.getWithUserVerify());

           intent.putExtra("blocked", item.getBlocked());

           intent.putExtra("fromUserId", item.getFromUserId());
           intent.putExtra("toUserId", item.getToUserId());

           startActivityForResult(intent, VIEW_CHAT);

           item.setNewMessagesCount(0);

           if (App.getInstance().getMessagesCount() > 0) {

               App.getInstance().setMessagesCount(App.getInstance().getMessagesCount() - 1);
               App.getInstance().saveData();
           }

           itemsAdapter.notifyDataSetChanged();
       }
    }


    private void  loadFbInstAd(){
        if (!fbInsAd.isAdLoaded()){
            fbInsAd.loadAd();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            messageCreateAt = 0;
            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIEW_CHAT && resultCode == getActivity().RESULT_OK && null != data) {

            int pos = data.getIntExtra("position", 0);

            Toast.makeText(getActivity(), getString(R.string.msg_chat_has_been_removed), Toast.LENGTH_SHORT).show();

            itemsList.remove(pos);

            itemsAdapter.notifyDataSetChanged();

            if (itemsAdapter.getItemCount() == 0) {

                showMessage(getText(R.string.label_empty_list).toString());

            } else {

                hideMessage();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("messageCreateAt", messageCreateAt);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_DIALOGS_NEW_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "DialogsFragment Not Added to Activity");

                            return;
                        }

                        try {

                            arrayLength = 0;

                            if (!loadingMore) {

                                itemsList.clear();
                            }

                            if (!response.getBoolean("error")) {

                                messageCreateAt = response.getInt("messageCreateAt");

                                if (response.has("chats")) {

                                    JSONArray chatsArray = response.getJSONArray("chats");

                                    arrayLength = chatsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < chatsArray.length(); i++) {

                                            JSONObject chatObj = (JSONObject) chatsArray.get(i);

                                            Chat chat = new Chat(chatObj);

                                            itemsList.add(chat);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            loadingComplete();

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "DialogsFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("messageCreateAt", Integer.toString(messageCreateAt));
                params.put("language", "en");

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        viewMore = arrayLength == LIST_ITEMS;

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);

        mSplash.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}