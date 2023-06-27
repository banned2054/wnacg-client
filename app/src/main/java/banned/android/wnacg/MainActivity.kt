package banned.android.wnacg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import banned.android.wnacg.ui.adapter.MangaAdapter
import banned.android.wnacg.viewmodel.GalleryViewModel

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

        mangaAdapter = MangaAdapter(emptyList())
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