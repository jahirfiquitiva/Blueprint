package jahirfiquitiva.libs.blueprint.quest.arctic

import android.support.annotation.Keep
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ArcticAPI {
    @Keep
    @Multipart
    @POST("./")
    fun sendRequest(
        @Part archive: MultipartBody.Part,
        @Part appsJson: MultipartBody.Part
                   ): Call<Void>
    
    companion object {
        fun connect(host: String, token: String): ArcticAPI {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("TokenID", token)
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "afollestad/arctic-icon-request")
                    .build()
                chain.proceed(request)
            }
            val rHost = if (host.endsWith("/")) host else "$host/"
            val retrofit = Retrofit.Builder()
                .baseUrl(rHost)
                .client(httpClient.build())
                .build()
            return retrofit.create(ArcticAPI::class.java)
        }
    }
}