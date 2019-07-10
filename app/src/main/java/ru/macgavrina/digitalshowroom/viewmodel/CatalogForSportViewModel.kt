package ru.macgavrina.digitalshowroom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.model.GetCatalogResponse
import ru.macgavrina.digitalshowroom.model.SportCategoryWithSelectedMarker
import ru.macgavrina.digitalshowroom.repository.CatalogRepository
import ru.macgavrina.digitalshowroom.repository.SportCategoriesRepository
import ru.macgavrina.digitalshowroom.support.Log
import ru.macgavrina.digitalshowroom.support.SingleLiveEvent

class CatalogForSportViewModel(application: Application) : AndroidViewModel(MainApplication.instance) {

    private var sportCategoriesRepository: SportCategoriesRepository = SportCategoriesRepository
    private var catalogRepository: CatalogRepository = CatalogRepository
    private var sportSparkowId: Int? = null
    private var selectedCategorySparkowId: Int? = null
    private var pageNumber: Int = 0
    private var totalPages: Int = 0
    private var allCategories: MutableLiveData<List<SportCategoryWithSelectedMarker>> = MutableLiveData()
    private var allCatalogItems: MutableLiveData<GetCatalogResponse> = MutableLiveData()
    internal val snackbarMessage = SingleLiveEvent<String>()
    private val compositeDisposable = CompositeDisposable()

    init {
    }

    fun getAllCategories(): MutableLiveData<List<SportCategoryWithSelectedMarker>> {
        return allCategories
    }

    fun getAllCatalogItems(): MutableLiveData<GetCatalogResponse> {
        return allCatalogItems
    }

    fun setSportSparkowId(sportSparkowId: Int, selectedCategoryId: Int?) {
        this.sportSparkowId = sportSparkowId
        selectedCategorySparkowId = selectedCategoryId ?: sportSparkowId

        getCategoriesFromServer()

        getCatalogFromServer()

    }

    fun setPageNumber(pageNumber: Int) {
        this.pageNumber = pageNumber

        if (selectedCategorySparkowId == null) return

        getCatalogFromServer()
    }

    fun setSelectedCategoryId(selectedCategoryId: Int) {

        if (this.selectedCategorySparkowId == selectedCategoryId) return

        this.selectedCategorySparkowId = selectedCategoryId
        pageNumber = 0

        if (allCategories.value == null) return

        val newSportCategoriesWithSelectedMarkerList = allCategories.value

        allCategories.value!!.forEach { categoryWithSelectedMarker ->
            categoryWithSelectedMarker.isSelected = (categoryWithSelectedMarker.sportCategory.id == selectedCategorySparkowId)
        }
        allCategories.value = newSportCategoriesWithSelectedMarkerList

        getCatalogFromServer()
    }

    fun retryButtonInSnackBarIsPressed() {
        if (selectedCategorySparkowId == null || sportSparkowId == null) return

        if (allCategories.value == null) {
            getCategoriesFromServer()
        }

        getCatalogFromServer()
    }

    fun gotoPreviousPage() {
        Log.d("goto previous page, current page = $pageNumber")
        if (pageNumber > 0) {
            setPageNumber(pageNumber - 1)
        }
    }

    fun gotoNextPage() {
        Log.d("goto next page, current page = $pageNumber")
        if (pageNumber < totalPages - 1) {
            setPageNumber(pageNumber + 1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun getCatalogFromServer() {
        val subscription = catalogRepository.getAllCatalogItems(selectedCategorySparkowId!!, pageNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getCatalogResponse ->
                Log.d("Catalog is received from server, getCatalogResponse = $getCatalogResponse")
                allCatalogItems.value = getCatalogResponse
                totalPages = getCatalogResponse.totalPages
            }, { error ->
                snackbarMessage.value = "Server error"
                Log.d("Error getting catalog from server, error = $error")
            })
        compositeDisposable.add(subscription)
    }

    private fun getCategoriesFromServer() {
        if (sportSparkowId == null) return

        val subscriptionToCategories = sportCategoriesRepository.getAllSportCategories(sportSparkowId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ categoriesList ->
                Log.d("Categories list is received from server, $categoriesList")
                val sportCategoriesWitSelectedMarkerList = mutableListOf<SportCategoryWithSelectedMarker>()

                sportCategoriesWitSelectedMarkerList.add(SportCategoryWithSelectedMarker(categoriesList, categoriesList.id == selectedCategorySparkowId))

                categoriesList.children.forEach { sportCategory ->
                    sportCategoriesWitSelectedMarkerList.add(SportCategoryWithSelectedMarker(sportCategory, sportCategory.id == selectedCategorySparkowId))
                }

                allCategories.value = sportCategoriesWitSelectedMarkerList
            }, { error ->
                snackbarMessage.value = "Server error"
                Log.d("Error getting categories from server, error = $error")
            })

        compositeDisposable.add(subscriptionToCategories)
    }
}