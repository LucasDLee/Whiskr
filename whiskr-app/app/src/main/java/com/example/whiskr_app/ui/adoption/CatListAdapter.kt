package com.example.whiskr_app.ui.adoption

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.whiskr_app.R

class CatListAdapter (
    private val context: Context,
    private var catList: List<Cat>

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
        val view: View = View.inflate(context, R.layout.layout_cat_adoption_card, null)
        val name = view.findViewById<TextView>(R.id.layoutCatAdoptionCardName)
        val age = view.findViewById<TextView>(R.id.layoutCatAdoptionCardAge)
        val breed = view.findViewById<TextView>(R.id.layoutCatAdoptionCardBreed)
        val sex = view.findViewById<TextView>(R.id.layoutCatAdoptionCardSex)

        name.text = "Name: ".plus(catList[position].name)
        age.text = "Age: ".plus(catList[position].age)
        breed.text = "Breed: ".plus(catList[position].breed)
        sex.text = "Sex: ".plus(catList[position].sex)

        return view
    }

    fun updateCats(newList: List<Cat>) {
        catList = newList
    }
}