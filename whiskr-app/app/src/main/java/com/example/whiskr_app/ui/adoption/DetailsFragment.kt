package com.example.whiskr_app.ui.adoption

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.whiskr_app.R

class DetailsFragment : Fragment() {
    private lateinit var adoptNowButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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