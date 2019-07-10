package ru.macgavrina.digitalshowroom.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.catalog_item_list_item.view.*
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.model.CatalogItem
import ru.macgavrina.digitalshowroom.support.Log
import ru.macgavrina.digitalshowroom.view.ProductDetailsActivity

class CatalogRecyclerViewAdapter:
    RecyclerView.Adapter<CatalogRecyclerViewAdapter.ViewHolder>() {

    private var mItems: List<CatalogItem>? = null

    fun setCatalogItems(catalogItems: List<CatalogItem>?) {
        this.mItems = catalogItems
        notifyDataSetChanged()
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mainImage: ImageView = view.catalog_item_main_imageView
        val price: TextView = view.catalog_item_price_tv
        val brandName: TextView = view.catalog_item_brand_name_tv
        val productName: TextView = view.catalog_item_productname_tv
        val ratingBar: RatingBar = view.catalog_item_ratingBar
        val ratingText: TextView = view.catalog_item_rating_tv

        private var mItem: CatalogItem? = null

        init {
            view.setOnClickListener{v ->
                Log.d("onClick product with id = ${mItem?.id}, name = ${mItem?.name}")
                if (mItem != null && mItem?.id != null) {
                    val intent = Intent(v.context, ProductDetailsActivity::class.java)
                    intent.putExtra("productId", mItem!!.id)
                    v.context.startActivity(intent)
                }
            }
        }

        fun setItem(item: CatalogItem) {
            mItem = item
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CatalogRecyclerViewAdapter.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        // create a new view
        val view = layoutInflater.inflate(R.layout.catalog_item_list_item, parent, false)
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

        holder.brandName.text = item.brand.name

        Picasso.get()
            .load(item.image.link)
            .resize(300, 300)
            .centerCrop()
            .into(holder.mainImage)

        if (item.promoPrice != 0) {
            holder.price.text = MainApplication.applicationContext().getString(R.string.price_value, item.promoPrice.toString())
        } else {
            holder.price.text = MainApplication.applicationContext().getString(R.string.price_value, item.price.toString())
        }

        holder.productName.text = item.name
        holder.ratingBar.rating = item.review.rating
        holder.ratingText.text = MainApplication.applicationContext()
            .getString(R.string.rating_value, item.review.rating.toString())

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