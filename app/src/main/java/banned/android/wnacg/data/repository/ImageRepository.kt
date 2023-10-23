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
import java.io.InputStream

class ImageRepository(
        private val context : Context,
        private val imageService : ApiService,
        private val imageDao : ImageDao
                     )
{

    fun getImage(url : String, imageView : ImageView)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val imageEntity = imageDao.find(url)
            if (imageEntity == null || ! File(imageEntity.filePath).exists() || imageEntity.expiryTime <= System.currentTimeMillis())
            {
                handleImageNotInCache(url, imageView, imageEntity)
                return@launch
            }
            updateImageExpiryAndSetImageView(url, imageView, imageEntity)
        }
    }

    private suspend fun handleImageNotInCache(
            url : String,
            imageView : ImageView,
            imageEntity : ImageEntity?
                                             )
    {
        imageEntity?.let { imageDao.delete(it) }
        downloadAndCacheImage(url, imageView)
    }

    private suspend fun updateImageExpiryAndSetImageView(
            url : String,
            imageView : ImageView,
            imageEntity : ImageEntity
                                                        )
    {
        val updatedImageEntity =
            imageEntity.copy(expiryTime = System.currentTimeMillis() + MAX_LIVE_TIME)
        imageDao.update(updatedImageEntity)
        val bitmap = try
        {
            BitmapFactory.decodeFile(imageEntity.filePath)
        }
        catch (e : Exception)
        {
            handleImageNotInCache(url, imageView, updatedImageEntity)
            return
        }
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap)
        }
        else
        {
            handleImageNotInCache(url, imageView, updatedImageEntity)
        }
    }

    private inner class ImageDownloadCallback(
            private val url : String,
            private val imageView : ImageView
                                             ) : Callback<ResponseBody>
    {
        override fun onResponse(call : Call<ResponseBody>, response : Response<ResponseBody>)
        {
            response.body()?.byteStream()?.let { inputStream ->
                handleInputStream(inputStream, url, imageView)
            }
        }

        override fun onFailure(call : Call<ResponseBody>, t : Throwable)
        {
            // Handle failure
        }
    }

    private fun handleInputStream(inputStream : InputStream, url : String, imageView : ImageView)
    {
        val file = File(context.cacheDir, url.hashCode().toString())
        FileOutputStream(file).apply {
            inputStream.copyTo(this)
            close()
        }
        val imageEntity =
            ImageEntity(url, file.absolutePath, System.currentTimeMillis() + MAX_LIVE_TIME)
        CoroutineScope(Dispatchers.IO).launch {
            imageDao.insert(imageEntity)
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun downloadAndCacheImage(url : String, imageView : ImageView)
    {
        imageService.downloadImage(url).enqueue(ImageDownloadCallback(url, imageView))
    }

    companion object
    {
        const val MAX_LIVE_TIME = 24 * 60 * 60 * 1000 // 24小时
    }
}
