package ru.macgavrina.digitalshowroom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.model.*
import ru.macgavrina.digitalshowroom.repository.ProductRepository
import ru.macgavrina.digitalshowroom.repository.SportCategoriesRepository
import ru.macgavrina.digitalshowroom.support.Log
import ru.macgavrina.digitalshowroom.support.SingleLiveEvent

class ProductDetailsViewModel(application: Application) : AndroidViewModel(MainApplication.instance) {

    private var productsRepository: ProductRepository = ProductRepository
    private var sportCategoriesRepository: SportCategoriesRepository = SportCategoriesRepository
    private var productId: Int? = null
    private var productDetails: MutableLiveData<Product> = MutableLiveData()
    private var selectedImageId: Int? = null
    private var imagesWithSelectedMarkerList: MutableLiveData<List<ImageWithSelectedMarker>> = MutableLiveData()
    private var sportSparkowId: Int? = null
    internal val snackbarMessage = SingleLiveEvent<String>()
    private var allCategories: MutableLiveData<List<SportCategoryWithSelectedMarker>> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()

    init {
    }

    fun getProductDetails(): MutableLiveData<Product> {
        return productDetails
    }

    fun getImagesWithSelectedMarkerList(): MutableLiveData<List<ImageWithSelectedMarker>> {
        return imagesWithSelectedMarkerList
    }

    private fun setSportSparkowId(sportSparkowId: Int) {
        this.sportSparkowId = sportSparkowId

        val subscriptionToCategories = sportCategoriesRepository.getAllSportCategories(sportSparkowId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ categoriesList ->
                Log.d("categories list is received from server, $categoriesList")
                val sportCategoriesWitSelectedMarkerList = mutableListOf<SportCategoryWithSelectedMarker>()

                sportCategoriesWitSelectedMarkerList.add(SportCategoryWithSelectedMarker(categoriesList, true))

                categoriesList.children.forEach { sportCategory ->
                    sportCategoriesWitSelectedMarkerList.add(SportCategoryWithSelectedMarker(sportCategory, false))
                }

                allCategories.value = sportCategoriesWitSelectedMarkerList
            }, { error ->
                snackbarMessage.value = "Server error"
                Log.d( "Error getting categories from server, error = $error")
            })

        compositeDisposable.add(subscriptionToCategories)
    }

    fun setProductId(productId: Int) {
        this.productId = productId

        getProductDetailsFromServer()
    }

    fun setSelectedImageId(imageId: Int) {
        selectedImageId = imageId

        val imagesWithSelectedMarker = imagesWithSelectedMarkerList.value

        imagesWithSelectedMarker?.forEach { imageWithSelectedMarker ->
            imageWithSelectedMarker.isSelected = imageWithSelectedMarker.image.id == selectedImageId
        }
        imagesWithSelectedMarkerList.value = imagesWithSelectedMarker
    }

    fun getAllCategories(): MutableLiveData<List<SportCategoryWithSelectedMarker>> {
        return allCategories
    }

    fun retryButtonInSnackBarIsPressed() {
        getProductDetailsFromServer()
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }

    private fun getProductDetailsFromServer() {

        if (productId == null) return
        val subscription = productsRepository.getProductDetails(productId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ product ->

                Log.d("Product details are received from server, $product")
                productDetails.value = product

                val imagesWithSelectedMarker = mutableListOf<ImageWithSelectedMarker>()
                product.images.forEach { image ->
                    imagesWithSelectedMarker.add(ImageWithSelectedMarker(image, false))
                }
                imagesWithSelectedMarkerList.value = imagesWithSelectedMarker

                product.sparkow_categories.forEach {
                    if (it.lvl == 0) {
                        Log.d("set setSportSparkowId = ${it.id}")
                        setSportSparkowId(it.id)
                        return@subscribe
                    }
                }
            }, { error ->
                snackbarMessage.value = "Server error"
                Log.d("Error getting catalog from server, error = $error")
            })


        compositeDisposable.add(subscription)
    }
}