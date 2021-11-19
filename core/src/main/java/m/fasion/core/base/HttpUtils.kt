package m.fasion.core.base

import android.util.Log
import m.fasion.core.BuildConfig
import m.fasion.core.Config.BASE_DEBUG_URL
import m.fasion.core.Config.BASE_URL
import m.fasion.core.util.SPUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
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
                    .header("Authorization", if (SPUtil.getToken().isNullOrEmpty()) "" else SPUtil.getToken()!!)
                    .method(request.method, request.body)
                    .build()
                try {
                    return@Interceptor chain.proceed(build)
                } catch (e: Exception) {
                    var msg = ""
                    var interceptorCode = 0
                    when (e) {
                        is SocketTimeoutException -> {
                            msg = "请求超时"
                            interceptorCode = 408
                        }
                        is IOException -> {
                            msg = "服务器文件上传错误"
                            interceptorCode = 408
                        }
                        else -> {
                            msg = "服务器异常"
                            interceptorCode = 404
                        }
                    }
                    val responseBody = JSONObject(mapOf("message" to msg)).toString().toResponseBody(null)
                    return@Interceptor Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_2)
                        .code(interceptorCode).message(msg)
                        .body(responseBody).build()
                }
            }).addInterceptor(HttpLoggingInterceptor { message ->
                Log.e("请求结果", message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY)).build()
    }

    companion object {
        private var mInstanse: HttpUtils? = null
        fun getInstances(): HttpUtils =
            mInstanse ?: synchronized(HttpUtils::class.java) {
                mInstanse ?: HttpUtils().also {
                    mInstanse = it
                }
            }
    }
}