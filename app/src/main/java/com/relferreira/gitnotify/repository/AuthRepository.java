package com.relferreira.gitnotify.repository;


import android.accounts.Account;

/**
 * Created by relferreira on 1/23/17.
 */
public interface AuthRepository {

    void addAccount(String username, String token);

    String getToken();

    String getUsername(Account account);
}
