package ru.macgavrina.digitalshowroom.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sport_list_item.view.*
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.model.Sport
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import ru.macgavrina.digitalshowroom.repository.SportsRepository
import ru.macgavrina.digitalshowroom.support.Log
import ru.macgavrina.digitalshowroom.view.CatalogActivity


class SportRecyclerViewAdapter:
    RecyclerView.Adapter<SportRecyclerViewAdapter.ViewHolder>() {

    private var mItems: List<Sport>? = null

    fun setSports(sports: List<Sport>) {
        this.mItems = sports
        notifyDataSetChanged()
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val sportName: TextView = view.sport_list_item_name
        val sportIcon: ImageView = view.sport_list_item_icon_iv
        val cardLayout: CardView = view.sport_list_item_card_layout

        private var mItem: Sport? = null

        init {
            view.setOnClickListener{v ->
                Log.d("onClick sport with id = ${mItem?.id}, item = $mItem")
                if (mItem != null && mItem?.id != null) {
                    val intent = Intent(v.context, CatalogActivity::class.java)
                    intent.putExtra("sportSparkowId", mItem!!.sparkow_id)
                    intent.putExtra("sportNameRu", mItem!!.name.ru)
                    v.context.startActivity(intent)
                }
            }
        }

        fun setItem(item: Sport) {
            mItem = item
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SportRecyclerViewAdapter.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        // create a new view
        val view = layoutInflater.inflate(R.layout.sport_list_item, parent, false)
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

        if (!item.isActive) return

        holder.sportName.text = item.name.ru
        if (SportsRepository.getSportImageById(item.id) != null) {
            holder.sportIcon.setImageResource(SportsRepository.getSportImageById(item.id)!!)
        }

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