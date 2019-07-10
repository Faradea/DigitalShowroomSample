package ru.macgavrina.digitalshowroom.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_image_list_item.view.*
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.model.ImageWithSelectedMarker
import ru.macgavrina.digitalshowroom.support.Log

class ProductImagesRecyclerViewAdapter(inputOnClickListener: OnProductImageClickListener):
    RecyclerView.Adapter<ProductImagesRecyclerViewAdapter.ViewHolder>() {

    private var mItems: List<ImageWithSelectedMarker>? = null
    private val mOnClickListener: OnProductImageClickListener = inputOnClickListener

    fun setImagesList(images: List<ImageWithSelectedMarker>) {
        this.mItems = images
        notifyDataSetChanged()
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = view.product_image_list_item_imageView

        private var mItem: ImageWithSelectedMarker? = null

        init {
        }

        fun setItem(item: ImageWithSelectedMarker) {
            mItem = item
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ProductImagesRecyclerViewAdapter.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        // create a new view
        val view = layoutInflater.inflate(R.layout.product_image_list_item, parent, false)
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

        Picasso.get()
            .load(item.image.link)
            .resize(125, 125)
            .centerCrop()
            .into(holder.image)

        holder.setItem(mItems!![position])

        holder.image.setOnClickListener {
            Log.d("onClick image with id = ${item.image.id}, isSelected = ${item.isSelected}, link = ${item.image.link}")
            mOnClickListener.onImageClick(item.image.link, item.image.id)
        }

        if (item.isSelected) {
            holder.image.setBackgroundResource(R.drawable.image_border_active)
        } else {
            holder.image.setBackgroundResource(R.drawable.image_border)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (mItems != null) {
            return mItems!!.size
        }
        return -1
    }

    interface OnProductImageClickListener {
        fun onImageClick(imageUrl: String, imageId: Int)
    }
}