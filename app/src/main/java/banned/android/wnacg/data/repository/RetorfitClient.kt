package banned.android.wnacg.data.repository

import retrofit2.Retrofit

class RetrofitClient
{
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://wnacg.com/")
            .build()
    }

    public val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}