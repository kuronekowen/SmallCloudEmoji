package org.sorz.lab.smallcloudemoji.activites;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import org.sorz.lab.smallcloudemoji.R;
import org.sorz.lab.smallcloudemoji.db.DaoSession;
import org.sorz.lab.smallcloudemoji.db.DatabaseHelper;
import org.sorz.lab.smallcloudemoji.db.DatabaseUpgrader;
import org.sorz.lab.smallcloudemoji.fragments.RepositoryFragment;
import org.sorz.lab.smallcloudemoji.fragments.SettingsFragment;


public class SettingsActivity extends Activity implements
        FragmentManager.OnBackStackChangedListener,
        SettingsFragment.OnSourceManageClickListener {
    private final static String REPOSITORY_FRAGMENT_IS_SHOWING = "REPOSITORY_FRAGMENT_IS_SHOWING";
    private RepositoryFragment repositoryFragment;
    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().addOnBackStackChangedListener(this);

        // Open database.
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this, true);
        daoSession = databaseHelper.getDaoSession();
        DatabaseUpgrader.checkAndDoUpgrade(this, daoSession);

        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REPOSITORY_FRAGMENT_IS_SHOWING,
                repositoryFragment != null && !repositoryFragment.isHidden());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean(REPOSITORY_FRAGMENT_IS_SHOWING, false)) {
            onSourceManageClick();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DatabaseHelper.getInstance(this).close();
    }

    @Override
    public void onBackStackChanged() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(
                    getFragmentManager().getBackStackEntryCount() > 0);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSourceManageClick() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment settingsFragment = fragmentManager.findFragmentById(R.id.settings_frag);
        if (repositoryFragment == null)
            repositoryFragment = new RepositoryFragment();
        fragmentManager.beginTransaction()
                .hide(settingsFragment)
                .add(R.id.settings_container, repositoryFragment)
                .addToBackStack(null)
                .commit();
    }

    public void hideRepository(View view) {
        repositoryFragment.hideRepository(view);
    }

    public void moveUpRepository(View view) {
        repositoryFragment.moveUpRepository(view);
    }

    public void moveDownRepository(View view) {
        repositoryFragment.moveDownRepository(view);
    }

    public void popMoreMenu(View view) {
        repositoryFragment.popMoreMenu(view);
    }

}
