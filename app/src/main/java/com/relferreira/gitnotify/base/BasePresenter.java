package com.relferreira.gitnotify.base;

/**
 * Created by relferreira on 10/29/16.
 */
public class BasePresenter<V extends BaseView> implements Presenter<V> {

    private V view;

    @Override
    public void attachView(V view) {
        this.view = view;
    }

    @Override
    public void dettachView() {
        if (view != null)
            view = null;
    }

    @Override
    public boolean isViewAttached() {
        return view != null;
    }

    @Override
    public V getView() {
        return view;
    }

}
