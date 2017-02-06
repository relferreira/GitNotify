package com.relferreira.gitnotify.domain.decoder;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;

/**
 * Created by relferreira on 2/5/17.
 */

public class PullRequestReviewEventDecoder implements DescriptionDecoder {
    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public PullRequestReviewEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().login();
        String repo = event.repo().name();
        int number = payload.getAsJsonObject("pull_request").get("number").getAsInt();
        return String.format(context.getString(R.string.action_pull_request_review), actor, repo, number);
    }

    @Override
    public String getSubtitle() {
        return payload.getAsJsonObject("review").get("body").getAsString();
    }
}
