package com.comp3617.finalProject.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.comp3617.finalProject.R
import com.comp3617.finalProject.adapters.BlockAdapter
import com.comp3617.finalProject.adapters.BlockUpperPanelAdapter
import com.comp3617.finalProject.adapters.GroupAdapter
import com.comp3617.finalProject.adapters.MDateAdapter
import com.comp3617.finalProject.adapters.itemTouchHelper.MyItemTouchHelper
import com.comp3617.finalProject.viewmodels.MainActivityViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity. Consists of two interacting fragments showing different information. There are
 * only two activities in this appThis app is about organizing information for making an itinerary.
 * There are four data classes: Group, Block, MDate, MPlace. A Group is a folder that can contain
 * Blocks, and Group represent the top folder for starting a trip. It is like the cover of a book.
 * Blocks contain endpoint information for a point of interest. MDate is a Date object related to
 * Group. MPlace is a PLace object that is related to Block, such as the Point of Interest's
 * address, name, etc.
 * @author Jack Shih
 * @version Dec 5, 2020
 */
class MainActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGroupAdapter: GroupAdapter
    private lateinit var mDateAdapter: MDateAdapter
    private lateinit var mBlockAdapter: BlockAdapter
    private lateinit var mBlockUpperPanelAdapter: BlockUpperPanelAdapter
    lateinit var scrollView: DiscreteScrollView
    lateinit var mMainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        ////////////
        //Sets up ViewModel. This is the ViewModel for MainActivity and the majority of all fragment
        //and database communication is reliant on this ViewModel
        ////////////
        mMainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        if (mMainActivityViewModel.mGroups.value?.isEmpty()!!) {
            mMainActivityViewModel.init()
        }

        //this is a value to represent where the user's current location in order to change UI
        // elements accordingly
        mMainActivityViewModel.innerGroup = false

        //Sets up observers for ViewModel There are four mutableLiveData, one for each dataSet:
        //Group, Block, MDate, MPlace
        mMainActivityViewModel.mGroups.observe(this, {
            mGroupAdapter.mData = it
            mGroupAdapter.notifyDataSetChanged()
        })

        mMainActivityViewModel.mDateList.observe(this, {
            mDateAdapter.mDataSet = it
            mDateAdapter.notifyDataSetChanged()
        })

        mMainActivityViewModel.mBlocks.observe(this, {
            mBlockAdapter.mData = it
            mBlockAdapter.notifyDataSetChanged()
        })

        ///////////
        //Sets up ScrollView for MDates that go in the upper fragment of main activity
        ///////////
        scrollView = findViewById(R.id.id_date_scroll)
        scrollView.setItemTransformer(
            ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.75f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build()
        )

        mDateAdapter = MDateAdapter(this, mMainActivityViewModel.mDateList.value!!)
        scrollView.adapter = mDateAdapter


        //////////////
        ///Set the FAB button
        /////////////
        findViewById<FloatingActionButton>(R.id.id_fab).setOnClickListener { view ->
            val intent = Intent(this, AddNewActivity::class.java)
            intent.putExtra("currentGroupId", mMainActivityViewModel.currentGroupId)
            if (!mMainActivityViewModel.innerGroup)
                intent.putExtra("currentMDateId", mMainActivityViewModel.currentMDateId)
            startActivity(intent)
            this.finish()
        }
    }

    /**
     * Sets up recyclerview for the lower fragment. This is where Groups and Blocks go. Groups can
     * be clicked and deleted by being long-clicked. Blocks are deleted by being swiped. Groups
     * and Blocks share the same RecyclerView, but each have their own adapters. They are joined by
     * ConcatAdapter
     */
    fun initRecyclerView(recyclerView: RecyclerView) {
        val mManager = LinearLayoutManager(this)
        mGroupAdapter = GroupAdapter(this, mMainActivityViewModel.mGroups.value!!)
        mBlockAdapter = BlockAdapter(this, mMainActivityViewModel.mBlocks.value!!)

        //Sets up ItemTouchHelper for the RecyclerView
        val callback : ItemTouchHelper.Callback = MyItemTouchHelper(this, mBlockAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        mBlockAdapter.setTouchHelper(itemTouchHelper)

        //Concat two adapters for Group and Block
        val concatenated = ConcatAdapter(mGroupAdapter, mBlockAdapter)
        mRecyclerView = recyclerView.apply {
                setHasFixedSize(true) //RecyclerView size does not change
            layoutManager = mManager
            adapter = concatenated
            }
        itemTouchHelper.attachToRecyclerView(mRecyclerView)

        //Hide the FAB when the recyclerView is scrolling
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> id_fab.show()
                    RecyclerView.SCROLL_STATE_DRAGGING -> id_fab.hide()
                }
            }
        })
    }

    /**
     * Function to show Blocks in the upper fragment. This is for when MapFragment is called.
     * MDates are replaced by Blocks. Blocks cannot be clicked when in the upper panel, but they
     * can be dragged and dropped.
     */
    fun showBlocksInUpperPanel() {
        mBlockUpperPanelAdapter = BlockUpperPanelAdapter(
            this,
            mMainActivityViewModel.mBlocks.value!!
        )
        val callback: ItemTouchHelper.Callback = MyItemTouchHelper(
            this,
            mBlockUpperPanelAdapter
        )
        val itemTouchHelper = ItemTouchHelper(callback)
        mBlockUpperPanelAdapter.setTouchHelper(itemTouchHelper)
        scrollView.adapter = mBlockUpperPanelAdapter
        itemTouchHelper.attachToRecyclerView(scrollView)
    }


    //////////////////////////
    //ActionBar
    //////////////////////////

    /**
     * Creates an ActionBar
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Interaction with the Action Bar. Home Icon to refresh DisplayTasksActivity
     * Settings Icon to go to SettingsActivity
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.id_home_menu -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.id_map_menu -> {
                id_fab.hide()
                findNavController(R.id.display_host_fragment).navigate(R.id.action_displayFragment_to_mapsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}