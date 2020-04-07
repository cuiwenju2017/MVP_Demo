package com.example.mvp_demo.module.home;

import com.example.mvp_demo.base.BaseBean;
import com.example.mvp_demo.base.BaseObserver;
import com.example.mvp_demo.base.BasePresenter;
import com.example.mvp_demo.bean.UserArticle;

public class HomePrensenter extends BasePresenter<HomeView> {
    public HomePrensenter(HomeView baseView){
        super(baseView);
    }

    /**
     * 广场列表数据
     */
    public void getUserArticleList(Integer page, final Integer type) {
        addDisposable(apiServer.getUserArticleList(page), new BaseObserver<BaseBean<UserArticle>>(baseView,true) {
            @Override
            public void onSuccess(BaseBean<UserArticle> bean) {
                baseView.setUserArticleData(bean,type);
            }

            @Override
            public void onError(String msg) {
                baseView.showUserArticleError(msg,type);
            }
        });
    }
}
