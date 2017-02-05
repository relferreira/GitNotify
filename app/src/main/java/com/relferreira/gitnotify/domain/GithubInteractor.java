package com.relferreira.gitnotify.domain;

import com.relferreira.gitnotify.BuildConfig;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.model.ImmutableLoginRequest;
import com.relferreira.gitnotify.model.Login;
import com.relferreira.gitnotify.model.LoginRequest;
import com.relferreira.gitnotify.repository.interfaces.AuthRepository;
import com.relferreira.gitnotify.util.ApiInterceptor;
import com.relferreira.gitnotify.util.CriptographyProvider;
import com.relferreira.gitnotify.util.SchedulerProvider;

import rx.Observable;

/**
 * Created by relferreira on 2/5/17.
 */

public class GithubInteractor {

    private final CriptographyProvider criptographyProvider;
    private final GithubService githubService;
    private final AuthRepository authRepository;
    private final SchedulerProvider schedulerProvider;
    private final ApiInterceptor apiInterceptor;

    public GithubInteractor(CriptographyProvider criptographyProvider, GithubService githubService, AuthRepository authRepository,
                            SchedulerProvider schedulerProvider, ApiInterceptor apiInterceptor) {
        this.criptographyProvider = criptographyProvider;
        this.githubService = githubService;
        this.authRepository = authRepository;
        this.schedulerProvider = schedulerProvider;
        this.apiInterceptor = apiInterceptor;
    }

    public Observable<Login> login(String username, String password) {
        String credentials = username + ":" + password;
        final String basic = "Basic " + criptographyProvider.base64(credentials);
        LoginRequest loginRequest = ImmutableLoginRequest.builder()
                .addScopes("public_repo")
                .note("admin script")
                .clientId(BuildConfig.CLIENT_ID)
                .clientSecret(BuildConfig.CLIENT_SECRET)
                .build();
        apiInterceptor.setAuthValue(basic);
        return githubService.login(loginRequest)
                .compose(schedulerProvider.applySchedulers())
                .map(login -> {
                    authRepository.addAccount(username, basic);
                    return login;
                });
    }
}
