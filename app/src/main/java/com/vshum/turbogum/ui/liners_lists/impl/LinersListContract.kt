package com.vshum.turbogum.ui.liners_lists.contract

import com.vshum.turbogum.model.Liner

/**
 * Contract between [LinersListFragment] and its presenter.
 * Fragment renders the data and reacts to navigation events;
 * presenter loads stickers for the requested series key.
 */
interface LinersListContract {

    interface View {
        /** Called when the presenter has loaded stickers for the series. */
        fun onSuccessList(list: List<Liner>)
    }

    interface Presenter {
        /**
         * Start screen — load stickers for [seriesKey].
         * Examples: "series1", "super1", etc.
         */
        fun startScreen(seriesKey: String)
    }
}