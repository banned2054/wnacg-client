package banned.android.wnacg.data.models

data class Manga
(
        val title : String,
        var thumbnailSrc : String,
        var thumbnailLocalPath : String,
        val imageNumbers : Int,
        val mangaAid : Int,
        val localImagePath : String
)
