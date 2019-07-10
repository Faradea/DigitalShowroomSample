package ru.macgavrina.digitalshowroom.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_catalog.*
import ru.macgavrina.digitalshowroom.INACTIVITY_TIMEOUT_MLS
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.adapter.CatalogRecyclerViewAdapter
import ru.macgavrina.digitalshowroom.adapter.PaginatorAdapter
import ru.macgavrina.digitalshowroom.adapter.SportCategoriesRecyclerViewAdapter
import ru.macgavrina.digitalshowroom.model.*
import ru.macgavrina.digitalshowroom.support.Log
import ru.macgavrina.digitalshowroom.support.PaginatorCalculator.Companion.generatePageNumbersList
import ru.macgavrina.digitalshowroom.viewmodel.CatalogForSportViewModel
import java.util.concurrent.TimeUnit






class CatalogActivity: AppCompatActivity(), PaginatorAdapter.OnPaginatorClickListener, SportCategoriesRecyclerViewAdapter.OnSportCategoryClickListener{

    private var snackBar: Snackbar? = null
    private lateinit var viewModel: CatalogForSportViewModel
    private var subscriptionToInactivityTimer: Disposable? = null
    private var selectedCategorySparkowId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        hideSystemUI()
        hideCatalogViews()

        val extras = intent.extras
        val sportSparkowId = extras?.getInt("sportSparkowId")
        val sportName = extras?.getString("sportNameRu")
        var sportCategorySparkowId = extras?.getInt("sportCategorySparkowId")
        if (sportCategorySparkowId == 0) {
            sportCategorySparkowId = null
        }

        Log.d("Activity is created, data from intent: sportSparkowId = $sportSparkowId, sportNameRu = $sportName, sportCategorySparkowId = $sportCategorySparkowId")

        viewModel = ViewModelProviders.of(this).get(CatalogForSportViewModel::class.java)

        if (sportSparkowId != null) {
            viewModel.setSportSparkowId(sportSparkowId, sportCategorySparkowId)
        }

        activity_catalog_gotoallsports_layout.setOnClickListener {
            Log.d("onClick goto all sports")
            onBackPressed()
        }

        if (sportName != null) {
            setupSportName(sportName)
        }

        initializeSportCategoriesList()

        initializeCatalogForAllCategory()

