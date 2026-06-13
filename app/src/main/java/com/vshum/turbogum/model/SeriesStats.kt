package com.vshum.turbogum.model

/**
 * Mapping between the Russian series label stored on [Liner]/[LinersFavourite] ("Серия 1",
 * "Super 1", ...), the Firestore field key ("series1", "super1", ...) and the total number
 * of stickers in that series. Mirrors WrappersListFragment.seriesEntries.
 */
object SeriesStats {

    data class SeriesStat(val ruName: String, val key: String, val label: String, val total: Int)

    val all = listOf(
        SeriesStat("Серия 1", "series1", "Серия 1", 50),
        SeriesStat("Серия 2", "series2", "Серия 2", 70),
        SeriesStat("Серия 3", "series3", "Серия 3", 70),
        SeriesStat("Серия 4", "series4", "Серия 4", 70),
        SeriesStat("Серия 5", "series5", "Серия 5", 70),
        SeriesStat("Super 1", "super1", "Super 1", 70),
        SeriesStat("Super 2", "super2", "Super 2", 70),
        SeriesStat("Super 3", "super3", "Super 3", 70),
        SeriesStat("Sport 1", "sport1", "Sport 1", 70),
        SeriesStat("Sport 2", "sport2", "Sport 2", 70),
        SeriesStat("Classic 1", "classic1", "Classic 1", 70),
        SeriesStat("Classic 2", "classic2", "Classic 2", 70)
    )

    const val TOTAL_PERCENT_KEY = "totalPercent"

    private val totalCount = all.sumOf { it.total }

    /**
     * Builds a Firestore update map: series1..classic2 percentages (0..100) plus
     * [TOTAL_PERCENT_KEY] computed as (sum of owned across all series) / (sum of totals) * 100.
     */
    fun percentMap(owned: List<LinersFavourite>): Map<String, Any> {
        val counts = owned.groupingBy { it.series.trim() }.eachCount()
        val result = HashMap<String, Any>()
        var ownedTotal = 0
        for (stat in all) {
            val count = counts[stat.ruName] ?: 0
            ownedTotal += count
            result[stat.key] = count * 100f / stat.total
        }
        result[TOTAL_PERCENT_KEY] = ownedTotal * 100f / totalCount
        return result
    }
}
