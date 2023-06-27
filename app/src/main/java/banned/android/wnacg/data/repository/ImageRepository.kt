package banned.android.wnacg.data.repository

import banned.android.wnacg.data.ImageDao
import banned.android.wnacg.data.models.ImageEntity

class ImageRepository(
    private val apiService: ApiService,
    private val imageDao: ImageDao
                     )
{
    // 你的downloadImage方法等其他方法

    suspend fun findImageByAid(img_src: String): ImageEntity?
    {
        return imageDao.findImageByAid(img_src)
    }

    suspend fun insertImage(imageEntity: ImageEntity)
    {
        imageDao.insertImage(imageEntity)
    }

    suspend fun deleteImage(img_src: String)
    {
        imageDao.deleteImage(img_src)
    }
}