package com.example.mvp_demo.module.home;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.mvp_demo.R;
import com.example.mvp_demo.base.BaseActivity;
import com.example.mvp_demo.base.BaseBean;
import com.example.mvp_demo.base.BaseRVAdapter;
import com.example.mvp_demo.base.BaseRVHolder;
import com.example.mvp_demo.bean.GetDataType;
import com.example.mvp_demo.bean.UserArticle;
import com.example.mvp_demo.module.webview.WebViewActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yechaoa.yutils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity<HomePrensenter> implements HomeView {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mRefreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private int page = 0;
    BaseRVAdapter<UserArticle.DatasBean> adapter;

    @Override
    protected HomePrensenter createPresenter() {
        return new HomePrensenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        presenter.getUserArticleList(page, GetDataType.GETDATA);

        adapter = new BaseRVAdapter<UserArticle.DatasBean>(R.layout.item_user_article) {
            @Override
            public void onBindVH(BaseRVHolder holder, UserArticle.DatasBean data, int position) {
                holder.setText(R.id.tv_title, data.getTitle());
                holder.setText(R.id.tv_shareUser, data.getShareUser());
                holder.setText(R.id.tv_niceShareDate, data.getNiceShareDate());

                holder.getView(R.id.ll).setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra("link",data.getLink());
                    startActivity(intent);
                });
            }
        };

        //无数据视图显示
        View empty = LayoutInflater.from(this).inflate(R.layout.layout_no_data, null, false);
        adapter.setEmptyView(empty);

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {//下拉刷新
            page = 0;
            presenter.getUserArticleList(page, GetDataType.REFRESH);
            hideLoading();
        });

        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {//上拉加载更多
            page++;
            presenter.getUserArticleList(page, GetDataType.LOADMORE);
            hideLoading();
        });

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));//列数设置
    }

    @Override
    public void setUserArticleData(BaseBean<UserArticle> bean, Integer type) {

        List<UserArticle.DatasBean> mArticles = bean.data.getDatas();

        switch (type) {
            case GetDataType.GETDATA://获取数据成功
                adapter.setNewData(mArticles);
                break;
            case GetDataType.REFRESH://刷新成功
                adapter.setNewData(mArticles);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.setNoMoreData(false);
                break;
            case GetDataType.LOADMORE://加载成功
                if (mArticles != null && !mArticles.isEmpty()) {
                    adapter.addData(mArticles);
                    if (mArticles.size() < page) {
                        mRefreshLayout.finishLoadMoreWithNoMoreData();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }
                } else {
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                }
                break;
        }
    }

    @Override
    public void showUserArticleError(String msg, Integer type) {
        ToastUtil.showToast(msg);
    }
}
