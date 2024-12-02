package com.example.whiskr_app.ui.adoption.fragment

import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.adapter.CatPictureAdapter
import com.example.whiskr_app.ui.adoption.model.AnimalData
import com.example.whiskr_app.ui.adoption.model.IncludedItem
import com.example.whiskr_app.ui.adoption.model.OrganizationAttributes

class DetailsFragment : Fragment() {
    private lateinit var adoptNowButton: Button
    private lateinit var description: TextView
    private lateinit var pictureSlider: RecyclerView

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
        val includedData = arguments?.getParcelableArrayList(
            "cat_details_extra", IncludedItem::class.java
        )

        val organizationData = animalData?.relationships?.orgs?.data
        val pictureData = animalData?.relationships?.pictures?.data
        val organizationId = organizationData?.get(0)?.id

        val pictureListId = ArrayList<String>()
        if (pictureData != null) {
            for (item in pictureData) {
                pictureListId.add(item.id)
            }
        }

        // grab organization info
        val organization = createOrganizationDetails(includedData, organizationId!!)

        // grab images
        val images = getImagesFromIncludedItem(includedData, pictureListId)
        val imageUrls = images.toList()

        // Set up picture slide view
        pictureSlider = view.findViewById(R.id.detailsPictureSlide)
        pictureSlider.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        pictureSlider.adapter = CatPictureAdapter(imageUrls)

        val descriptionText = animalData.attributes.descriptionText
        if (descriptionText.isNullOrEmpty()) {
            // Set to "No description available" and center it
            description.text = "No Description Available"
        } else {
            // Set the description HTML
            description.text = descriptionText
        }

        adoptNowButton = view.findViewById(R.id.button_adopt_now)
        adoptNowButton.setOnClickListener {
            showAdoptionDialog(organization!!)
        }
    }

    /**
     * Builds the adoption dialog when a user chooses a cat to adopt
     */
    private fun showAdoptionDialog(organization: OrganizationAttributes) {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // Create a TextView to display the message
        val messageTextView = TextView(requireContext()).apply {
            text = """
                To complete your adoption request, please contact:
                ${organization.name ?: " "}
                ${organization.street ?: " Address Not Available"}
                ${organization.citystate ?: " "} ${organization.postalcode ?: " "} ${organization.country ?: " "}
                ${organization.phone ?: "Phone Number Not Available"}
                ${organization.url ?: "Url Not Available"}
            """.trimIndent()

            // Make links clickable
            autoLinkMask = Linkify.WEB_URLS
            movementMethod = LinkMovementMethod.getInstance()

            // Add padding and styling
            setPadding(40, 20, 40, 20)
            textSize = 16f
        }

        // Build the dialog
        dialogBuilder.setTitle("Get in touch")
            .setView(messageTextView)
            .setPositiveButton("Done") { dialog, _ ->
                // Handle the confirmation action
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

    /**
     * Generates the image for each cat from the API
     */
    private fun getImagesFromIncludedItem(
        includedData: ArrayList<IncludedItem>?,
        pictureListId: ArrayList<String>
    ): ArrayList<String?> {
        val imageOriginalUrlList = ArrayList<String?>()
        if (includedData != null) {
            for (includedItem in includedData) {
                if (includedItem.type == "pictures" && includedItem.id in pictureListId) {
                    if (!includedItem.attributes.small?.url.isNullOrEmpty()) {
                        imageOriginalUrlList.add(includedItem.attributes.original?.url)
                    }
                }
            }
        }
        return imageOriginalUrlList
    }

    /**
     * Formats the organization's details from the API
     */
    private fun createOrganizationDetails(
        includedData: ArrayList<IncludedItem>?,
        organizationId: String
    ): OrganizationAttributes? {
        if (includedData != null) {
            for (includedItem in includedData) {
                if (includedItem.type == "orgs" && includedItem.id == organizationId) {
                    val organizationAttributes = includedItem.attributes.let { attributes ->
                        // Map the attributes to OrganizationAttributes
                        OrganizationAttributes(
                            name = attributes.name,
                            street = attributes.street,
                            city = attributes.city,
                            state = attributes.state,
                            postalcode = attributes.postalcode,
                            country = attributes.country,
                            phone = attributes.phone,
                            url = attributes.url,
                            facebookUrl = attributes.facebookUrl,
                            services = attributes.services,
                            type = attributes.type,  // Assuming 'type' field is not in IncludedAttributes
                            lat = attributes.lat,
                            lon = attributes.lon,
                            coordinates = attributes.coordinates,
                            citystate = attributes.citystate
                        )
                    }
                    return organizationAttributes
                }
            }
        }
        return null
    }
}