package com.example.whiskr_app.ui.adoption.adapter

import android.content.Context
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.whiskr_app.R
import com.example.whiskr_app.ui.adoption.model.AnimalData

class CatListAdapter (
    private val context: Context,
    private var catList: List<AnimalData>

) : BaseAdapter() {

    override fun getItem(position: Int): Any {
        return catList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return catList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflate(context, R.layout.layout_cat_adoption_card, null)
        val picture = view.findViewById<ImageView>(R.id.layoutCatAdoptionCardImage)
        val id = view.findViewById<TextView>(R.id.layoutCatAdoptionCardId)
        val name = view.findViewById<TextView>(R.id.layoutCatAdoptionCardName)
        val age = view.findViewById<TextView>(R.id.layoutCatAdoptionCardAge)
        val breed = view.findViewById<TextView>(R.id.layoutCatAdoptionCardBreed)
        val sex = view.findViewById<TextView>(R.id.layoutCatAdoptionCardSex)

        val imageUrl = catList[position].attributes.pictureThumbnailUrl.toString()
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.cat_default)
            .into(picture)

        id.text = "Id: ".plus(catList[position].id)
        name.text = "Name: ".plus(catList[position].attributes.name)
        age.text = "Age: ".plus(catList[position].attributes.ageGroup)
        breed.text = "Breed: ".plus(catList[position].attributes.breedPrimary)
        sex.text = "Sex: ".plus(catList[position].attributes.sex)

        return view
    }

    fun updateCats(newList: List<AnimalData>) {
        catList = newList
    }
}