package com.comp3617.finalProject.adapters

import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.comp3617.finalProject.R
import com.comp3617.finalProject.adapters.itemTouchHelper.ItemTouchHelperAdapter
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.repositories.Repository
import com.comp3617.finalProject.ui.MainActivity
import kotlinx.android.synthetic.main.views_block_upper_panel.view.*

class BlockUpperPanelAdapter(private val activity: AppCompatActivity, blocks: MutableList<Block>) : RecyclerView.Adapter<BlockUpperPanelAdapter.MyViewHolder>(),
        ItemTouchHelperAdapter {
    var mData = blocks
    lateinit var mTouchHelper: ItemTouchHelper
    lateinit var parentRecycler : RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        parentRecycler = recyclerView
    }

        inner class MyViewHolder(activity: AppCompatActivity, view: View, mAdapter: BlockUpperPanelAdapter) : RecyclerView.ViewHolder(view),
            View.OnTouchListener,
            GestureDetector.OnGestureListener
        {
            private val mGestureDetector : GestureDetector = GestureDetector(itemView.context, this)
            private val vm = (activity as MainActivity).mMainActivityViewModel

            init {
                itemView.setOnTouchListener(this)
            }

            /**
             * This is the onClickListener for Blocks
             */
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                //this is the onclick
                Log.d("test", "clicked itemview, ${mData[bindingAdapterPosition]}")
                parentRecycler.smoothScrollToPosition(bindingAdapterPosition)
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
                .inflate(R.layout.views_block_upper_panel, parent, false)
            return MyViewHolder(activity, view, this)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val block = mData[position]
            holder.itemView.apply {
                id_blockTitle_Upper.text = block.title
            }
            Thread {
                val place = Repository.findPlaceByParentId(block.blockId)
                val placeName = holder.itemView.findViewById<TextView>(R.id.id_placeName_upper)

                place?.let {
                    val text = "<a href='${place.WEBSITE_URI}'>${place.NAME}</a>"
                    activity.runOnUiThread {
                        placeName.text = null
                        placeName.isClickable = true
                        placeName.movementMethod = LinkMovementMethod.getInstance()
                        placeName.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
                    }

                }
            }.start()

            if (block.icon != null ) {
                val drawableId = activity.resources.getIdentifier(mData[position].icon, "drawable", activity.packageName)
                val iconView = holder.itemView.findViewById<ImageView>(R.id.id_icon_upperBlock)
                if (drawableId != 0) {
                    val d = ContextCompat.getDrawable(activity, drawableId)
                    iconView.setImageDrawable(d)
                } else {
                    iconView.setImageDrawable(null)
                }
            }

            holder.itemView.setTag(R.string.s_object, holder)

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