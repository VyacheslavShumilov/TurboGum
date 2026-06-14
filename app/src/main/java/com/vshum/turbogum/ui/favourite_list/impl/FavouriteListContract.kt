package com.vshum.turbogum.ui.favourite_list.impl

import com.vshum.turbogum.model.LinersFavourite

/** A single grid cell: an owned sticker (with its favourite record) or an empty slot. */
data class CollectionSticker(
    val seriesKey: String,
    val number: Int,
    val favourite: LinersFavourite?
)

/** One series section: header text + its full grid of slots (owned + placeholders). */
data class CollectionSection(
    val seriesKey: String,
    val title: String,
    val stickers: List<CollectionSticker>
)

/** A filter chip: [key] null means "Все" (all series). */
data class ChipData(val key: String?, val label: String)

interface FavouriteListContract {
    interface View {
        fun onData(sections: List<CollectionSection>, chips: List<ChipData>)
        fun onEmpty()
    }

    interface Presenter {
        fun loadData()
        fun selectFilter(seriesKey: String?)
        fun onDestroy()
    }
}
