package banned.android.wnacg.data

import androidx.room.Database
import androidx.room.RoomDatabase
import banned.android.wnacg.data.models.ImageEntity

@Database(entities = [ImageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun imageDao(): ImageDao
}