package banned.android.wnacg.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ImageEntity(
    @PrimaryKey
    val url: String,
    val filePath: String,
    val expiryTime: Long
                      )
