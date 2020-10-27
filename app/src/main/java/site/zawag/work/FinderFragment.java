package site.zawag.work;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import site.zawag.work.constants.Constants;

public class FinderFragment extends Fragment implements Constants {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private SectionsPagerAdapter adapter;
    private InterstitialAd mInterstitialAd;

    public FinderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_finder, container, false);

        getActivity().setTitle(R.string.nav_people_nearby);
        //interstial ad

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstial_ad_unit));

        loadIntAd();

        mViewPager = rootView.findViewById(R.id.view_pager);

        adapter = new SectionsPagerAdapter(getChildFragmentManager());


        adapter.addFragment(new PeopleNearbyFragment(), getString(R.string.nav_people_nearby));
        adapter.addFragment(new SearchFragment(), getString(R.string.nav_search));
        adapter.addFragment(new HotgameFragment(), getString(R.string.nav_hotgame));

        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(2);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

               if(mInterstitialAd.isLoaded()){
                   mInterstitialAd.show();
               }else {
                   loadIntAd();
               }

                getActivity().setTitle(mViewPager.getAdapter().getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout = rootView.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);




        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                loadIntAd();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                loadIntAd();
            }
        });



        return rootView;
    }

    private void loadIntAd() {

            if (!mInterstitialAd.isLoaded() && !mInterstitialAd.isLoading()){
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

    }

    public void onCloseHotgameSettingsDialog(int sexOrientation, int liked, int matches) {

        HotgameFragment p = (HotgameFragment) adapter.getItem(2);
        p.onCloseHotgameSettingsDialog(sexOrientation, liked, matches);
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {

            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (mViewPager.getCurrentItem()) {

            case 0: {

                PeopleNearbyFragment p = (PeopleNearbyFragment) adapter.getItem(0);
                p.onRequestPermissionsResult(requestCode, permissions, grantResults);

                break;
            }

            case 2: {

                HotgameFragment p = (HotgameFragment) adapter.getItem(2);
                p.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

            default: {

                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
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