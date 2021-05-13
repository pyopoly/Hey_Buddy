package com.comp3617.finalProject.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.Group
import com.comp3617.finalProject.ui.AddNewActivity
import com.comp3617.finalProject.ui.fragments.dialogs.DatePicker
import com.comp3617.finalProject.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_add_group.*


/**
 * This is the fragment in AddNewActivity to add a Group.
 */
class AddGroupFragment : Fragment() {
    lateinit var picker: DatePicker
    private var currentParent : Long? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_group, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentParent = (activity as AppCompatActivity).intent?.getLongExtra("currentGroupId", 0)
        if (currentParent != 0L) {
            id_datePicker_btn.visibility = View.GONE
            id_dateTtile_addGroup.visibility = View.GONE
        }


        id_save_group.setOnClickListener {
            val dateRange = id_date_chosen.text
            if (currentParent == 0L && dateRange.isNullOrEmpty())
                Toast.makeText(activity, "Top level Group must have date range specified", Toast.LENGTH_LONG).show()
            else {
                if(addGroup()) {
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                }
                else
                    Toast.makeText(activity, "Group title cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        id_datePicker_btn.setOnClickListener {
            picker = DatePicker(activity as AppCompatActivity)
        }

    }

    private fun addGroup(): Boolean {
        val title = "${id_group_title_editText.text}"
        val group = Group(title, null, null, currentParent)
        if(currentParent != 0L)
            (activity as AddNewActivity).vm.addGroup(group, null)
        else
            (activity as AddNewActivity).vm.addGroup(group, picker)

        Log.d("test", "added group: ${group}")
        return title.isNotEmpty()
    }
}