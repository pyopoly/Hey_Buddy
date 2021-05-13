package com.comp3617.finalProject.ui.fragments.dialogs

import android.icu.text.SimpleDateFormat
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.comp3617.finalProject.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class DatePicker(val activity: AppCompatActivity) {
    var dateStart = 0
    var monthStart = 0
    var yearStart = 0
    var numberOfDays = 0
    var formattedDate : String = ""


    init {
        setupDateRangePickerDialog()
    }

    private fun setupDateRangePickerDialog() {
        val picker = MaterialDatePicker.Builder.dateRangePicker().build()
        picker.addOnPositiveButtonClickListener {
            val startDate = it.first?.let { it1 -> offSetTimeZone(it1) }
            val endDate = it.second?.let { it1 -> offSetTimeZone(it1) }

            val formatDate =  SimpleDateFormat("MM dd yyyy", Locale.CANADA)
            val date = formatDate.format(startDate)
            val end = formatDate.format(endDate)
            showDateSelected(date, end)
            dateStart = date.substring(3,5).toInt()
            monthStart = date.substring(0,2).toInt()
            yearStart = date.substring(6).toInt()
            val d = endDate?.minus(startDate!!)
            val f =  SimpleDateFormat("dd", Locale.CANADA)
            numberOfDays = f.format(d).toInt() + 1
            formattedDate =  SimpleDateFormat("MMM dd, yyyy", Locale.CANADA).format(startDate)
            formattedDate = formattedDate.replace(".","")
        }
        picker.show(activity.supportFragmentManager, "DatePickerDialog")
    }

    private fun offSetTimeZone(date: Long): Long {
        val d = java.util.concurrent.TimeUnit.MILLISECONDS.convert(Date(date).timezoneOffset.toLong(), java.util.concurrent.TimeUnit.MINUTES)
        return date.plus(d)
    }

    private fun showDateSelected(start: String, end: String) {
        activity.findViewById<TextView>(R.id.id_date_chosen).text = "$start  -  $end"
    }
}