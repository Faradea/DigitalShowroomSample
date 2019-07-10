package ru.macgavrina.digitalshowroom.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.paginator_list_item.view.*
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.model.*
import ru.macgavrina.digitalshowroom.support.Log


class PaginatorAdapter(
    pageNumbers: MutableList<PageInPaginator>?, inputOnClickListener: OnPaginatorClickListener
) :
    RecyclerView.Adapter<PaginatorAdapter.ViewHolder>() {

    private val mItems: List<PageInPaginator>? = pageNumbers
    private val mOnClickListener: OnPaginatorClickListener = inputOnClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var pageNumberText: TextView = view.paginator_list_item_page_number_tv
        var pageNumberLayout: ConstraintLayout = view.paginator_list_item_layout

        private var mItem: PageInPaginator? = null

        init {
        }

        fun setItem(item: PageInPaginator) {
            mItem = item
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaginatorAdapter.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        // create a new view
        val view = layoutInflater.inflate(ru.macgavrina.digitalshowroom.R.layout.paginator_list_item, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return ViewHolder(view)
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val item = mItems?.get(position) ?: return

        holder.pageNumberText.text = item.text

        holder.pageNumberLayout.setOnClickListener {

            Log.d("onClick page with text = ${item.text}")

            if (item.isCurrentPage) return@setOnClickListener

            if (item.type == PAGINATOR_TYPES_DIVIDER) return@setOnClickListener

            if (item.type == PAGINATOR_TYPES_REALPAGE) {
                mOnClickListener.onPageNumberClick(item.text.toInt() - 1)
                return@setOnClickListener
            }

            if (item.type == PAGINATOR_TYPES_NEXT) {
                mOnClickListener.onNextPageClick()
                return@setOnClickListener
            }

            if (item.type == PAGINATOR_TYPES_PREVIOUS) {
                mOnClickListener.onPreviousPageClick()
                return@setOnClickListener
            }
        }

        if (item.isCurrentPage) {
            //holder.pageNumberText?.setTypeface(null, Typeface.BOLD)
            holder.pageNumberLayout.setBackgroundResource(R.drawable.paginator_border_active)
            holder.pageNumberText.setTextColor(MainApplication.applicationContext().resources.getColor(R.color.whiteBackground))
        } else {
            //holder.pageNumberText?.setTypeface(null, Typeface.NORMAL)
            holder.pageNumberLayout.setBackgroundResource(R.drawable.paginator_border)
            holder.pageNumberText.setTextColor(MainApplication.applicationContext().resources.getColor(R.color.colorButton))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (item.type == PAGINATOR_TYPES_DIVIDER) {
                holder.pageNumberLayout.foreground = null
            } else {
                holder.pageNumberLayout.foreground = MainApplication.applicationContext().resources.getDrawable(R.drawable.ripple_effect)
            }
        }

        holder.setItem(mItems[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (mItems != null) {
            return mItems.size
        }
        return -1
    }

    interface OnPaginatorClickListener {
        fun onPageNumberClick(selectedPageNumber: Int)
        fun onNextPageClick()
        fun onPreviousPageClick()
    }
}
