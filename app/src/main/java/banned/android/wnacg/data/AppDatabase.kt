package banned.android.wnacg.data

import androidx.room.Database
import androidx.room.RoomDatabase
import banned.android.wnacg.data.models.ImageEntity

@Database(entities = [ImageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun imageCacheDao(): ImageDao
}
