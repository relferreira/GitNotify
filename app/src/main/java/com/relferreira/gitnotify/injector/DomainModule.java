package com.relferreira.gitnotify.injector;

import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.domain.AuthInteractor;
import com.relferreira.gitnotify.domain.EventInteractor;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.domain.OrganizationInteractor;
import com.relferreira.gitnotify.repository.interfaces.AuthRepository;
import com.relferreira.gitnotify.repository.interfaces.EventRepository;
import com.relferreira.gitnotify.repository.interfaces.OrganizationRepository;
import com.relferreira.gitnotify.util.ApiInterceptor;
import com.relferreira.gitnotify.util.CriptographyProvider;
import com.relferreira.gitnotify.util.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by relferreira on 2/5/17.
 */
@Module
public class DomainModule {

    @Provides
    @Singleton
    protected AuthInteractor provideAuthInteractor(AuthRepository authRepository) {
        return new AuthInteractor(authRepository);
    }

    @Provides
    @Singleton
    protected EventInteractor provideEventInteractor(EventRepository eventRepository) {
        return new EventInteractor(eventRepository);
    }

    @Provides
    @Singleton
    protected GithubInteractor provideOrganizationRepository(CriptographyProvider criptographyProvider, GithubService githubService, AuthRepository authRepository,
                                                             SchedulerProvider schedulerProvider, ApiInterceptor apiInterceptor) {
        return new GithubInteractor(criptographyProvider, githubService, authRepository, schedulerProvider, apiInterceptor);
    }

    @Provides
    @Singleton
    protected OrganizationInteractor provideOrganizationInteractor(OrganizationRepository organizationRepository) {
        return new OrganizationInteractor(organizationRepository);
    }
}
