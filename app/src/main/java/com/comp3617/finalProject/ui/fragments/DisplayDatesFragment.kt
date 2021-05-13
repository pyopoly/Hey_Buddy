package com.comp3617.finalProject.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.comp3617.finalProject.R

/**
 * Simple fragment for the upper panel of MainActivity to display Dates
 */
class DisplayDatesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_date_display, container, false)
    }
}