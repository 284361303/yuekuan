package m.fasion.core.service

import m.fasion.core.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

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
    suspend fun getClothesList(@Query("sort") sort: String, @Query("categoryIds") categoryId: MutableList<String>,
                               @Query("page") page: Int, @Query("size") size: Int): Response<ClothesList>

    /**
     * 关键字搜索
     * @param   sort    上新new 热度heat
     * @param   categoryId  分类id
     * @param   page    第几页
     * @param   size    条数
     */
    @GET("/api/clothes")
    suspend fun getSearchList(@Query("s") searchName: String): Response<ClothesList>

    /**
     * 款式详情
     */
    @GET("/api/clothes/{id}")
    suspend fun getClothesInfo(@Path("id") id: String): Response<ClothesInfo>

    /**
     * 根据合集id获取合集信息，如：首页banner跳转到高级西装搭配、今日推荐页面列表数据
     */
    @GET("/api/topics/{id}")
    suspend fun getTopics(@Path("id") id: String): Response<RecommendList>

    /**
     * 收藏列表
     */
    @GET("/api/favorites/clothes")
    suspend fun getFavoritesList(@Query("id") id: String): Response<FavoritesListData>

    /**
     * 收款某一款式
     * @param clothesId 款式id
     */
    @PUT("/api/favorites/clothes/{clothes_id}")
    suspend fun addFavorites(@Path("clothes_id") clothesId: String): Response<String>

    /**
     * 取消收藏
     * @param clothesId 款式id
     */
    @DELETE("/api/favorites/clothes/{clothes_id}")
    suspend fun cancelFavorites(@Path("clothes_id") clothesId: String): Response<String>

    /**
     * 获取用户资料
     */
    @GET("/api/me")
    suspend fun getMe(): Response<UserInfo>

    /**
     * 更新用户资料
     */
    @PATCH("/api/me")
    suspend fun editMe(@Body maps: MutableMap<String, Any>): Response<UserInfo>

    /**
     * 上传图片
     */
    @Multipart
    @PUT("/api/upload/avatar")
    suspend fun uploadImage(@Part part: MultipartBody.Part): Response<String>

    /**
     * 意见反馈
     */
    @POST("/api/feedback")
    suspend fun feedBack(@Body maps: MutableMap<String, Any>): Response<Any>

    /**
     * 筛选条件返回
     */
    @GET("/api/categories")
    suspend fun getCategories(): Response<Categories>
}