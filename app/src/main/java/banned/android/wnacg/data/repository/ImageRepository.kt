package banned.android.wnacg.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import banned.android.wnacg.data.ImageDao
import banned.android.wnacg.data.models.ImageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ImageRepository(
    private val context: Context, private val imageService: ApiService, private val imageDao: ImageDao
                     )
{
    private fun downloadAndCacheImage(url: String, imageView: ImageView)
    {
        imageService.downloadImage(url).enqueue(object : Callback<ResponseBody>
                                                {
                                                    override fun onResponse(
                                                        call: Call<ResponseBody>, response: Response<ResponseBody>
                                                                           )
                                                    {
                                                        if (response.isSuccessful)
                                                        {
                                                            val inputStream = response.body()?.byteStream()
                                                            val file = File(context.cacheDir, url.hashCode().toString())
                                                            val outputStream = FileOutputStream(file)
                                                            inputStream?.copyTo(outputStream)
                                                            outputStream.close()

                                                            // 更新数据库
                                                            val imageEntity = ImageEntity(
                                                                url, file.absolutePath, System.currentTimeMillis() + MAX_LIVE_TIME
                                                                                         )

                                                            CoroutineScope(Dispatchers.IO).launch {
                                                                imageDao.insert(imageEntity)
                                                            }
                                                            // 更新ImageView
                                                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                                            imageView.setImageBitmap(bitmap)
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<ResponseBody>, t: Throwable
                                                                          )
                                                    {
                                                        // 处理错误
                                                    }
                                                })
    }

    fun getImage(url: String, imageView: ImageView)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val imageEntity = imageDao.find(url)
            if (imageEntity != null)
            {
                if (imageEntity.expiryTime > System.currentTimeMillis())
                {
                    // 更新ImageView
                    val bitmap = BitmapFactory.decodeFile(imageEntity.filePath)
                    imageView.setImageBitmap(bitmap)
                }
                else
                {
                    // 更新图片的有效期
                    val updatedImageEntity = imageEntity.copy(expiryTime = System.currentTimeMillis() + MAX_LIVE_TIME)
                    imageDao.update(updatedImageEntity)

                    // 更新ImageView
                    val bitmap = BitmapFactory.decodeFile(updatedImageEntity.filePath)
                    imageView.setImageBitmap(bitmap)
                }
            }
            else
            {
                downloadAndCacheImage(url, imageView)
            }
        }
    }


    companion object
    {
        const val MAX_LIVE_TIME = 24 * 60 * 60 * 1000 // 24小时
    }
}