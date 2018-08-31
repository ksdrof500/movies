package br.com.movies_tek.ui.main.view

import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class MainItemPadding(private val itemPadding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State?
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val spanCount = getSpanCount(parent)
        val column = position % spanCount

        outRect.left = if (column == 0) itemPadding else itemPadding / 2
        outRect.right = if (column + 1 == spanCount) itemPadding else itemPadding / 2
        if (position < spanCount) {
            outRect.top = itemPadding
        }
        outRect.bottom = itemPadding
    }

    private fun getSpanCount(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as? GridLayoutManager
                ?: throw RuntimeException("Only supports GridLayoutManager!")

        return layoutManager.spanCount
    }
}
