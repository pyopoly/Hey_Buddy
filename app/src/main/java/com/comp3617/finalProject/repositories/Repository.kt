package com.comp3617.finalProject.repositories

import com.comp3617.finalProject.App
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.Group
import com.comp3617.finalProject.models.MDate
import com.comp3617.finalProject.models.MPlace


/**
 * Used to talk to Database
 */
object Repository {

    //////////////////Group///////////
    fun getAllGroups() : MutableList<Group> {
        return App.getDB().db.groupDao().allGroups.toMutableList()
    }

    fun getAllTopGroups() : MutableList<Group> {
        return App.getDB().db.groupDao().allTopLevelGroups.toMutableList()
    }

    fun getAllChildGroups(id: Long) : MutableList<Group> {
        return App.getDB().db.groupDao().findChildrenByParentGroupId(id).toMutableList()
    }

    fun findParentIdByChildId(id: Long) : Long? {
        return App.getDB().db.groupDao().findParentIdByChildId(id)
    }

    fun findGroupId(group: Group) : Long {
        val title = group.title
        val description = group.description
        val date = group.date
        val parentGroupId = group.parentGroupId
        return App.getDB().db.groupDao().findId(title, description, date, parentGroupId)
    }

    fun findGroupByAttributes(group: Group) : Group? {
        val title = group.title
        val description = group.description
        val date = group.date
        val parentGroupId = group.parentGroupId
        return App.getDB().db.groupDao().findGroupByAttributes(title, description, date, parentGroupId)
    }

    fun addGroup(groups: List<Group>) {
        App.getDB().db.groupDao().insertAll(groups)
    }

    fun deleteGroup(group: Group) {
        App.getDB().db.groupDao().delete(group)
    }

    ////////////Block/////////
    fun addBlock(list: List<Block>) {
        App.getDB().db.blockDao().insertAll(list)
    }

    fun getChildBlocks(id: Long, dateId: Long) : MutableList<Block> {
        return App.getDB().db.blockDao().getChildBlocks(id, dateId).toMutableList()
    }

    fun updateBlock(block: Block) {
        App.getDB().db.blockDao().update(block)
    }

    fun deleteBlock(block: Block) {
        App.getDB().db.blockDao().delete(block)
    }

    fun findFirstBlock(groupId: Long, dateId: Long): Block? {
        return App.getDB().db.blockDao().findFirstBlock(groupId, dateId)
    }

    fun findBlockId(block: Block) : Long {
        val title = block.title
        val description = block.description
        val duration = block.duration
//        val placeId = block.placeId
        val lat = block.lat
        val lng = block.lng
        val icon = block.icon
        val belongsToGroupId = block.belongsToGroupId
        val belongsToDateId = block.belongsToDateId

        return App.getDB().db.blockDao().findBlockId(title, description, duration, icon, lat, lng, belongsToGroupId, belongsToDateId)
    }

    ////////////MDate////////////
    fun getDates(parentGroupId: Long): MutableList<MDate> {
        return App.getDB().db.mDateDao().findChildrenByParentMDateId(parentGroupId).toMutableList()
    }

    fun addDate(dates: List<MDate>) {
        App.getDB().db.mDateDao().insertAll(dates)
    }

    fun updateDate(date: MDate) {
        App.getDB().db.mDateDao().update(date)
    }

    ///////////////MPLace/////
    fun addPlace(place: MPlace, parentBlockId: Long) {
        place.parentBlockId = parentBlockId
        App.getDB().db.mPlaceDao().insertOne(place)
    }

    fun findPlaceByParentId(blockId: Long): MPlace? {
        return App.getDB().db.mPlaceDao().findByParentId(blockId)
    }

//    fun findPlaceByLatLng(latLng: String): MPlace? {
////        val place = App.getDB().db.mPlaceDao().findPlaceByLatLng(latLng)
////        if (place == null) Log.d("test", "Repo returned null for MPlace")
////        return place
//    }

}