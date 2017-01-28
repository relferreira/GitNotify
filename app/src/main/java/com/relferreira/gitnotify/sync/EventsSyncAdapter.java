package com.relferreira.gitnotify.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.AuthRepository;
import com.relferreira.gitnotify.repository.LogRepository;
import com.relferreira.gitnotify.repository.OrganizationRepository;
import com.relferreira.gitnotify.util.RequestErrorHelper;

import java.util.List;

import rx.schedulers.Schedulers;

/**
 * Created by relferreira on 1/25/17.
 */
public class EventsSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = EventsSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private Context context;
    private final AuthRepository authRepository;
    private final OrganizationRepository organizationRepository;
    private final GithubService githubService;
    private final LogRepository Log;

    public EventsSyncAdapter(Context context, AuthRepository authRepository, OrganizationRepository organizationRepository,
                             GithubService githubService, LogRepository logRepository, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        this.authRepository = authRepository;
        this.organizationRepository = organizationRepository;
        this.githubService = githubService;
        this.Log = logRepository;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.i(LOG_TAG, "sync");
        githubService.listOrgs()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(organizations -> {
                    Log.i(LOG_TAG, "orgs");
                    organizationRepository.storeOrganizations(organizations);
                    loadEvents(account, organizations);
                }, throwable -> {
                    Throwable teste = throwable;
                    if(RequestErrorHelper.getCode(throwable) == 304) {
                        List<Organization> organizations = organizationRepository.listOrganizations();
                        loadEvents(account, organizations);
                    } else {
                        Log.e(LOG_TAG, "error retrieving organizations");
                    }
                });
    }

    public static void onAccountCreated(Account newAccount, Context context) {

        EventsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
    }

    private static Account getSyncAccount(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType(context.getString(R.string.sync_account_type));
        return accounts[0];
    }

    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private void loadEvents(Account account, List<Organization> organizations) {
        String username = authRepository.getUsername(account);

        for(Organization organization : organizations) {
            Log.i(LOG_TAG, "teste");
            loadOrganizationEvents(username, organization);
        }
    }

    private void loadOrganizationEvents(String username, Organization organization) {
        githubService.getOrgs(username, organization.login())
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(events -> {
                    Log.i(LOG_TAG, "certo");
                }, error -> {
                    error.printStackTrace();
                    Log.e(LOG_TAG, error.toString());
                });
    }
}
