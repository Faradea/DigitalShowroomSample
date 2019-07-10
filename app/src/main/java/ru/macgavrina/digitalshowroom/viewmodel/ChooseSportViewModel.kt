package ru.macgavrina.digitalshowroom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.model.Sport
import ru.macgavrina.digitalshowroom.repository.SportsRepository
import ru.macgavrina.digitalshowroom.support.Log
import ru.macgavrina.digitalshowroom.support.SingleLiveEvent

class ChooseSportViewModel(application: Application) : AndroidViewModel(MainApplication.instance) {

    private var repository: SportsRepository = SportsRepository
    private var allSports: MutableLiveData<List<Sport>> = MutableLiveData()
    internal val snackbarMessage = SingleLiveEvent<String>()
    private val compositeDisposable = CompositeDisposable()

    init {
        getSportsFromServer()
    }

    fun getAllSports(): MutableLiveData<List<Sport>> {
        return allSports
    }

    fun retryButtonInSnackBarIsPressed() {
        getSportsFromServer()
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }

    private fun getSportsFromServer() {
        val subscription = repository.getAllSports()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ allSportsResponse ->
                Log.d("All sports are received from server, $allSportsResponse")
                val filteredSportList = mutableListOf<Sport>()
                allSportsResponse.forEach { sport ->
                    if (sport.isActive && sport.sparkow_id != null) {
                        filteredSportList.add(sport)
                    }
                }
                allSports.value = filteredSportList
            }, { error ->
                snackbarMessage.value = "Server error"
                Log.d("Error getting sports from server, error = $error")
            })

        compositeDisposable.add(subscription)
    }
}