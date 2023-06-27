package banned.android.wnacg.data

import androidx.room.*
import banned.android.wnacg.data.models.ImageEntity

@Dao
interface ImageDao
{
    @Query("SELECT * FROM images WHERE imgSrc = :img_src")
    suspend fun findImageByAid(img_src: String): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageEntity: ImageEntity)

    @Query("DELETE FROM images WHERE imgSrc = :img_src")
    suspend fun deleteImage(img_src: String)
}

