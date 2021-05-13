package com.comp3617.finalProject.adapters.itemTouchHelper

interface ItemTouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemSwiped(position: Int)
}