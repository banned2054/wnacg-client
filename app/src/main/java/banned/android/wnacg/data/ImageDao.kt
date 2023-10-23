package banned.android.wnacg.data

import androidx.room.*
import banned.android.wnacg.data.models.ImageEntity

@Dao
interface ImageDao
{
    @Query("SELECT * FROM ImageEntity WHERE url = :url")
    fun find(url : String) : ImageEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(image : ImageEntity)

    @Delete
    fun delete(image : ImageEntity)

    @Update
    fun update(image : ImageEntity)
}