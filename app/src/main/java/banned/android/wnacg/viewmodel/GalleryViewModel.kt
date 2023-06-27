package banned.android.wnacg.viewmodel

import androidx.lifecycle.liveData
import banned.android.wnacg.data.models.Manga
import banned.android.wnacg.data.repository.RetrofitClient
import kotlinx.coroutines.Dispatchers
import org.jsoup.Jsoup

class GalleryViewModel
{
    val galleryItems = liveData(Dispatchers.IO) {
        val response = RetrofitClient.api.getHtml()
        if (response.isSuccessful)
        {
            val html = response.body()?.string() ?: ""
            val items = parseHtml(html)
            emit(items)
        }
    }

    private suspend fun parseHtml(html: String): List<Manga>
    {
        val doc = Jsoup.parse(html)
        val elements = doc.select("li.gallary_item")
        val galleryItems = mutableListOf<Manga>()

        for (element in elements)
        {
            val imgSrc = element.select("div.pic_box > a > img").attr("src")
            val title = element.select("div.info > div.title > a").attr("title")
            val link = element.select("div.info > div.title > a").attr("href")
            val pictureCountStr = element.select("div.info > div.info_col").text().split("，")[0]
            val pictureCount = pictureCountStr.filter { it.isDigit() }.toInt()

            val galleryItem = Manga(title, imgSrc, pictureCount, link)
            galleryItems.add(galleryItem)
        }

        return galleryItems
    }
}