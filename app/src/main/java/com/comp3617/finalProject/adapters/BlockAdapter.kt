package com.comp3617.finalProject.adapters

import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.comp3617.finalProject.R
import com.comp3617.finalProject.adapters.itemTouchHelper.ItemTouchHelperAdapter
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.ui.MainActivity
import com.comp3617.finalProject.ui.fragments.DisplayFragmentDirections
import com.comp3617.finalProject.ui.fragments.dialogs.DialogIconSelect
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.views_block.view.*

/**
 * Adapter for Blocks. Blocks can be swiped to be deleted, and long-clicked to be dragged and
 * dropped. Icons can be clicked to be changed. Clicking a Block can change its information.
 */
class BlockAdapter(private val activity: AppCompatActivity, blocks: MutableList<Block>) : RecyclerView.Adapter<BlockAdapter.MyViewHolder>(),
    ItemTouchHelperAdapter {
    var mData = blocks
    lateinit var mTouchHelper: ItemTouchHelper

    inner class MyViewHolder(activity: AppCompatActivity, view: View) : RecyclerView.ViewHolder(view),
        View.OnTouchListener,
        GestureDetector.OnGestureListener
    {
        private val mGestureDetector : GestureDetector = GestureDetector(itemView.context, this)
        private val vm = (activity as MainActivity).mMainActivityViewModel
        val cardView: CardView = view.findViewById(R.id.id_cardView_block)

        init {
            itemView.setOnTouchListener(this)

            //This is clicking the icon
            view.findViewById<ImageView>(R.id.id_blockIcon).setOnClickListener {
                val block = mData[bindingAdapterPosition]
                Log.d("test","clicked icon of block: ${block}")
                DialogIconSelect(block).show(activity.supportFragmentManager, "IconSelectDialog")
            }

            cardView.setOnLongClickListener {
                Log.d("test", "Long clicked: ${this.itemView.findViewById<TextView>(R.id.id_title_block).text}")
                return@setOnLongClickListener true
            }
        }

        /**
         * This is the onClickListener for Blocks
         */
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            //this is the onclick
            Log.d("test", "clicked itemview, ${mData[bindingAdapterPosition]}")
            vm.clickedBlock = mData[bindingAdapterPosition]
            val action = DisplayFragmentDirections.actionDisplayFragmentToAddBlockFragment2()
            activity.findNavController(R.id.display_host_fragment).navigate(action)
            activity.findViewById<FloatingActionButton>(R.id.id_fab).visibility = View.INVISIBLE
            return true
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            mGestureDetector.onTouchEvent(event)
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent?) {
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            activity.apply {}
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            mTouchHelper.startDrag(this)
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.views_block, parent, false)
        return MyViewHolder(activity, view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val block = mData[position]
        val iconView = holder.itemView.findViewById<ImageView>(R.id.id_blockIcon)
        if (block.icon != null ) {
            val drawableId = activity.resources.getIdentifier(mData[position].icon, "drawable", activity.packageName)
            if (drawableId != 0) {
                val d = ContextCompat.getDrawable(activity, drawableId)
                iconView.setImageDrawable(d)
            }
        } else {
            iconView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_add_24_dark))
        }

        val durationView = holder.itemView.findViewById<TextView>(R.id.id_blockDuration)
        if (!block.duration.isNullOrBlank()) {
            durationView.text = "${block.duration}hr"
        } else {
            durationView.text = null
        }

        //used in getMovementFlags in MyItemTouchHelper so only Groups can be swiped to delete
        holder.cardView.setTag(R.string.s_object, block)

        (activity as MainActivity).mMainActivityViewModel.clickedBlock = block
        holder.itemView.apply {
            id_title_block.text = block.title
            id_blockDescription_textV.text = block.description
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val fromBlock = mData[fromPosition]
        mData.remove(fromBlock)
        mData.add(toPosition, fromBlock)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int) {
        (activity as MainActivity).mMainActivityViewModel.deleteBlock(mData[position])
        notifyItemRemoved(position)
        mData.removeAt(position)
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.mTouchHelper = touchHelper
    }
}