        viewModel.snackbarMessage.observe(this, Observer {
            hideProgress()
            displaySnackBar(this.resources.getString(R.string.snackbar_text), this.resources.getString(R.string.snackbar_retry_button))
        })
    }

    override fun onResume() {
        super.onResume()
        onUserInteraction()

        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        subscriptionToInactivityTimer?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptionToInactivityTimer?.dispose()
    }


    override fun onPreviousPageClick() {
        showProgress()
        viewModel.gotoPreviousPage()
    }

    override fun onNextPageClick() {
        showProgress()
        viewModel.gotoNextPage()
    }

    override fun onPageNumberClick(selectedPageNumber: Int) {
        showProgress()
        viewModel.setPageNumber(selectedPageNumber)
    }

    override fun onCategoryClick(selectedCategorySparkowId: Int) {

        snackBar?.dismiss()
        snackBar = null

        if (this.selectedCategorySparkowId == selectedCategorySparkowId) return
        this.selectedCategorySparkowId = selectedCategorySparkowId
        showProgress()
        Log.d("onCategoryClick, selectedCategorySparkowId = $selectedCategorySparkowId")
        viewModel.setSelectedCategoryId(selectedCategorySparkowId)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        hideSystemUI()

        if (subscriptionToInactivityTimer == null) {
            Log.d("initialize subscriptionToInactivityTimer")
            subscribeToIntervalIObserver()
        } else {
            Log.d("reset subscriptionToInactivityTimer")
            subscriptionToInactivityTimer!!.dispose()
            subscribeToIntervalIObserver()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
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

    private fun setupSportName(sportName: String) {
        activity_catalog_sportname_tv.text = sportName
    }

    private fun initializeSportCategoriesList() {

        viewModel.getAllCategories().observe(this,
            Observer<List<SportCategoryWithSelectedMarker>> { categoriesList ->

                if (categoriesList != null) {
                    val adapter = SportCategoriesRecyclerViewAdapter(this)
                    activity_catalog_categories_recycler_view.adapter = adapter
                    activity_catalog_categories_recycler_view.layoutManager = LinearLayoutManager(MainApplication.applicationContext(), LinearLayoutManager.HORIZONTAL, false)

                    adapter.setSports(categoriesList)
                }
            })

    }

    private fun initializeCatalogForAllCategory() {

        val catalogAdapter = CatalogRecyclerViewAdapter()
        activity_catalog_catalog_recyclerview.adapter = catalogAdapter
        val layoutManager = FlexboxLayoutManager(MainApplication.applicationContext())
        //layoutManager.flexDirection = FlexDirection.COLUMN
        layoutManager.flexWrap = FlexWrap.WRAP
        //layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.justifyContent = JustifyContent.CENTER

        //val layoutManager = GridLayoutManager(this, 4)

            //GridLayoutManager(this, 4)
//        activity_catalog_catalog_recyclerview.addItemDecoration(DividerItemDecoration(this,
//            DividerItemDecoration.HORIZONTAL))
//        activity_catalog_catalog_recyclerview.addItemDecoration(DividerItemDecoration(this,
//            DividerItemDecoration.VERTICAL))

        activity_catalog_catalog_recyclerview.layoutManager = layoutManager

        viewModel.getAllCatalogItems().observe(this,
            Observer<GetCatalogResponse> { getCatalogResponse ->

                if (getCatalogResponse != null) {

                    val oneLevelCatalogItemsList = mutableListOf<CatalogItem>()

                    getCatalogResponse.content.forEach { items ->
                        oneLevelCatalogItemsList.addAll(items.items)
                    }

                    catalogAdapter.setCatalogItems(oneLevelCatalogItemsList)
                    showCatalogViews()

                    hideProgress()

                    initializeOrUpdatePaginator(getCatalogResponse.totalPages, getCatalogResponse.pageable.pageNumber + 1)

                    //This code is necessary because flex layout doesn't wrap content correctly if nested scroll is set to false
                    val displayMetrics = MainApplication.applicationContext().resources.displayMetrics
                    val dpWidth = displayMetrics.widthPixels / displayMetrics.density
                    val flexLayoutNumberOfColumns = Math.floor(dpWidth / 299.0)

                    activity_catalog_catalog_recyclerview.layoutParams.height =
                        (400 * Math.ceil(getCatalogResponse.content.size / flexLayoutNumberOfColumns) + 2).toInt()
                }
            })
    }

    private fun initializeOrUpdatePaginator(totalPagesAmount: Int?, pageNumber: Int?) {

        if (totalPagesAmount == null || pageNumber == null) {
            val viewManagerForPaginator = LinearLayoutManager(MainApplication.applicationContext(),
                LinearLayoutManager.HORIZONTAL, false)
            activity_catalog_paginator_recyclerview.adapter = PaginatorAdapter(null, this)
            activity_catalog_paginator_recyclerview.layoutManager = viewManagerForPaginator


            val viewManagerForPaginatorBottom = LinearLayoutManager(MainApplication.applicationContext(),
                LinearLayoutManager.HORIZONTAL, false)
            activity_catalog_paginator_bottom_recyclerview.adapter = PaginatorAdapter(null, this)
            activity_catalog_paginator_bottom_recyclerview.layoutManager = viewManagerForPaginatorBottom

            return
        }

        val viewManagerForPaginator = LinearLayoutManager(MainApplication.applicationContext(),
            LinearLayoutManager.HORIZONTAL, false)
        if (totalPagesAmount == 1) {
            activity_catalog_paginator_recyclerview.adapter = PaginatorAdapter(null, this)
        } else {
            activity_catalog_paginator_recyclerview.adapter = PaginatorAdapter(generatePageNumbersList(totalPagesAmount, pageNumber), this)
        }
        activity_catalog_paginator_recyclerview.layoutManager = viewManagerForPaginator

        val viewManagerForPaginatorBottom = LinearLayoutManager(MainApplication.applicationContext(),
            LinearLayoutManager.HORIZONTAL, false)
        if (totalPagesAmount == 1) {
            activity_catalog_paginator_bottom_recyclerview.adapter = PaginatorAdapter(null, this)
        } else {
            activity_catalog_paginator_bottom_recyclerview.adapter = PaginatorAdapter(generatePageNumbersList(totalPagesAmount, pageNumber), this)
        }
        activity_catalog_paginator_bottom_recyclerview.layoutManager = viewManagerForPaginatorBottom

    }


    private fun hideProgress() {
        activity_catalog_progressBar.visibility = View.INVISIBLE
    }

    private fun showProgress() {
        activity_catalog_progressBar.visibility = View.VISIBLE
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

    private fun displaySnackBar(messageText: String, actionText: String) {

        if (snackBar != null) return

        hideCatalogViews()

        snackBar = Snackbar.make(activity_catalog_layout, messageText, Snackbar.LENGTH_INDEFINITE)
        snackBar!!.setAction(actionText) {
            snackBar?.dismiss()
            snackBar = null
            Log.d("Retry button in snackbar is pressed")
            showProgress()
            viewModel.retryButtonInSnackBarIsPressed()
        }
        snackBar?.show()
    }

    private fun hideCatalogViews() {
        divider2.visibility = View.INVISIBLE
        activity_catalog_catalog_recyclerview.visibility = View.INVISIBLE
        divider5.visibility = View.INVISIBLE
        activity_catalog_paginator_recyclerview.visibility = View.INVISIBLE
        activity_catalog_paginator_bottom_recyclerview.visibility = View.INVISIBLE
        val adapter = activity_catalog_catalog_recyclerview.adapter as? CatalogRecyclerViewAdapter
        adapter?.setCatalogItems(null)
        activity_catalog_catalog_recyclerview.layoutParams.height = 200

    }

    private fun showCatalogViews() {
        divider2.visibility = View.VISIBLE
        activity_catalog_catalog_recyclerview.visibility = View.VISIBLE
        divider5.visibility = View.VISIBLE
        activity_catalog_paginator_recyclerview.visibility = View.VISIBLE
        activity_catalog_paginator_bottom_recyclerview.visibility = View.VISIBLE
    }

}