package com.comp3617.finalProject.adapters.itemTouchHelper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.comp3617.finalProject.R
import com.comp3617.finalProject.adapters.BlockAdapter
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.ui.MainActivity

/**
 * ItemTouchHelper. Used mainly for Blocks in MainActivity. Handles swiping, dragging and dropping,
 * color changes. It is also shared by Blocks in the MapFragment, but most of the features are
 * disabled for
 */
class MyItemTouchHelper(val activity: MainActivity, private val adapter : ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    /**
     * Changing the color of the View when it is long-clicked and dragged. This only works for
     * Blocks in MainActivity.
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is BlockAdapter.MyViewHolder)
            viewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.lightRose))
    }

    /**
     * Change the color of Views back to original when dropped. This only works for Blocks in
     * MainActivity.
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            if (viewHolder is BlockAdapter.MyViewHolder)
                viewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.chip))
        }
    }

    /**
     * Handles what movements are allowed. Groups cannot be swiped. Only Blocks can be swiped.
     * Blocks in MapFragment can be moved left or right, which is detected by read the tag of
     * viewHolder.itemView.
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        var dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val item = viewHolder.itemView.getTag(R.string.s_object)
        var swipeFlags = 0
        if (item is Block) {
            swipeFlags = ItemTouchHelper.START
        }
        //This means the item is from the MapFragment. Changing swiping to left or right only.
        if (item is RecyclerView.ViewHolder) {
            dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemSwiped(viewHolder.adapterPosition)
    }

    /**
     * Drawing the background for the Views when swiped.
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        //Only apply this swiping style to Blocks
        if (viewHolder is BlockAdapter.MyViewHolder) {
            //Sets up background when View is Swiped left
            val background = ColorDrawable(Color.RED)
            background.setBounds(viewHolder.itemView.right + dX.toInt(), viewHolder.itemView.top, viewHolder.itemView.right, viewHolder.itemView.bottom)
            background.draw(c)
            //Below deals with the Trash Icon Image. Setting the size, and the position
            val icon = ContextCompat.getDrawable(activity, R.drawable.ic_baseline_delete_24)
            icon?. let {
                val top = viewHolder.itemView.top
                val bottom = viewHolder.itemView.bottom
                val verticalMargin = (bottom - top - icon.intrinsicHeight) /2
                val right = viewHolder.itemView.right - verticalMargin
                icon.setBounds(right - icon.intrinsicWidth, viewHolder.itemView.top + verticalMargin,
                    right , bottom - verticalMargin)
            }
            icon?.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}