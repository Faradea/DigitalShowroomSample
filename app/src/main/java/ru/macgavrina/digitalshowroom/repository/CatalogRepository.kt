package ru.macgavrina.digitalshowroom.repository

import io.reactivex.Single
import ru.macgavrina.digitalshowroom.api.CatalogDataService
import ru.macgavrina.digitalshowroom.model.GetCatalogResponse

object CatalogRepository {

    init {
    }

    fun getAllCatalogItems(sparkowId: Int, pageNumber: Int?): Single<GetCatalogResponse> {
        return if (pageNumber != null) {
            CatalogDataService.create().getAllCatalogItemsForPage(sparkowId, pageNumber)
        } else {
            CatalogDataService.create().getAllCatalogItems(sparkowId)
        }
    }
}