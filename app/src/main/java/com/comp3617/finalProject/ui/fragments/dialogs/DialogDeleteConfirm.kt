package com.comp3617.finalProject.ui.fragments.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.Group
import com.comp3617.finalProject.ui.MainActivity

/**
 * The Dialog that opens when user long clicks a Task, to confirm deletion of the Task. Task is
 * deleted in Database and also List of Tasks. RecyclerView is notified and updated.
 */
class DialogDeleteConfirm( private val dataSet: MutableList<Group>, private val adapterPosition: Int) : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = activity as Context
        val builder = AlertDialog.Builder(ctx)

        builder.setMessage(R.string.dialogDelete)
            .setPositiveButton(R.string.dialogDeleteConfirm
            ) { dialog, id ->
                Thread {
                    val group = dataSet[adapterPosition]
                    Log.d("test", "deleting ${group.title}")
                    if (ctx is MainActivity) {
                        ctx.mMainActivityViewModel.deleteGroup(group)
                    }
                }.start()
            }
            .setNegativeButton(R.string.dialogDeleteCancel
            ) { dialog, id ->
                // User cancels the dialog
                dialog.cancel()
            }
        // Create the AlertDialog object and return it
        return builder.create()
    }
}