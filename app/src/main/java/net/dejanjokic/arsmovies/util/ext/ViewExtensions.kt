package net.dejanjokic.arsmovies.util.ext

import android.view.View
import android.widget.ListView

fun ListView.setHeightBasedOnChildren() {
    var totalHeight = 0
    val desiredWidth = View.MeasureSpec.makeMeasureSpec(this.width, View.MeasureSpec.AT_MOST)
    for (i in 0 until adapter.count) {
        val listItem = adapter.getView(i, null, this)
        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
        totalHeight += listItem.measuredHeight
    }
    val params = this.layoutParams
    params.height = totalHeight + (this.dividerHeight * (adapter.count - 1))
    this.layoutParams = params
    this.requestLayout()
}