package com.example.mvp_demo.http;

import com.example.mvp_demo.base.BaseBean;
import com.example.mvp_demo.bean.UserArticle;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Description : API
 * 接口的管理类
 *
 * @author XuCanyou666
 * @date 2020/2/7
 */
public class API {

    public static final String BASE_URL = "https://www.wanandroid.com/";

    public static final String BASE_URL2 = "https://gank.io/";

    public interface WAZApi {
        // 广场列表数据
        @GET("user_article/list/{page}/json")
        Observable<BaseBean<UserArticle>> getUserArticleList(@Path("page") Integer page);

        //干货banners图
        @GET("api/v2/banners")
        Call<ResponseBody> banners();
    }
}
