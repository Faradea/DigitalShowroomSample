package ru.macgavrina.digitalshowroom.repository

import io.reactivex.Single
import ru.macgavrina.digitalshowroom.api.SportCategoriesDataService
import ru.macgavrina.digitalshowroom.model.SportCategory

object SportCategoriesRepository {

    init {
    }

    fun getAllSportCategories(sportSparkowId: Int): Single<SportCategory> {
        return SportCategoriesDataService.create().getSportCategories(sportSparkowId)
    }
}