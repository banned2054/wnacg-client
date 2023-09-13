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
                                                        val a = 1
                                                    }
                                                })
    }

    fun getImage(url: String, imageView: ImageView)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val imageEntity = imageDao.find(url)

            //数据库中没有图片
            if (imageEntity == null)
            {
                downloadAndCacheImage(url, imageView)
                return@launch
            }

            val file = File(imageEntity.filePath)

            //数据库中有图片，但实际没有图片
            if (!file.exists())
            {
                downloadAndCacheImage(url, imageView)
                return@launch
            }

            //超时，重新下载图片
            if (imageEntity.expiryTime <= System.currentTimeMillis())
            {
                imageDao.delete(imageEntity)
                downloadAndCacheImage(url, imageView)
                return@launch
            }

            // 更新图片的有效期
            val updatedImageEntity = imageEntity.copy(expiryTime = System.currentTimeMillis() + MAX_LIVE_TIME)
            imageDao.update(updatedImageEntity)
            try
            {
                // 更新ImageView
                val bitmap = BitmapFactory.decodeFile(imageEntity.filePath)

                //如果成功读取图片，设置imageView
                if (bitmap != null)
                {
                    imageView.setImageBitmap(bitmap)
                }

                //图片文件存在，但不完整
                else
                {
                    file.delete()

                    imageDao.delete(updatedImageEntity)
                    downloadAndCacheImage(url, imageView)
                    return@launch
                }
            }
            catch (e: Exception)
            {
                file.delete()

                imageDao.delete(updatedImageEntity)
                downloadAndCacheImage(url, imageView)
            }

        }
    }


    companion object
    {
        const val MAX_LIVE_TIME = 24 * 60 * 60 * 1000 // 24小时
    }
}