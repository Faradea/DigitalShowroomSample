package ru.macgavrina.digitalshowroom.repository

import io.reactivex.Single
import ru.macgavrina.digitalshowroom.api.ProductDetailsDataService
import ru.macgavrina.digitalshowroom.model.Product

object ProductRepository {

    init {
    }

    fun getProductDetails(productId: Int): Single<Product> {
        return ProductDetailsDataService.create().getProductDetails(productId)
    }
}

