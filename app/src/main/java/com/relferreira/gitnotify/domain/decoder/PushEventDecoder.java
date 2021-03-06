package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Push;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.util.SchedulerProvider;

/**
 * Created by relferreira on 2/5/17.
 */

public class PushEventDecoder implements DescriptionDecoder {

    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public PushEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        String[] branchRef = payload.get("ref").getAsString().split("/");
        String branch = branchRef[branchRef.length - 1];
        String repo = event.repo().name();

        return String.format(context.getString(R.string.action_push), actor, branch, repo);
    }

    @Override
    public String getSubtitle() {
        JsonArray commits = payload.getAsJsonArray("commits");
        if(commits.size() > 1)
            return String.format(context.getString(R.string.action_push_multiple_commits), commits.size());
        else if(commits.size() > 0)
            return commits.get(0).getAsJsonObject().get("message").getAsString();
        return null;
    }

    @Override
    public String getDetailTitle() {
        return context.getString(R.string.push_title);
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {
        Push push = interactor.constructPush(payload);
        listener.successLoadingData(push.commits());
    }

    @Override
    public void loadPage(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener, Integer page) {

    }
}