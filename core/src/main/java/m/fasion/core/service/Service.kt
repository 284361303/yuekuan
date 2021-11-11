package m.fasion.core.service

import m.fasion.core.model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Service {

    /**
     * 登录
     */
    @POST("/api/auth/login")
    suspend fun getLogin(@Body maps: MutableMap<String, String>): Response<UserModel>

    /**
     * 发送验证码
     */
    @POST("/api/auth/code")
    suspend fun sendCode(@Body maps: MutableMap<String, String>): Response<Any>
}