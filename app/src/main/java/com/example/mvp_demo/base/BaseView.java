package com.example.mvp_demo.base;

/**
 * Description : BaseView
 *
 * @author XuCanyou666
 * @date 2020/2/7
 */


public interface BaseView {

    void showLoading();

    void hideLoading();

    void onErrorCode(BaseBean bean);

}
