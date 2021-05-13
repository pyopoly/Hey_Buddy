package com.comp3617.finalProject.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.MPlace
import com.comp3617.finalProject.repositories.Repository
import com.comp3617.finalProject.ui.AddNewActivity
import com.comp3617.finalProject.ui.MainActivity
import com.comp3617.finalProject.ui.fragments.dialogs.DialogIconSelect
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_add_block.*

/**
 * This is the fragment in AddNewActivity to add a Block.
 */
class AddBlockFragment: Fragment() {
    var mPlace: MPlace? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_block, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the SDK
        activity?.applicationContext?.let { Places.initialize(
            it,
            "AIzaSyBw5M75oPEDzs_edLpc0685QwuPUrDJ2mY"
        ) }

        // Create a new PlacesClient instance
        val placesClient = activity?.let { Places.createClient(it) }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.RATING,
                Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER
            )
        )

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                mPlace =  MPlace(0, place.name, place.address, place.phoneNumber,
                    place.openingHours?.let{it.weekdayText.toString().substring(1)},
                    place.latLng?.latitude, place.latLng?.longitude, place.rating, place.websiteUri.toString())
            }

            override fun onError(status: Status) {
                Log.i("test autocomplete", "An error occurred: $status")
            }
        })

        id_iconBtn_addBlock.setOnClickListener {
            DialogIconSelect(null).show((activity as AddNewActivity).supportFragmentManager, "DialogIconSelect")
        }


        id_save_btn_addBlock.setOnClickListener {
            if(addBlock()) {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
            else
                Toast.makeText(activity, "title cannot be empty", Toast.LENGTH_LONG).show()
        }

        id_multipleBtn_addBlock.setOnClickListener {
            findNavController().navigate(R.id.action_addBlockFragment_to_addBlockMultipleFragment)
        }

        id_chipIcon_addBlock.setOnCloseIconClickListener {
            (it as Chip).text = null
            it.visibility = View.INVISIBLE
        }


        //View Block in MainActivity
        if (activity is MainActivity) {
            val block = (activity as MainActivity).mMainActivityViewModel.clickedBlock
            Thread {
                val place = Repository.findPlaceByParentId(block.blockId)
                autocompleteFragment.setText(place?.NAME)
            }.start()
            //View only. Disable editable elements
            (id_description_addBlock as EditText).inputType = InputType.TYPE_NULL
            (id_description_addBlock as EditText).isFocusable = false
            id_title_addBlock.inputType = InputType.TYPE_NULL
            id_title_addBlock.isFocusable = false
            id_duration_addBlock.inputType = InputType.TYPE_NULL
            id_iconBtn_addBlock.visibility = View.GONE
            id_edit_addBlock.visibility = View.VISIBLE
            id_chipIcon_addBlock.isCloseIconVisible = false
            id_chipIcon_addBlock.isClickable = false
            id_chipIcon_addBlock.setPadding(20, 0, 0, 0)
            autocompleteFragment.view?.findViewById<LinearLayout>(R.id.autocomplete_fragment)?.let {
                it.findViewById<AppCompatEditText>(R.id.places_autocomplete_search_input).isEnabled = false
                it.findViewById<AppCompatImageButton>(R.id.places_autocomplete_clear_button).isEnabled = false
                it.findViewById<AppCompatImageButton>(R.id.places_autocomplete_search_button).isEnabled = false
            }



            id_title_addBlock.setText(block.title)
            id_duration_addBlock.setText(block.duration)
            id_description_addBlock.setText((block.description))

            id_chipIcon_addBlock.apply {
                if (block.icon != null) {
                    val drawableId = resources.getIdentifier(block.icon, "drawable", context?.packageName)
                    val drawable = context?.let { ContextCompat.getDrawable(it, drawableId) }
                    chipIcon = drawable
                    visibility = View.VISIBLE
                }
            }

            id_multipleBtn_addBlock.visibility = View.INVISIBLE
            id_cancel_addBlock.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    findNavController().navigate(R.id.action_addBlockFragment2_to_displayFragment)
                    (activity as MainActivity).findViewById<FloatingActionButton>(R.id.id_fab).visibility = View.VISIBLE
                }
            }

            id_iconBtn_addBlock.setOnClickListener {
                (activity )?.supportFragmentManager?.let { it1 -> DialogIconSelect(block).show(it1, "DialogIconSelect") }
            }

        }
    }

    private fun addBlock(): Boolean {
        val currentGroupParent = (activity as AppCompatActivity)
            .intent?.getLongExtra("currentGroupId", 0)
        val currentMDateParent = (activity as AppCompatActivity)
            .intent?.getLongExtra("currentMDateId", 0)

        val title = "${id_title_addBlock.text}"
        val description = id_description_addBlock.text.let { if (it.isNullOrEmpty()) null else "$it"  }
        val duration = id_duration_addBlock.text.let { if (it.isNullOrEmpty()) null else "$it"  }
        val icon = id_chipIcon_addBlock.tag?.let { id_chipIcon_addBlock.tag as String }
        val block = Block(
            title,
            description,
            duration,
            icon,
            mPlace?.latitude,
            mPlace?.longitude,
            currentGroupParent!!,
            currentMDateParent!!
        )

        if (title.isNotEmpty())
            (activity as AddNewActivity).vm.addBlock(block, mPlace)
//        Log.d("test", "place added ${(mPlace as MPlace).NAME}")
        return title.isNotEmpty()
    }
}