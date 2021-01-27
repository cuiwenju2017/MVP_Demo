package com.example.mvp_demo.module.home;

import com.example.mvp_demo.base.BaseBean;
import com.example.mvp_demo.base.BaseView;
import com.example.mvp_demo.bean.BannersBean;
import com.example.mvp_demo.bean.UserArticle;

public interface HomeView extends BaseView {
    void getUserArticleListData(BaseBean<UserArticle> bean, Integer type);

    void getUserArticleListError(String msg, Integer type);

    void onError(String msg);

    void bannersData(BannersBean bean);
}
