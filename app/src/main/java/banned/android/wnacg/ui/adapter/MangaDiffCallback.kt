package banned.android.wnacg.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import banned.android.wnacg.data.models.Manga


class MangaDiffCallback(
        private val oldList : List<Manga>,
        private val newList : List<Manga>
                       ) : DiffUtil.Callback()
{

    override fun areItemsTheSame(oldItemPosition : Int, newItemPosition : Int) : Boolean
    {
        return oldList[oldItemPosition].mangaAid == newList[newItemPosition].mangaAid
    }

    override fun areContentsTheSame(oldItemPosition : Int, newItemPosition : Int) : Boolean
    {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size
}
