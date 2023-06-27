package banned.android.wnacg.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import banned.android.wnacg.data.models.Manga
import banned.android.wnacg.data.repository.ApiService
import banned.android.wnacg.data.repository.RetrofitClient
import kotlinx.coroutines.Dispatchers
import org.jsoup.Jsoup
import retrofit2.Retrofit
import java.io.File
import java.io.IOException

class GalleryViewModel : ViewModel()
{
    private val searchQuery = MutableLiveData("無修正")
    private val searchPage = MutableLiveData(1)
    val galleryItems = liveData(Dispatchers.IO) {
        val query = searchQuery.value ?: return@liveData
        val page = searchPage.value ?: return@liveData
        try
        {
            val response =
                RetrofitClient.pageApi.search(query, "", "yes", "_all", "create_time_DESC", page)
                    .execute()
            if (response.isSuccessful)
            {
                val html = response.body()?.string() ?: ""
                val items = parseHtml(html)
                Log.i("GalleryViewModel", "get ${items.size} manga")
                emit(items)
            } else
            {
                // 打印错误的 HTTP 状态码和消息
                Log.e(
                    "GalleryViewModel",
                    "HTTP error, code = ${response.code()}, message = ${response.message()}"
                     )
                val items: List<Manga> = emptyList()
                emit(items)
            }
        } catch (e: IOException)
        {
            // 网络请求失败，打印错误信息
            e.printStackTrace()

        }
    }

    private fun parseHtml(html: String): List<Manga>
    {
        val doc = Jsoup.parse(html)
        val elements = doc.select("li.gallary_item")
        val galleryItems = mutableListOf<Manga>()

        for (element in elements)
        {
            val imgSrc = "https:" + element.select("div.pic_box > a > img").attr("src")
            val title = element.select("div.info > div.title > a").attr("title").replace("<em>", "")
                .replace("</em>", "")
            val linkStr = element.select("div.info > div.title > a").attr("href")
                .replace("photos-index-aid-", "").replace(".html", "")
            val linkAid = linkStr.filter { it.isDigit() }.toInt()
            val pictureCountStr =
                element.select("div.info > div.info_col").text().split("，")[0].replace("張圖片", "")
            val pictureCount = pictureCountStr.filter { it.isDigit() }.toInt()

            val galleryItem = Manga(title, imgSrc, pictureCount, linkAid, "")
            galleryItems.add(galleryItem)
        }

        return galleryItems
    }

    suspend fun downloadImage(imageUrl: String, destination: File)
    {
        val apiService = Retrofit.Builder().build().create(ApiService::class.java)
        val responseBody = apiService.downloadImage(imageUrl)
        destination.outputStream().use { outputStream ->
            responseBody.byteStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    fun search(query: String? = null, page: Int? = null)
    {
        if (query != null)
        {
            searchQuery.value = query
        }
        if (page != null)
        {
            searchPage.value = page
        }
    }
}