package com.comp3617.finalProject.ui.fragments.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.MDate
import com.comp3617.finalProject.ui.AddNewActivity
import com.comp3617.finalProject.ui.MainActivity
import com.comp3617.finalProject.ui.fragments.AddBlockFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * The Dialog that opens when user long clicks a Group to confirm deletion. Group is
 * deleted in Database and also List of Groups. RecyclerView is notified and updated.
 * the param block is only needed if we want to update the icon attribute of block in DB
 */
class DialogIconSelect(var block: Block?) : DialogFragment(){
    var myView : View? = null //init in onCreateDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity as Context)
        myView = activity?.layoutInflater?.inflate(R.layout.dialog_icon_select, null)

        builder.setMessage("Select your Icon")
            .setPositiveButton(
                R.string.dialogDeleteConfirm
            ) { dialog, id ->
                val chipId = myView?.findViewById<ChipGroup>(R.id.id_chipGroup)?.checkedChipId
                if(chipId == -1) {
                    Toast.makeText(activity, "You must choose one Icon", Toast.LENGTH_LONG).show()
                } else {
                    val fileName = myView?.findViewById<Chip>(chipId!!)?.tag
                    val drawableId = resources.getIdentifier(fileName as String?, "drawable", context?.packageName)
                    val drawable = context?.let { ContextCompat.getDrawable(it, drawableId) }
                    if (activity is AddNewActivity) {
                        val addBlockFragment = (requireActivity().supportFragmentManager.fragments[0].childFragmentManager.fragments[0]) as AddBlockFragment
                        val chip = addBlockFragment.view?.findViewById<Chip>(R.id.id_chipIcon_addBlock)
                        (chip as Chip).chipIcon = drawable
                        chip.tag = fileName
                        chip.visibility = View.VISIBLE
                    } else if (activity is MainActivity) {
                        block?.let {
                            it.icon = fileName
                            val vm = (activity as MainActivity).mMainActivityViewModel
                            vm.updateBlock(it) //This will update block in DB
                        }
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