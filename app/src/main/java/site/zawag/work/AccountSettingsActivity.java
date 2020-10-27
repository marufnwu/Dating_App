package site.zawag.work;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import site.zawag.work.common.ActivityBase;
import site.zawag.work.dialogs.AlcoholViewsSelectDialog;
import site.zawag.work.dialogs.GenderSelectDialog;
import site.zawag.work.dialogs.ImportantInOthersSelectDialog;
import site.zawag.work.dialogs.PersonalPrioritySelectDialog;
import site.zawag.work.dialogs.PoliticalViewsSelectDialog;
import site.zawag.work.dialogs.RelationshipStatusSelectDialog;
import site.zawag.work.dialogs.SexOrientationSelectDialog;
import site.zawag.work.dialogs.SmokingViewsSelectDialog;
import site.zawag.work.dialogs.WorldViewSelectDialog;
import site.zawag.work.dialogs.YouLikeSelectDialog;
import site.zawag.work.dialogs.YouLookingSelectDialog;

public class AccountSettingsActivity extends ActivityBase implements GenderSelectDialog.AlertPositiveListener, RelationshipStatusSelectDialog.AlertPositiveListener, PoliticalViewsSelectDialog.AlertPositiveListener, WorldViewSelectDialog.AlertPositiveListener, PersonalPrioritySelectDialog.AlertPositiveListener, ImportantInOthersSelectDialog.AlertPositiveListener, SmokingViewsSelectDialog.AlertPositiveListener, AlcoholViewsSelectDialog.AlertPositiveListener, YouLookingSelectDialog.AlertPositiveListener, YouLikeSelectDialog.AlertPositiveListener, SexOrientationSelectDialog.AlertPositiveListener {

    Toolbar mToolbar;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(site.zawag.work.R.layout.activity_account_settings);

        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment = new AccountSettingsFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(site.zawag.work.R.id.container_body, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(site.zawag.work.R.menu.menu_account_settings, menu);
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

    @Override
    public void onSexOrientationSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getSexOrientation(position);
    }

    @Override
    public void onGenderSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getGender(position);
    }

    @Override
    public void onRelationshipStatusSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getRelationshipStatus(position);
    }

    @Override
    public void onPoliticalViewsSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getPoliticalViews(position);
    }

    @Override
    public void onWorldViewSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getWorldView(position);
    }

    @Override
    public void onPersonalPrioritySelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getPersonalPriority(position);
    }

    @Override
    public void onImportantInOthersSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getImportantInOthers(position);
    }

    @Override
    public void onSmokingViewsSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getSmokingViews(position);
    }

    @Override
    public void onAlcoholViewsSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getAlcoholViews(position);
    }

    @Override
    public void onYouLookingSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getYouLooking(position);
    }

    @Override
    public void onYouLikeSelect(int position) {

        AccountSettingsFragment p = (AccountSettingsFragment) fragment;
        p.getYouLike(position);
    }
}
