package com.example.mvp_demo.module.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mvp_demo.R;
import com.example.mvp_demo.bean.BannersBean;
import com.example.mvp_demo.module.home.MainActivity;
import com.example.mvp_demo.module.webview.WebViewActivity;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 自定义布局，图片+标题+数字指示器
 */
public class ImageTitleNumAdapter extends BannerAdapter<BannersBean.DataBean, ImageTitleNumAdapter.BannerViewHolder> {

    private Context context;

    public ImageTitleNumAdapter(Context context, List<BannersBean.DataBean> mDatas) {
        //设置数据，也可以调用banner提供的方法
        super(mDatas);
        this.context = context;
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        //注意布局文件，item布局文件要设置为match_parent，这个是viewpager2强制要求的
        //或者调用BannerUtils.getView(parent,R.layout.banner_image_title_num);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_image_title_num, parent, false);
        return new BannerViewHolder(view);
    }

    //绑定数据
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindView(BannerViewHolder holder, BannersBean.DataBean data, int position, int size) {
        Glide.with(context).load(data.getImage()).into(holder.imageView);
        holder.title.setText(data.getTitle());
        //可以在布局文件中自己实现指示器，亦可以使用banner提供的方法自定义指示器，目前样式较少，后面补充
        holder.numIndicator.setText((position + 1) + "/" + size);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("link", data.getUrl());
            context.startActivity(intent);
        });
    }


    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView numIndicator;

        public BannerViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            title = view.findViewById(R.id.bannerTitle);
            numIndicator = view.findViewById(R.id.numIndicator);
        }
    }

}
