package ru.macgavrina.digitalshowroom.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.product_benefit_list_item.view.*
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.model.Characteristic

class ProductCharacteristicsRecyclerViewAdapter:
    RecyclerView.Adapter<ProductCharacteristicsRecyclerViewAdapter.ViewHolder>() {

    private var mItems: List<Characteristic>? = null

    fun setCharacteristicsList(characteristic: List<Characteristic>) {
        this.mItems = characteristic
        notifyDataSetChanged()
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.product_benefit_list_item_title_tv
        val desc: TextView = view.product_benefit_list_item_desc_tv

        private var mItem: Characteristic? = null

        init {
        }

        fun setItem(item: Characteristic) {
            mItem = item
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ProductCharacteristicsRecyclerViewAdapter.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        // create a new view
        val view = layoutInflater.inflate(R.layout.product_benefit_list_item, parent, false)
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

        holder.title.text = item.type
        holder.desc.text = item.value

        holder.setItem(mItems!![position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (mItems != null) {
            return mItems!!.size
        }
        return -1
    }
}