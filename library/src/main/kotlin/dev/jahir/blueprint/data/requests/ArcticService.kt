package dev.jahir.blueprint.data.requests

import dev.jahir.blueprint.data.models.ArcticResponse
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ArcticService {
    @Headers("Accept: application/json", "User-Agent: afollestad/icon-request")
    @Multipart
    @POST("v1/request")
    suspend fun uploadRequest(
        @Header("TokenID") TokenID: String,
        @Part("apps") apps: String,
        @Part archive: MultipartBody.Part
    ): ArcticResponse
}