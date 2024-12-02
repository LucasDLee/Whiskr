package com.example.whiskr_app.ui.adoption.fragment

import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.adapter.CatPictureAdapter
import com.example.whiskr_app.ui.adoption.model.AnimalData
import com.example.whiskr_app.ui.adoption.model.IncludedItem
import com.example.whiskr_app.ui.adoption.model.OrganizationAttributes

class DetailsActivity : AppCompatActivity() {
    private lateinit var adoptNowButton: Button
    private lateinit var description: TextView
    private lateinit var pictureSlider: RecyclerView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Set up the Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.AppBar)
        setSupportActionBar(toolbar)

        // Enable the back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set the back button click listener
        toolbar.setNavigationOnClickListener {
            finish() // Navigates back to the previous screen
        }

        description = findViewById(R.id.fragmentDetailsDescription)

        val animalData = intent.getParcelableExtra<AnimalData>("cat_detail")
        val includedData = intent.getParcelableArrayListExtra<IncludedItem>("cat_details_extra")

        val organizationData = animalData?.relationships?.orgs?.data
        val pictureData = animalData?.relationships?.pictures?.data
        val organizationId = organizationData?.get(0)?.id

        val pictureListId = ArrayList<String>()
        if (pictureData != null) {
            for (item in pictureData) {
                pictureListId.add(item.id)
            }
        }

        // Grab organization info
        val organization = createOrganizationDetails(includedData, organizationId!!)

        // Grab images
        val images = getImagesFromIncludedItem(includedData, pictureListId)
        val imageUrls = images.toList()

        // Set up picture slider
        pictureSlider = findViewById(R.id.detailsPictureSlide)
        pictureSlider.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        pictureSlider.adapter = CatPictureAdapter(imageUrls)

        val descriptionText = animalData.attributes.descriptionText
        if (descriptionText.isNullOrEmpty()) {
            description.text = "No Description Available"
        } else {
            description.text = descriptionText
        }

        adoptNowButton = findViewById(R.id.button_adopt_now)
        adoptNowButton.setOnClickListener {
            showAdoptionDialog(organization!!)
        }
    }

    /**
     * Builds the adoption dialog when a user chooses a cat to adopt
     */
    private fun showAdoptionDialog(organization: OrganizationAttributes) {
        val dialogBuilder = AlertDialog.Builder(this)

        val messageTextView = TextView(this).apply {
            text = """
                To complete your adoption request, please contact:
                ${organization.name ?: " "}
                ${organization.street ?: " Address Not Available"}
                ${organization.citystate ?: " "} ${organization.postalcode ?: " "} ${organization.country ?: " "}
                ${organization.phone ?: "Phone Number Not Available"}
                ${organization.url ?: "Url Not Available"}
            """.trimIndent()

            autoLinkMask = Linkify.WEB_URLS
            movementMethod = LinkMovementMethod.getInstance()
            setPadding(40, 20, 40, 20)
            textSize = 16f
        }

        // Build the dialog
        dialogBuilder.setTitle("Get in touch")
            .setView(messageTextView)
            .setPositiveButton("Done") { dialog, _ ->
                // Handle the confirmation action
                Toast.makeText(
                    this,
                    "Thank you for using Whiskr!",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }

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
                    val attributes = includedItem.attributes
                    return OrganizationAttributes(
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
                        type = attributes.type,
                        lat = attributes.lat,
                        lon = attributes.lon,
                        coordinates = attributes.coordinates,
                        citystate = attributes.citystate
                    )
                }
            }
        }
        return null
    }
}
