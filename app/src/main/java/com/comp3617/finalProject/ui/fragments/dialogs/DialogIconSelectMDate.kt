package com.comp3617.finalProject.ui.fragments.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.MDate
import com.comp3617.finalProject.ui.MainActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Used to change the icon of the MDate in the upper panel of MainActivity.
 */
class DialogIconSelectMDate(var date: MDate?) : DialogFragment() {
    var myView: View? = null //init in onCreateDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity as Context)
        myView = activity?.layoutInflater?.inflate(R.layout.dialog_icon_select, null)

        builder.setMessage("Select your Icon")
            .setPositiveButton(
                R.string.dialogDeleteConfirm
            ) { dialog, id ->
                val chipId = myView?.findViewById<ChipGroup>(R.id.id_chipGroup)?.checkedChipId
                if (chipId == -1) {
                    Toast.makeText(activity, "You must choose one Icon", Toast.LENGTH_LONG).show()
                    date?.let {
                        it.iconDrawableId = null
                        val vm = (activity as MainActivity).mMainActivityViewModel
                        vm.updateDate(it)
                    }
                } else {
                    val fileName = myView?.findViewById<Chip>(chipId!!)?.tag
                    val drawableId = resources.getIdentifier(
                        fileName as String?,
                        "drawable",
                        context?.packageName
                    )

                    date?.let {
                        it.iconDrawableId = drawableId
                        val vm = (activity as MainActivity).mMainActivityViewModel
                        vm.updateDate(it) //This will update block in DB

                    }
                }

            }
            .setNegativeButton(
                R.string.dialogDeleteCancel
            ) { dialog, id ->
                // User cancels the dialog
                dialog.cancel()
            }
        // Create the AlertDialog object and return it
        return builder.setView(myView).create()
    }
}
