package com.comp3617.finalProject.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.Group
import com.comp3617.finalProject.models.MDate
import com.comp3617.finalProject.repositories.Repository
import com.google.android.gms.maps.model.LatLng

/**
 * Main ViewModel for the App. Bridge for all background data. Talks to Repository and Activities
 * and Fragments as a mediator. Mainly in charge of writing to database through Repository class.
 * Also have instance variables that are used to determine where the user is, and make UI changes
 * accordingly. currentGroupId and currentMDateId determine the where the user is in the app. When
 * groupId is null, it means user is at the top page.
 */
class MainActivityViewModel : ViewModel() {
    var mGroups: MutableLiveData<MutableList<Group>> = MutableLiveData(mutableListOf())
    var mBlocks: MutableLiveData<MutableList<Block>> = MutableLiveData(mutableListOf())
    var mDateList: MutableLiveData<MutableList<MDate>> = MutableLiveData(mutableListOf())
    var topGroupId: Long? = null
    var currentGroupId: Long? = null
    var currentMDateId: Long? = null
    var innerGroup : Boolean = false
    var latLngList : MutableList<LatLng?> = mutableListOf()
    lateinit var clickedBlock: Block

    /**
     * Init. Get Groups and Blocks.
     */
    fun init() {
        Thread {
            mGroups.postValue(Repository.getAllTopGroups())
            mBlocks.postValue(Repository.getChildBlocks(0, 0))
        }.start()
    }

    //////////////
    //Groups
    //////////////

    fun deleteGroup(group: Group) {
        Thread {
            Repository.deleteGroup(group)
            mGroups.postValue(Repository.getAllTopGroups())
        }.start()
    }

    fun setChildGroups() {
        val id = currentGroupId
        Log.d("test", "set child currentgorupid: $currentGroupId,  current dateid $currentMDateId")
        Thread {
            //when id is not id means user is inside another Group. Get the child elements
            //of this Group (by its GroupId).
            if (id != null) {
                val list = Repository.getAllChildGroups(id)
                for (x in list)
                    Log.d("test", "what is $x")
                mGroups.postValue(list)
            } else {
                //when id is null means user is on top/first page
                //init to refresh page
                init()
            }
        }.start()
    }

    fun findGroupByAttributes(group: Group): Group? {
        return Repository.findGroupByAttributes(group)
    }

    /**
     * When back arrow is clicked
     */
    fun displayGroupsByBackClick () {
        Thread {
            currentGroupId = currentGroupId?.let {
                val parentId = Repository.findParentIdByChildId(it)
                if ( parentId == 0L) null else parentId
            }
            if (currentGroupId == null) {
                clearDates()
                innerGroup = false
            }
            setChildGroups()
            clearBlocks()
            setBlocks(currentMDateId)
        }.start()
    }

    fun findGroupId(group: Group): Long {
        return Repository.findGroupId(group)
    }

    //A testing function
    fun testingGetAllGroups() {
        Thread {
            val groups = Repository.getAllGroups()
            mGroups.postValue(groups)
            for (g in groups) {
                Log.d("testall", "${g}")
            }
        }.start()
    }

    ////////////
    /////Blocks
    ///////////

    fun setBlocks(dateId: Long?) {
        val id = currentGroupId
        Thread {
            clearBlocks()
            if (id != null && dateId != null) {
                val list = Repository.getChildBlocks(id, dateId)
                mBlocks.postValue(list)
            }
        }.start()
    }

    private fun clearBlocks() {
        mBlocks.postValue(mutableListOf())
    }

    fun updateBlock(block: Block) {
        Thread {
            Repository.updateBlock(block)
            init()
        }.start()
    }

    fun deleteBlock(block: Block) {
        Thread {
            Repository.deleteBlock(block)
            mBlocks.value?.remove(block)
        }.start()
    }

    fun findFirstBlockByDateID(dateId: Long) : Block? {
        return currentGroupId?.let { Repository.findFirstBlock(it, dateId) }
    }

    //////////////////////////
    //Date
    ////////////

    fun setDates(parentGroupId: Long) {
        Thread {
            mDateList.postValue(Repository.getDates(parentGroupId).toMutableList())
        }.start()
    }

    private fun clearDates() {
        currentMDateId = null
        Thread {
            mDateList.postValue(mutableListOf())
        }.start()
    }

    fun updateDate(date: MDate) {
        Thread{
            Repository.updateDate(date)
            mDateList.postValue(currentGroupId?.let { Repository.getDates(it).toMutableList() })
        }.start()
    }
    ///////////////////////////
    ///Place
    ///////////////
}