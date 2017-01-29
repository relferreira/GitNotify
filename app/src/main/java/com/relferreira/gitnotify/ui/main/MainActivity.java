package com.relferreira.gitnotify.ui.main;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.relferreira.gitnotify.ApplicationComponent;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.repository.data.OrganizationColumns;
import com.relferreira.gitnotify.ui.base.BaseActivity;
import com.relferreira.gitnotify.util.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainView, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.main_viewpager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_loading)
    ProgressBar loading;
    @Inject
    MainPresenter presenter;
    @Inject
    Navigator navigator;

    private Resources resources;
    private static final int LOADER_ID = 1;
    private TabsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        resources = getResources();

        setSupportActionBar(toolbar);

        adapter = new TabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

        presenter.attachView(this);
        presenter.loadToastMsg();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!presenter.checkIfIsLogged())
            navigator.goToLogin(this);
    }

    @Override
    public void injectActivity(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{OrganizationColumns.LOGIN};
        return new CursorLoader(this, GithubProvider.Organizations.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        while (!data.isAfterLast()) {
            String tabName = data.getString(data.getColumnIndex(OrganizationColumns.LOGIN));
            adapter.add(GitNotificationsFragment.newInstance(), tabName);
            data.moveToNext();
        }
        loading.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}