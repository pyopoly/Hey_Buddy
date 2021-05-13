package com.comp3617.finalProject.adapters


import android.icu.text.DateFormatSymbols
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.MDate
import com.comp3617.finalProject.ui.MainActivity
import com.comp3617.finalProject.ui.fragments.dialogs.DialogIconSelectMDate
import com.comp3617.finalProject.util.Weather
import com.comp3617.finalProject.util.WeatherName
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MDateAdapter(val activity: AppCompatActivity, var mDataSet: MutableList<MDate>) :
    RecyclerView.Adapter<MDateAdapter.MyViewHolder>() {
    lateinit var parentRecycler: RecyclerView
    var weatherName = mutableListOf<String?>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        parentRecycler = recyclerView
    }

    inner class MyViewHolder(
        act: AppCompatActivity,
        private val parentRecycler: RecyclerView,
        view: View
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val activity = act as MainActivity
        val vm = activity.mMainActivityViewModel
        val day: TextView = view.findViewById(R.id.id_day)
        val dayOfWeek: TextView = view.findViewById(R.id.id_dayOfweek)
        val month: TextView = view.findViewById(R.id.id_month)
        private val card: CardView = view.findViewById(R.id.id_dateCard)

        init {
            card.setOnClickListener(this)
            view.findViewById<ImageView>(R.id.id_dateIcon).setOnClickListener {
                val mDate = mDataSet[bindingAdapterPosition]
                Log.d("test", "clicked icon of block: ${mDate}")
                DialogIconSelectMDate(mDate).show(
                    activity.supportFragmentManager,
                    "IconSelectDialogMDate"
                )

            }
        }

        override fun onClick(view: View?) {
            Log.d("test", "clicked date: ${mDataSet[bindingAdapterPosition]}")
            vm.currentMDateId = mDataSet[bindingAdapterPosition].dateId
            parentRecycler.smoothScrollToPosition(bindingAdapterPosition)


            if (vm.innerGroup)
                vm.currentGroupId = vm.topGroupId
            vm.setBlocks(vm.currentMDateId)
            vm.setChildGroups()
            activity.findNavController(R.id.display_host_fragment)
                .navigate(R.id.action_displayFragment_self)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.views_date, parent, false)
        return MyViewHolder(activity, parentRecycler, view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val weatherIcon = holder.itemView.findViewById<ImageView>(R.id.id_weatherIcon)
        val reminderIcon = holder.itemView.findViewById<ImageView>(R.id.id_dateIcon)
        val mDate = mDataSet[position]
        val f = DateFormatSymbols.getInstance()
        val dw = f.weekdays[mDate.datOfWeek]
        val m = f.months[mDate.month - 1]
        holder.day.text = "${mDate.day}"
        holder.month.text = m.substring(0, 3)
        holder.dayOfWeek.text = dw.substring(0, 3)
        if (mDate.iconDrawableId == null)
            reminderIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    activity,
                    R.drawable.ic_baseline_add_24_dark
                )
            )
        else
            reminderIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    activity,
                    mDate.iconDrawableId!!
                )
            )

        Thread {
            val firstBlock =
                (activity as MainActivity).mMainActivityViewModel.findFirstBlockByDateID(mDate.dateId)
            if (firstBlock == null) {
                activity.runOnUiThread { weatherIcon.setImageDrawable(null) }
            } else {
                if (firstBlock.lat == null)
                    activity.runOnUiThread { weatherIcon.setImageDrawable(null) }
                else {
                    Weather(activity, weatherIcon, firstBlock).getAllWeather()
                }
            }
        }.start()
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }


}