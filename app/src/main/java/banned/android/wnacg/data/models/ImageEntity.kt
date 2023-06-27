package banned.android.wnacg.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey
    val imgSrc: String,
    val imgPath: String,
    val lastAccessTime: Long
                      )