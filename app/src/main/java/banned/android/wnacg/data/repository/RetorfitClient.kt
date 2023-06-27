package banned.android.wnacg.data.repository

import retrofit2.Retrofit

object RetrofitClient
{

    private val pageRetrofit by lazy {
        Retrofit.Builder().baseUrl("https://wnacg.com/").build()
    }

    val pageApi: ApiService by lazy {
        pageRetrofit.create(ApiService::class.java)
    }

}