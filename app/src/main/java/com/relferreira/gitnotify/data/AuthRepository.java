package com.relferreira.gitnotify.data;


/**
 * Created by relferreira on 1/23/17.
 */
public interface AuthRepository {

    void addAccount(String username, String token);
}
