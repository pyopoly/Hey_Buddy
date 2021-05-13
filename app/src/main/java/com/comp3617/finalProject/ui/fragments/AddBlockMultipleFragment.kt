package com.comp3617.finalProject.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.ui.AddNewActivity
import com.comp3617.finalProject.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_add_block_multiple.*

/**
 * This is for adding multiple Blocks add the same time. Each new line will generate a new Block.
 * The Blocks will have nulls for all it's attributes.
 */
class AddBlockMultipleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_block_multiple, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id_cancelbtn_addMultiBlock.setOnClickListener {
            findNavController().navigate(R.id.action_addBlockMultipleFragment_to_addBlockFragment)
        }

        id_saveBtn_addMultiBlocks.setOnClickListener {

            if(addMultipleBlocks()) {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
            else
                Toast.makeText(activity, "title cannot be empty", Toast.LENGTH_LONG).show()
        }
    }


    private fun addMultipleBlocks() : Boolean {
        val currentGroupParent = (activity as AppCompatActivity).intent?.getLongExtra("currentGroupId", 0)
        val currentMDateParent = (activity as AppCompatActivity).intent?.getLongExtra("currentMDateId", 0)
        val titles = "${id_TitleMultiple_addMulti.text}"
        val listTitles = titles.lines()
        val blocks = mutableListOf<Block>()
        listTitles.forEach {
            if (it.isNotEmpty() and it.isNotBlank()) {
                blocks.add(Block(it, null, null, null, null,null, currentGroupParent!!, currentMDateParent!! ))
            }
        }
        (activity as AddNewActivity).vm.addMultiBlocks(blocks)
        Log.d("test", "added multiple groups with title: $blocks")
        return titles.isNotEmpty()
    }
}