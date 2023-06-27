package banned.android.wnacg.data.repository

import retrofit2.Response
import retrofit2.http.GET

interface ApiService
{
    @GET("your_endpoint")
    suspend fun getHtml(): Response<String>
}