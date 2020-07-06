package com.example.mvp_demo.module.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mvp_demo.R;
import com.example.mvp_demo.base.BaseActivity;
import com.example.mvp_demo.base.BaseBean;
import com.example.mvp_demo.base.BaseRVAdapter;
import com.example.mvp_demo.base.BaseRVHolder;
import com.example.mvp_demo.bean.BannersBean;
import com.example.mvp_demo.bean.GetDataType;
import com.example.mvp_demo.bean.UserArticle;
import com.example.mvp_demo.http.API;
import com.example.mvp_demo.module.adapter.ImageTitleNumAdapter;
import com.example.mvp_demo.module.webview.WebViewActivity;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yechaoa.yutils.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.indicator.CircleIndicator;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        getBanner();
        presenter.getUserArticleList(page, GetDataType.GETDATA);

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
            //给banner重新设置数据
            banner.setDatas(data);
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

    private Banner banner;
    private List<BannersBean.DataBean> data;

    private void getBanner() {
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL2)
                .build();
        //2.通过Retrofit实例创建接口服务对象
        API.WAZApi apiService = retrofit.create(API.WAZApi.class);
        //3.接口服务对象调用接口中方法，获得Call对象
        Call<ResponseBody> call = apiService.banners();
        //同步请求
        //Response<ResponseBody> bodyResponse = call.execute();
        //4.Call对象执行请求（异步、同步请求）
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Gson gson = new Gson();
                    BannersBean bannersBean = gson.fromJson(response.body().string(), BannersBean.class);
                    data = bannersBean.getData();
                    banner.setAdapter(new ImageTitleNumAdapter(MainActivity.this, data));
                    banner.removeIndicator();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public class ImageAdapter extends BannerAdapter<BannersBean.DataBean, ImageAdapter.BannerViewHolder> {

        public ImageAdapter(Context context, List<BannersBean.DataBean> mDatas) {
            //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
            super(mDatas);
        }

        //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
        @Override
        public ImageAdapter.BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            //注意，必须设置为match_parent，这个是viewpager2强制要求的
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ImageAdapter.BannerViewHolder(imageView);
        }

        @Override
        public void onBindView(ImageAdapter.BannerViewHolder holder, BannersBean.DataBean data, int position, int size) {
            Glide.with(MainActivity.this).load(data.getImage()).into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("link", data.getUrl());
                startActivity(intent);
            });
        }

        class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public BannerViewHolder(@NonNull ImageView view) {
                super(view);
                this.imageView = view;
            }
        }
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
