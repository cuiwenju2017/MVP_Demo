package com.example.mvp_demo.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;
import com.yechaoa.yutils.ActivityUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * Description : BaseFragment
 *
 * @author XuCanyou666
 * @date 2020/2/7
 */


public abstract class BaseFragment<T extends ViewBinding,P extends BasePresenter> extends Fragment implements BaseView {

    protected Context mContext;

    protected P presenter;

    protected abstract P createPresenter();

    protected abstract void initView();

    protected abstract void initData();

    protected T viewBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Class cls = (Class) type.getActualTypeArguments()[0];
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            viewBinding = (T) inflate.invoke(null, inflater, container, false);
            presenter = createPresenter();
            initView();
            initData();
        }  catch (NoSuchMethodException | IllegalAccessException| InvocationTargetException e) {
            e.printStackTrace();
        }
        return viewBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //销毁时，解除绑定
        if (presenter != null) {
            presenter.detachView();
        }
    }

    private void initListener() {
    }

    @Override
    public void onErrorCode(BaseBean bean) {
    }
}
