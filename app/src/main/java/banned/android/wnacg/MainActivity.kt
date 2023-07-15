package banned.android.wnacg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import banned.android.wnacg.data.AppDatabase
import banned.android.wnacg.data.repository.ApiService
import banned.android.wnacg.data.repository.ImageRepository
import banned.android.wnacg.ui.adapter.MangaAdapter
import banned.android.wnacg.viewmodel.GalleryViewModel
import retrofit2.Retrofit

class MainActivity : AppCompatActivity()
{

    private lateinit var viewModel: GalleryViewModel
    private lateinit var mangaAdapter: MangaAdapter
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.mangaRecyclerView)

        viewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        val db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "database-name"
                                     ).build()
        val imageDao = db.imageDao()
        val retrofit = Retrofit.Builder().baseUrl("https://localhost/").build() // 使用一个占位符作为 base URL.build()

        val imageService = retrofit.create(ApiService::class.java)
        val imageRepository = ImageRepository(this, imageService, imageDao)

        mangaAdapter = MangaAdapter(emptyList(), imageDao, imageRepository)
        recyclerView.adapter = mangaAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.galleryItems.observe(this, Observer { items ->
            // 当 items 更新时，调用 Adapter 的 updateData 方法来更新 RecyclerView 的内容
            mangaAdapter.updateData(items)
        })

        // 在一开始，你可以使用默认的搜索参数来执行搜索
        viewModel.search()
    }
}