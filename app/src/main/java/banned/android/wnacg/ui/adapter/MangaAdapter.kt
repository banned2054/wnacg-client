package banned.android.wnacg.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import banned.android.wnacg.R
import banned.android.wnacg.data.ImageDao
import banned.android.wnacg.data.models.Manga
import banned.android.wnacg.data.repository.ImageRepository

class MangaAdapter(
        private var list : List<Manga>,
        private val imageDao : ImageDao,
        private val imageRepository : ImageRepository
                  ) : RecyclerView.Adapter<MangaAdapter.MyViewHolder>()
{

    inner class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val textViewMangaTitle : TextView = view.findViewById(R.id.mangaTitle)
        val textViewMangaImageNumb : TextView = view.findViewById(R.id.mangaImageNumber)
        val imageViewMangeThumbnail : ImageView = view.findViewById(R.id.mangaThumbImage)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MyViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder : MyViewHolder, position : Int)
    {
        holder.textViewMangaTitle.text = list[position].title
        holder.textViewMangaImageNumb.text = list[position].imageNumbers.toString()
        val url = list[position].thumbnailSrc
        imageRepository.getImage(url, holder.imageViewMangeThumbnail)
    }

    override fun getItemCount() = list.size

    fun updateData(newList : List<Manga>)
    {
        val diffResult = DiffUtil.calculateDiff(MangaDiffCallback(list, newList))
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }
}
