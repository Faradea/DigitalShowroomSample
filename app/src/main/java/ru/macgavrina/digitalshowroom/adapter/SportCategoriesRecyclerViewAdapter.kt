package ru.macgavrina.digitalshowroom.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sport_category_list_item.view.*
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.model.SportCategoryWithSelectedMarker
import ru.macgavrina.digitalshowroom.support.Log

class SportCategoriesRecyclerViewAdapter(inputOnClickListener: OnSportCategoryClickListener):
    RecyclerView.Adapter<SportCategoriesRecyclerViewAdapter.ViewHolder>() {

    private var mItems: List<SportCategoryWithSelectedMarker>? = null
    private val mOnClickListener: OnSportCategoryClickListener = inputOnClickListener

    fun setSports(sportCategories: List<SportCategoryWithSelectedMarker>) {
        this.mItems = sportCategories
        notifyDataSetChanged()
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val categoryName: TextView = view.sport_catergory_list_item_categoryname
        val categoryLayout: LinearLayout = view.sport_category_list_item_layout

        private var mItem: SportCategoryWithSelectedMarker? = null

        init {
        }

        fun setItem(item: SportCategoryWithSelectedMarker) {
            mItem = item
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SportCategoriesRecyclerViewAdapter.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        // create a new view
        val view = layoutInflater.inflate(R.layout.sport_category_list_item, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return ViewHolder(view)
    }


    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (mItems == null) return

        val item = mItems?.get(position) ?: return

        if (position == 0) {
            holder.categoryName.text = MainApplication.applicationContext().resources.getString(R.string.categories_first_item)
        } else {
            holder.categoryName.text = item.sportCategory.name
        }

        if (item.isSelected) {
            holder.categoryLayout.setBackgroundResource(R.color.colorButton)
            holder.categoryName.setTextColor(MainApplication.applicationContext().resources.getColor(R.color.whiteBackground))
        } else {
            holder.categoryLayout.setBackgroundResource(R.color.categories_background)
            holder.categoryName.setTextColor(MainApplication.applicationContext().resources.getColor(R.color.text_color))
            //holder.categoryName.setTextColor(R.color.text_color)
        }

        holder.setItem(mItems!![position])

        holder.categoryLayout.setOnClickListener {
            Log.d("onClick category with id = ${item.sportCategory.id} and name = ${item.sportCategory.name}")
            mOnClickListener.onCategoryClick(item.sportCategory.id)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (mItems != null) {
            return mItems!!.size
        }
        return -1
    }

    interface OnSportCategoryClickListener {
        fun onCategoryClick(selectedCategorySparkowId: Int)
    }
}