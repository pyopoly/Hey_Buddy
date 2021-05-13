package com.comp3617.finalProject.viewmodels

import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import androidx.lifecycle.ViewModel
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.Group
import com.comp3617.finalProject.models.MDate
import com.comp3617.finalProject.models.MPlace
import com.comp3617.finalProject.repositories.Repository
import com.comp3617.finalProject.ui.fragments.dialogs.DatePicker


/**
 * ViewModel for AddNewActivity. Used by AddNewActivity to write to database.
 */
class AddNewActivityViewModel: ViewModel() {

    fun addGroup(group: Group, picker: DatePicker?) {
        val list = listOf(group)
        Thread {
            if (picker != null) {
                list[0].date = picker.formattedDate
                Repository.addGroup(list)
                val parentId = Repository.findGroupId(group) //this is for finding groupID for MDates
                addDates(picker, parentId)
            } else {
                Repository.addGroup(list)
            }
        }.start()
    }

    fun addBlock(block: Block, place: MPlace?) {
        val list = listOf(block)
        Thread {
            Repository.addBlock(list)
            if (place != null) {
                val id = Repository.findBlockId(block)
                Repository.addPlace(place, id)
            }

        }.start()
    }

    fun addMultiBlocks(blocks: MutableList<Block>) {
        Thread {
            Repository.addBlock(blocks)
        }.start()
    }

    /**
     * Add MDates to database. Dates are added automatically, by using the date range the user
     * chose with DatePicker
     */
    private fun addDates(picker: DatePicker, parentId: Long) {
        val n: Int = picker.numberOfDays
        val dateList = mutableListOf<MDate>()
        val cal= GregorianCalendar(picker.yearStart, picker.monthStart - 1, picker.dateStart - 1)
        for (i in 1..n) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val d: Int = cal.get(Calendar.DAY_OF_MONTH)
            val m: Int = cal.get(Calendar.MONTH) + 1
            val y: Int = cal.get(Calendar.YEAR)
            val dw: Int = cal.get(Calendar.DAY_OF_WEEK)
            dateList.add(MDate(d, m, y, dw, null, parentId))
        }
        addMDatesToDB(dateList)
    }

    /**
     * Assistance function
     */
    private fun addMDatesToDB(list: MutableList<MDate>) {
        Thread {
            Repository.addDate(list)
        }.start()
    }
}