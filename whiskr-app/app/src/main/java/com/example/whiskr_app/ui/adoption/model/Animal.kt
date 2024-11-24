package com.example.whiskr_app.ui.adoption.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnimalResponse(
    val data: List<AnimalData>,
    val included: List<IncludedItem>
) : Parcelable

@Parcelize
data class AnimalData(
    val type: String,                   // Type of resource, e.g., "animals"
    val id: String,                     // Unique ID for the animal
    val attributes: AnimalAttributes,   // Detailed attributes about the animal
    val relationships: AnimalRelationships? // Relationships to other resources
) : Parcelable


@Parcelize
data class AnimalAttributes(
    val activityLevel: String?,            // Activity level of the animal
    val adoptedDate: String?,              // Date when the animal was adopted
    val adoptionFeeString: String?,        // Adoption fee
    val isAdoptionPending: Boolean,        // Whether the adoption is pending
    val ageGroup: String?,                 // Age group (e.g., "Baby")
    val ageString: String?,                // Age description
    val birthDate: String?,                // Birth date
    val isBirthDateExact: Boolean,         // Whether the birth date is exact
    val breedString: String?,              // Combined breed description
    val breedPrimary: String?,             // Primary breed
    val breedSecondary: String?,           // Secondary breed
    val isCatsOk: Boolean?,                // Whether the animal is okay with cats
    val coatLength: String?,               // Coat length
    val descriptionHtml: String?,          // Description in HTML format
    val descriptionText: String?,          // Plain text description
    val isHousetrained: Boolean?,          // Whether the animal is housetrained
    val indoorOutdoor: String?,            // Indoor or outdoor preference
    val isKidsOk: Boolean?,                // Whether the animal is okay with kids
    val name: String,                      // Name of the animal
    val pictureThumbnailUrl: String?,      // Thumbnail URL for the picture
    val sex: String?,                      // Sex (e.g., "Male", "Female")
    val sizeGroup: String?,                // Size group (e.g., "Medium")
    val url: String?,                      // URL for more details
    val isSpecialNeeds: Boolean?,          // Whether the animal has special needs
    val updatedDate: String?               // Last updated date
) : Parcelable

@Parcelize
data class AnimalRelationships(
    val breeds: RelationshipData?,
    val colors: RelationshipData?,
    val patterns: RelationshipData?,
    val species: RelationshipData?,
    val statuses: RelationshipData?,
    val fosters: RelationshipData?,
    val locations: RelationshipData?,
    val orgs: RelationshipData?,
    val pictures: RelationshipData?
) : Parcelable

@Parcelize
data class RelationshipData(
    val data: List<RelatedItem>
) : Parcelable

@Parcelize
data class RelatedItem(
    val type: String, // Type of the related item
    val id: String    // ID of the related item
) : Parcelable

@Parcelize
data class IncludedItem(
    val type: String,
    val id: String,
    val attributes: IncludedAttributes,
    val links: Links
) : Parcelable

@Parcelize
data class IncludedAttributes(
    val name: String? = null,
    val singular: String? = null,
    val plural: String? = null,
    val youngSingular: String? = null,
    val youngPlural: String? = null,
    val description: String? = null,
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val citystate: String? = null,
    val postalcode: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val services: String? = null,
    val url: String? = null,
    val facebookUrl: String? = null,
    val order: Int? = null,
    val created: String? = null,
    val updated: String? = null,
    val coordinates: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val resolutionX: Int? = null,
    val resolutionY: Int? = null,
    val filesize: Int? = null,
    val imageUrl: String? = null
) : Parcelable

@Parcelize
data class Links(
    val self: String
) : Parcelable

@Parcelize
data class Picture(
    val original: ImageSize,
    val large: ImageSize,
    val small: ImageSize
) : Parcelable

@Parcelize
data class ImageSize(
    val filesize: Int,
    val resolutionX: Int,
    val resolutionY: Int,
    val url: String
) : Parcelable

