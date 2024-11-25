package com.example.whiskr_app.ui.adoption.fragment

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.model.AnimalData

class DetailsFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var imageView2: ImageView
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

        imageView = view.findViewById(R.id.fragmentDetailsImageOne)
        imageView2 = view.findViewById(R.id.fragmentDetailsImageTwo)
        description = view.findViewById(R.id.fragmentDetailsDescription)


        val animalData = arguments?.getParcelable("cat_detail", AnimalData::class.java)
        val imageUrl = animalData?.attributes?.pictureThumbnailUrl
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.cat_default)
            .into(imageView)

        Glide.with(this)
            .load(R.drawable.cat_default)
            .into(imageView2)

        val descriptionText = animalData?.attributes?.descriptionHtml
        if (descriptionText.isNullOrEmpty()) {
            // Set to "No description available" and center it
            description.text = Html.fromHtml(
                "<div style=\"text-align: center;\">No description available</div>",
                Html.FROM_HTML_MODE_COMPACT or Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV)
        } else {
            // Set the description HTML
            description.text = Html.fromHtml(
                descriptionText,
                Html.FROM_HTML_MODE_COMPACT or Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
            )
        }

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