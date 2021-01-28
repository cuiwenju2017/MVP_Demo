package com.example.mvp_demo.module.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvp_demo.R;
import com.example.mvp_demo.base.BaseActivity;
import com.example.mvp_demo.base.BaseBean;
import com.example.mvp_demo.base.BaseRVAdapter;
import com.example.mvp_demo.base.BaseRVHolder;
import com.example.mvp_demo.bean.BannersBean;
import com.example.mvp_demo.bean.GetDataType;
import com.example.mvp_demo.bean.UserArticle;
import com.example.mvp_demo.module.adapter.ImageTitleNumAdapter;
import com.example.mvp_demo.module.webview.WebViewActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yechaoa.yutils.ToastUtil;
import com.yechaoa.yutils.YUtils;
import com.youth.banner.Banner;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity<HomePrensenter> implements HomeView {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mRefreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private int page = 0;
    private BaseRVAdapter<UserArticle.DatasBean> adapter;
    private Banner banner;

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
        presenter.banners();//干货banners图

        YUtils.showLoading("加载中...");
        presenter.getUserArticleList(page, GetDataType.GETDATA);// 广场列表数据

        adapter = new BaseRVAdapter<UserArticle.DatasBean>(R.layout.item_user_article) {
            @Override
            public void onBindVH(BaseRVHolder holder, UserArticle.DatasBean data, int position) {
                holder.setText(R.id.tv_title, data.getTitle());
                holder.setText(R.id.tv_shareUser, data.getShareUser());
                holder.setText(R.id.tv_niceShareDate, data.getNiceShareDate());

                holder.getView(R.id.ll).setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra("link", data.getLink());
                    startActivity(intent);
                });
            }
        };

        //无数据视图显示
        View empty = LayoutInflater.from(this).inflate(R.layout.layout_no_data, null, false);
        adapter.setEmptyView(empty);

        //banner轮播图
        View header = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_header, null, false);
        banner = header.findViewById(R.id.banner);
        adapter.addHeaderView(header);

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {//下拉刷新
            page = 0;
            presenter.getUserArticleList(page, GetDataType.REFRESH);
            presenter.banners();//干货banners图
        });

        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {//上拉加载更多
            page++;
            presenter.getUserArticleList(page, GetDataType.LOADMORE);
        });

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));//列数设置
    }

    @Override
    public void getUserArticleListData(BaseBean<UserArticle> bean, Integer type) {
        YUtils.hideLoading();

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
    public void getUserArticleListError(String msg, Integer type) {
        YUtils.hideLoading();
        ToastUtil.showCenter(msg);
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void onError(String msg) {
        ToastUtil.showCenter(msg);
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void bannersData(BannersBean bean) {
        banner.setAdapter(new ImageTitleNumAdapter(MainActivity.this, bean.getData()));
        banner.removeIndicator();
    }
}
