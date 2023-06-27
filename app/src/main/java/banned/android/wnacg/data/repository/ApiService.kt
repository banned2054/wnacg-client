package banned.android.wnacg.data.repository

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService
{
    @GET("search/index.php")
    fun search(
        @Query("q")
        query: String,
        @Query("m")
        m: String,
        @Query("syn")
        syn: String,
        @Query("f")
        f: String,
        @Query("s")
        s: String,
        @Query("p")
        p: Int
              ): Call<ResponseBody>

    @GET("photos-index-aid-{aid}.html")
    fun getPhotosIndex(
        @Path("aid")
        aid: Int
                      ): Call<ResponseBody>

    @GET("photos-view-id-{id}.html")
    fun getPhotosView(
        @Path("id")
        id: Int
                     ): Call<ResponseBody>

    @GET
    suspend fun downloadImage(
        @Url
        imageUrl: String
                             ): ResponseBody
}