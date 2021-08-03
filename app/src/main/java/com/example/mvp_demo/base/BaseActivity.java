package com.example.mvp_demo.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewbinding.ViewBinding;

import com.example.mvp_demo.http.API;
import com.tencent.mmkv.MMKV;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * Description : BaseActivity
 *
 * @author XuCanyou666
 * @date 2020/2/7
 */

public abstract class BaseActivity<T extends ViewBinding, P extends BasePresenter> extends AppCompatActivity implements BaseView {

    protected P presenter;

    protected abstract P createPresenter();

    protected abstract void initView();

    protected abstract void initData();

    protected T viewBinding;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Class cls = (Class) type.getActualTypeArguments()[0];
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
            viewBinding = (T) inflate.invoke(null, getLayoutInflater());
            setContentView(viewBinding.getRoot());
            //设置竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            presenter = createPresenter();
            initView();
            initData();

            int iValue = API.kv.decodeInt("night_day");
            AppCompatDelegate.setDefaultNightMode(iValue);
        } catch (NoSuchMethodException | IllegalAccessException| InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时，解除绑定
        if (presenter != null) {
            presenter.detachView();
        }
    }

    protected void initListener() {
    }

    /**
     * 可以处理异常
     */
    @Override
    public void onErrorCode(BaseBean bean) {
    }

    /**
     * 启动activity
     *
     * @param activity 当前活动
     * @param isFinish 是否结束当前活动
     */
    public void startActivity(Class<?> activity, boolean isFinish) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }
}
