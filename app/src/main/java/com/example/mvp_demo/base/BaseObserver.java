package com.example.mvp_demo.base;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.example.mvp_demo.utils.LoadingDialog;
import com.google.gson.JsonParseException;
import com.yechaoa.yutils.YUtils;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

import static com.yechaoa.yutils.YUtils.getApplication;

/**
 * Description : BaseObserver
 *
 * @author XuCanyou666
 * @date 2020/2/7
 */


public abstract class BaseObserver<T> extends DisposableObserver<T> {

    protected BaseView view;

    @Override
    protected void onStart() {

    }

    @Override
    public void onNext(T o) {
        onSuccess(o);
    }

    @Override
    public void onError(Throwable e) {
        BaseException be;

        Log.i("Throwable", "onError: " + e);

        if (e != null) {
            //自定义异常
            if (e instanceof BaseException) {
                be = (BaseException) e;
                //回调到view层 处理 或者根据项目情况处理
                if (view != null) {
                    // 处理登录失效 更新
                    view.onErrorCode(new BaseBean(be.getErrorCode(), be.getErrorMsg()));
                } else {
                    onError(be.getErrorMsg());
                }
                //系统异常
            } else {
                if (e instanceof HttpException) {
                    //HTTP错误
                    if (((HttpException) e).code() == 401) {
                        be = new BaseException(BaseException.TOKEN_INVALIDATION, e);
                    } else {
                        be = new BaseException(BaseException.BAD_NETWORK_MSG, e);
                    }
                } else if (e instanceof ConnectException || e instanceof UnknownHostException) {
                    //连接错误
                    be = new BaseException(BaseException.CONNECT_ERROR_MSG, e);
                } else if (e instanceof InterruptedIOException) {
                    //连接超时
                    be = new BaseException(BaseException.CONNECT_TIMEOUT_MSG, e);
                } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
                    //解析错误
                    be = new BaseException(BaseException.PARSE_ERROR_MSG, e);
                } else {
                    be = new BaseException(BaseException.OTHER_MSG, e);
                }
            }
        } else {
            be = new BaseException(BaseException.OTHER_MSG);
        }
        onError(be.getErrorMsg());
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(T o);

    public abstract void onError(String msg);
}
