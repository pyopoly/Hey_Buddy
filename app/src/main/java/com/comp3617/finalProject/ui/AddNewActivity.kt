package com.comp3617.finalProject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.comp3617.finalProject.R
import com.comp3617.finalProject.viewmodels.AddNewActivityViewModel
import kotlinx.android.synthetic.main.activity_add_new.*

/**
 * Second Activity of the app. This is in charge of adding Group and Block to the database via
 * ViewModel, which communicates with Repository, which calls App.getDB. The View can be switched
 * between Group or Block. Titles are required. everything else can be null. A location is required
 * for Block in order for Weather forecast and Map to work. A Group that is on the first page must
 * have a Date range chosen.
 */
class AddNewActivity : AppCompatActivity() {
    lateinit var vm: AddNewActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)
        vm = ViewModelProvider(this).get(AddNewActivityViewModel::class.java)

        id_radioBtn_addGroup.isChecked = true
        id_add_radio_group.setOnCheckedChangeListener { _ , checkedId ->
            if (checkedId == R.id.id_radioBtn_addGroup)
                findNavController(R.id.nav_add_new_fragment).navigate(R.id.action_addBlockFragment_to_addGroupFragment)
            else
                findNavController(R.id.nav_add_new_fragment).navigate(R.id.action_addGroupFragment_to_addBlockFragment)
        }

        //Depending on where user accessed this AddNewActivity, elements will be changed.
        //If it's on page one, Group must have a Date Range and the radio group has Group
        //selected as default, else it is Block.
        val currentGroupParent = intent?.getLongExtra("currentGroupId", 0)
        if (currentGroupParent != 0L)
            id_radioBtn_addBlock.isChecked = true

        //Clicking the back arrow goes back to MainActivity
        id_back_addActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }
}