package br.com.movies_tek.ui.main

import br.com.movies_tek.R
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

enum class SortOption(val value: String) {
    SORT_POPULARITY("popularity.desc"),
    SORT_RATING("vote_average.desc"),
    SORT_FAVORITE("favorite")
}

@PaperParcel
data class Sort(
        val option: SortOption,
        val title: String
) : PaperParcelable {
    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR = PaperParcelSort.CREATOR
    }

    override fun toString(): String {
        return title
    }
}

data class SortSelectionState(
        val sort: Sort,
        val sortPrev: Sort
)

fun makeSortOptions(getTitle: (Int) -> String): List<Sort> = listOf(
        Sort(SortOption.SORT_POPULARITY, getTitle(R.string.sort_popularity)),
        Sort(SortOption.SORT_RATING, getTitle(R.string.sort_rating)),
        Sort(SortOption.SORT_FAVORITE, getTitle(R.string.sort_favorite))
)
