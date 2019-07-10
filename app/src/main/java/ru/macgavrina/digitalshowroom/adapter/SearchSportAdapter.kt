package ru.macgavrina.digitalshowroom.adapter

import android.content.Context
import android.widget.ArrayAdapter


class SearchSportAdapter(context: Context, private val itemLayout: Int, private var items: List<String>) :
    ArrayAdapter<String>(context, itemLayout, items) {

}