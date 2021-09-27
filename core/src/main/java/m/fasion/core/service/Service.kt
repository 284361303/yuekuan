package m.fasion.core.service

import m.fasion.core.model.LoginModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Service {

    @POST("/api/auth/login")
    suspend fun getLogin(@Body maps: MutableMap<String, String>): Response<LoginModel>
}