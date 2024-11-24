package com.example.whiskr_app.ui.adoption.fragment

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.model.AnimalData

class DetailsFragment : Fragment() {
    private lateinit var adoptNowButton: Button
    private lateinit var description: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        description = view.findViewById(R.id.fragmentDetailsDescription)


        val animalData = arguments?.getParcelable("cat_detail", AnimalData::class.java)

        description.text = Html.fromHtml(animalData?.attributes?.descriptionHtml, Html.FROM_HTML_MODE_COMPACT)

        adoptNowButton = view.findViewById(R.id.button_adopt_now)
        adoptNowButton.setOnClickListener {
            showAdoptionDialog()
        }
    }

    private fun showAdoptionDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Get in touch")
            .setMessage("To complete your adoption request, please contact adoptacat@whiskr.com\n")
            .setPositiveButton("Done") { dialog, _ ->
                // Handle the confirmation action (e.g., update database, send request)
                Toast.makeText(
                    requireContext(),
                    "Thank you for using Whiskr!",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
        // Show the dialog
        dialogBuilder.create().show()
    }
}