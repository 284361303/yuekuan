package m.fasion.core.service

import m.fasion.core.model.BannerModel
import m.fasion.core.model.ClothesInfo
import m.fasion.core.model.ClothesList
import m.fasion.core.model.UserModel
import retrofit2.Response
import retrofit2.http.*

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

    /**
     * 退出登录
     */
    @POST("/api/auth/logout")
    suspend fun logout(): Response<Any>

    /**
     * banner
     * @param type  首页轮播传 Clothes_Banner   合集中的穿Clothes_Style
     */
    @GET("/api/topics")
    suspend fun getBanner(@Query("type") type: String): Response<BannerModel>

    /**
     * 首页款式列表
     * @param   sort    上新new 热度heat
     * @param   categoryId  分类id
     * @param   page    第几页
     * @param   size    条数
     */
    @GET("/api/clothes")
    suspend fun getClothesList(@Query("sort") sort: String, @Query("category_id") categoryId: String,
                               @Query("page") page: Int, @Query("size") size: Int): Response<ClothesList>

    /**
     * 款式详情
     */
    @GET("/api/clothes/{id}")
    suspend fun getClothesInfo(@Path("id") id: String): Response<ClothesInfo>

    /**
     * 根据合集id获取合集信息，如：首页banner跳转到高级西装搭配
     */
    @GET("/api/topics/{id}")
    suspend fun getTopics(@Path("id") id: String): Response<Any>

    /**
     * 收藏列表
     */
    @GET("/api/favorites/clothes")
    suspend fun getClothesList(): Response<Any>
}