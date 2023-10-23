package banned.android.wnacg.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import banned.android.wnacg.data.models.Manga
import banned.android.wnacg.data.repository.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.IOException


class GalleryViewModel : ViewModel()
{
    private fun parseHtml(html : String) : List<Manga>
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
            val thumbnailImagePath = ""

            val galleryItem = Manga(title, imgSrc, thumbnailImagePath, pictureCount, linkAid, "")
            galleryItems.add(galleryItem)
        }

        return galleryItems
    }

    private val nowPage = 1
    private val searchQuery = MutableLiveData("無修正")
    private val searchPage = MutableLiveData(1)
    val galleryItems = MutableLiveData<List<Manga>>()

    fun search(query : String? = null, page : Int? = null)
    {
        if (query != null)
        {
            searchQuery.value = query
        }
        if (page != null)
        {
            searchPage.value = page
        }

        val query = searchQuery.value ?: return
        val page = searchPage.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try
            {
                val response = RetrofitClient.pageApi.search(
                        query,
                        "",
                        "yes",
                        "_all",
                        "create_time_DESC",
                        page
                                                            ).execute()
                if (response.isSuccessful)
                {
                    val html = response.body()?.string() ?: ""
                    val items = parseHtml(html)
                    Log.i("GalleryViewModel", "get ${items.size} manga")
                    val currentItems = galleryItems.value ?: emptyList()
                    galleryItems.postValue(currentItems + items)
                }
                else
                {
                    Log.e(
                            "GalleryViewModel",
                            "HTTP error, code = ${response.code()}, message = ${response.message()}"
                         )
                }
            }
            catch (e : IOException)
            {
                e.printStackTrace()
            }
        }
    }

}