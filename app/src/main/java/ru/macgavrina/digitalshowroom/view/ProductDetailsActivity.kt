package ru.macgavrina.digitalshowroom.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_productdetails.*
import ru.macgavrina.digitalshowroom.INACTIVITY_TIMEOUT_MLS
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.adapter.*
import ru.macgavrina.digitalshowroom.model.ImageWithSelectedMarker
import ru.macgavrina.digitalshowroom.model.Product
import ru.macgavrina.digitalshowroom.model.SportCategoryWithSelectedMarker
import ru.macgavrina.digitalshowroom.support.Log
import ru.macgavrina.digitalshowroom.viewmodel.ProductDetailsViewModel
import java.util.concurrent.TimeUnit


class ProductDetailsActivity: AppCompatActivity(), ProductImagesRecyclerViewAdapter.OnProductImageClickListener,
    SportCategoriesRecyclerViewAdapter.OnSportCategoryClickListener {

    private lateinit var viewModel: ProductDetailsViewModel
    private var snackBar: Snackbar? = null
    private var sportSparkowId: Int? = null
    private var sportName: String? = null
    private var subscriptionToInactivityTimer: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productdetails)

        hideSystemUI()
        showProgress()
        hideProductDetailsViews()

        val extras = intent.extras
        val productId = extras?.getInt("productId")
        Log.d("Product details activity is created, data from intent: productId = $productId")

        activity_productdetails_goback_layout.setOnClickListener {
            Log.d("goback button is pressed")
            onBackPressed()
        }

        activity_productdetails_goback_layout_bottom.setOnClickListener {
            Log.d("goback button (bottom) is pressed")
            onBackPressed()
        }

        viewModel = ViewModelProviders.of(this).get(ProductDetailsViewModel::class.java)
        if (productId != null) {
            viewModel.setProductId(productId)
        }

        viewModel.getProductDetails().observe(this,
            Observer<Product> { product ->

                showProductDetailsViews()
                displayProductDetails(product)

                product.sparkow_categories.forEach {
                    if (it.lvl == 0) {
                        sportSparkowId = it.id
                        sportName = it.name
                        return@Observer
                    }
                }
            })

        viewModel.getImagesWithSelectedMarkerList().observe(this,
            Observer<List<ImageWithSelectedMarker>> { imagesWithSelectedMarkerList ->

                val imagesAdapter = ProductImagesRecyclerViewAdapter(this)
                activity_productdetails_images_list.adapter = imagesAdapter
                activity_productdetails_images_list.layoutManager = LinearLayoutManager(MainApplication.applicationContext(), LinearLayoutManager.HORIZONTAL, false)
                imagesAdapter.setImagesList(imagesWithSelectedMarkerList)

            })

        viewModel.snackbarMessage.observe(this, Observer {
            hideProgress()
            displaySnackBar(this.resources.getString(R.string.snackbar_text), this.resources.getString(R.string.snackbar_retry_button))
        })

        viewModel.getAllCategories().observe(this,
            Observer<List<SportCategoryWithSelectedMarker>> { categoriesList ->

                if (categoriesList != null) {
                    val adapter = SportCategoriesRecyclerViewAdapter(this)
                    activity_productdetails_categories_list.adapter = adapter
                    activity_productdetails_categories_list.layoutManager = LinearLayoutManager(MainApplication.applicationContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter.setSports(categoriesList)

                    val adapterBottom = SportCategoriesRecyclerViewAdapter(this)
                    activity_productdetails_categories_list_bottom.adapter = adapterBottom
                    activity_productdetails_categories_list_bottom.layoutManager = LinearLayoutManager(MainApplication.applicationContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapterBottom.setSports(categoriesList)
                }
            })
    }

    override fun onResume() {
        super.onResume()

        onUserInteraction()
    }

    override fun onPause() {
        super.onPause()
        subscriptionToInactivityTimer?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptionToInactivityTimer?.dispose()
    }

    override fun onCategoryClick(selectedCategorySparkowId: Int) {
        Log.d("onCategoryClick, categoryId = $selectedCategorySparkowId,  sportSparkowId = $sportSparkowId, sportName = $sportName")
        val intent = Intent(this, CatalogActivity::class.java)
        intent.putExtra("sportSparkowId", sportSparkowId)
        intent.putExtra("sportNameRu", sportName)
        if (selectedCategorySparkowId != sportSparkowId) {
            intent.putExtra("sportCategorySparkowId", selectedCategorySparkowId)
        }
        this.startActivity(intent)
    }

    override fun onImageClick(imageUrl: String, imageId: Int) {
        Picasso.get()
            .load(imageUrl)
            .resize(500, 500)
            .centerCrop()
            .into(activity_productdetails_mainimage)

        viewModel.setSelectedImageId(imageId)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        if (subscriptionToInactivityTimer == null) {
            Log.d("initialize subscriptionToInactivityTimer")
            subscribeToIntervalIObserver()
        } else {
            Log.d("reset subscriptionToInactivityTimer")
            subscriptionToInactivityTimer!!.dispose()
            subscribeToIntervalIObserver()
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun displaySnackBar(messageText: String, actionText: String) {

        if (snackBar != null) return

        snackBar = Snackbar.make(activity_productdetails_layout, messageText, Snackbar.LENGTH_INDEFINITE)
        snackBar!!.setAction(actionText) {
            snackBar?.dismiss()
            snackBar = null
            Log.d("Retry button in snackbar is pressed")
            viewModel.retryButtonInSnackBarIsPressed()
            showProgress()
        }
        snackBar?.show()
    }

    private fun displayProductDetails(product: Product) {

        hideProgress()

        activity_productdetails_brand_tv.text = product.brand.name
        activity_productdetails_id_tv.text = MainApplication.applicationContext().resources.getString(
            R.string.product_ids, product.dsm, product.id.toString()
        )
        activity_productdetails_productname_tv.text = product.name
        if (product.review.rating == 0F) {
            activity_productdetails_ratingBar.visibility = View.INVISIBLE
            activity_productdetails_rating_tv.visibility = View.INVISIBLE
        } else {
            activity_productdetails_ratingBar.rating = product.review.rating
            activity_productdetails_rating_tv.text = MainApplication.applicationContext().resources.getString(
                R.string.rating_value,
                product.review.rating.toString()
            )
        }
        activity_productdetails_price_tv.text = MainApplication.applicationContext().resources.getString(R.string.price_value, product.price.toString())

        Picasso.get()
            .load(product.image.link)
            .resize(500, 500)
            .centerCrop()
            .into(activity_productdetails_mainimage)

        activity_productdetails_desc_tv.text = product.description

        val benefitsList = product.benefits.filter { it.active }
        val benefitsAdapter = ProductBenefitsRecyclerViewAdapter()
        activity_productdetails_benefits_list.adapter = benefitsAdapter
        activity_productdetails_benefits_list.layoutManager = LinearLayoutManager(MainApplication.applicationContext())
        benefitsAdapter.setBenefitsList(benefitsList)

        val characteristicsAdapter = ProductCharacteristicsRecyclerViewAdapter()
        activity_productdetails_characteristics_list.adapter = characteristicsAdapter
        val layoutManagerCharacteristics = FlexboxLayoutManager(MainApplication.applicationContext())
        layoutManagerCharacteristics.flexWrap = FlexWrap.WRAP
        layoutManagerCharacteristics.flexDirection = FlexDirection.COLUMN
        layoutManagerCharacteristics.justifyContent = JustifyContent.CENTER
        activity_productdetails_characteristics_list.layoutManager = layoutManagerCharacteristics
        characteristicsAdapter.setCharacteristicsList(product.characteristics)
    }

    private fun hideProgress() {
        activity_productdetails_progressBar.visibility = View.INVISIBLE
    }

    private fun showProgress() {
        activity_productdetails_progressBar.visibility = View.VISIBLE
    }

    private fun subscribeToIntervalIObserver() {
        val interval = Observable
            .interval(INACTIVITY_TIMEOUT_MLS, TimeUnit.MILLISECONDS)
        subscriptionToInactivityTimer = interval
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("Inactivity TIMEOUT!!!!")
                subscriptionToInactivityTimer!!.dispose()
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
            }
    }

    private fun hideProductDetailsViews() {
        divider4.visibility = View.INVISIBLE
        activity_productdetails_ratingBar.visibility = View.INVISIBLE
        activity_productdetails_price_tv.visibility = View.INVISIBLE
        divider6.visibility = View.INVISIBLE
        divider7.visibility = View.INVISIBLE
        activity_productdetails_benefits_layout.visibility = View.INVISIBLE
        activity_productdetails_technicaldetails_layout.visibility = View.INVISIBLE
        divider9.visibility = View.INVISIBLE
        activity_productdetails_goback_layout_bottom.visibility = View.INVISIBLE
        activity_productdetails_mainimage.layoutParams.height = 100
    }

    private fun showProductDetailsViews() {
        divider4.visibility = View.VISIBLE
        activity_productdetails_ratingBar.visibility = View.VISIBLE
        activity_productdetails_price_tv.visibility = View.VISIBLE
        divider6.visibility = View.VISIBLE
        divider7.visibility = View.VISIBLE
        activity_productdetails_benefits_layout.visibility = View.VISIBLE
        activity_productdetails_technicaldetails_layout.visibility = View.VISIBLE
        divider9.visibility = View.VISIBLE
        activity_productdetails_goback_layout_bottom.visibility = View.VISIBLE
        activity_productdetails_mainimage.layoutParams.height = 500
    }

}