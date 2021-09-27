package m.fasion.core.base

import android.util.Log
import com.google.gson.internal.GsonBuildConfig
import m.fasion.core.BuildConfig
import m.fasion.core.Config.BASE_DEBUG_URL
import m.fasion.core.Config.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpUtils {

    private fun baseUrl(): String {
        return if (BuildConfig.DEBUG) {
            BASE_DEBUG_URL
        } else {
            BASE_URL
        }
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(getClient())
            .baseUrl(baseUrl())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            //设置网络超时
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            //错误重连
            .retryOnConnectionFailure(true)
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val build = request.newBuilder()
                    .header("Authorization", "aaa")
                    .method(request.method, request.body)
                    .build()
                return@Interceptor chain.proceed(build)
            }).addInterceptor(HttpLoggingInterceptor { message ->
                Log.e("", message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    companion object {
        private var mInstanse: HttpUtils? = null
        fun getInstanse(): HttpUtils =
            mInstanse ?: synchronized(HttpUtils::class.java) {
                mInstanse ?: HttpUtils().also {
                    mInstanse = it
                }
            }
    }
}