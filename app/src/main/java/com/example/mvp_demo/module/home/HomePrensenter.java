package com.example.mvp_demo.module.home;

import com.example.mvp_demo.base.BaseBean;
import com.example.mvp_demo.base.BaseObserver;
import com.example.mvp_demo.base.BasePresenter;
import com.example.mvp_demo.bean.BannersBean;
import com.example.mvp_demo.bean.UserArticle;
import com.example.mvp_demo.http.API;
import com.yechaoa.yutils.YUtils;

import java.util.Map;

import io.reactivex.observers.DisposableObserver;
import retrofit2.Retrofit;

public class HomePrensenter extends BasePresenter<HomeView> {
    public HomePrensenter(HomeView baseView) {
        super(baseView);
    }

    /**
     * 广场列表数据
     */
    public void getUserArticleList(Integer page, final Integer type) {
        addDisposable(apiServer.getUserArticleList(page), new BaseObserver<BaseBean<UserArticle>>() {
            @Override
            public void onSuccess(BaseBean<UserArticle> bean) {
                baseView.getUserArticleListData(bean, type);
            }

            @Override
            public void onError(String msg) {
                baseView.getUserArticleListError(msg, type);
            }
        });
    }

    /**
     * 干货banners图
     */
    public void banners() {
        addDisposable(apiServer.banners(), new BaseObserver<BannersBean>() {
            @Override
            public void onSuccess(BannersBean bean) {
                baseView.bannersData(bean);
            }

            @Override
            public void onError(String msg) {
                baseView.onError(msg);
            }
        });
    }
}
