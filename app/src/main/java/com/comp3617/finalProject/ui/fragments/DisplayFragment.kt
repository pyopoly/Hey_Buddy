package com.comp3617.finalProject.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.comp3617.finalProject.R
import com.comp3617.finalProject.ui.MainActivity
import kotlinx.android.synthetic.main.display_main.*
import kotlin.random.Random


class DisplayFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.display_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vm = (activity as MainActivity).mMainActivityViewModel

        val array = resources.getStringArray(R.array.quotes)

        val string = array[Random.nextInt(array.size)]
        (activity as MainActivity).initRecyclerView(id_Recycler)
        view.findViewById<TextView>(R.id.id_quotesTitle_display).text = string

        if (vm.currentGroupId == null) {
            id_back_btn.visibility = View.INVISIBLE
        } else {
            id_back_btn.visibility = View.VISIBLE
        }

        id_back_btn.setOnClickListener {
            vm.displayGroupsByBackClick()
            findNavController().navigate(R.id.action_displayFragment_self)
        }
    }
}