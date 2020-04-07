package com.example.mvp_demo.module.home;

import com.example.mvp_demo.base.BaseBean;
import com.example.mvp_demo.base.BaseView;
import com.example.mvp_demo.bean.UserArticle;

public interface HomeView extends BaseView {

    void setUserArticleData(BaseBean<UserArticle> bean, Integer type);

    void showUserArticleError(String msg, Integer type);
}
