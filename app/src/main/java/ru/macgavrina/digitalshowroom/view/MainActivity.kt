package ru.macgavrina.digitalshowroom.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import ru.macgavrina.digitalshowroom.INACTIVITY_TIMEOUT_MLS
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.PIN
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.adapter.SearchSportAdapter
import ru.macgavrina.digitalshowroom.adapter.SportRecyclerViewAdapter
import ru.macgavrina.digitalshowroom.model.Sport
import ru.macgavrina.digitalshowroom.support.Log
import ru.macgavrina.digitalshowroom.viewmodel.ChooseSportViewModel
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var snackBar: Snackbar? = null
    private lateinit var viewModel: ChooseSportViewModel
    private var pinDialog: Dialog? = null
    private var subscriptionToInactivityTimer: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideSystemUI()
        showProgress()

        viewModel = ViewModelProviders.of(this).get(ChooseSportViewModel::class.java)

        val adapter = SportRecyclerViewAdapter()
        activity_main_sporttypes_recyclerview.adapter = adapter
        val layoutManager = FlexboxLayoutManager(MainApplication.applicationContext())

        //layoutManager.flexDirection = FlexDirection.COLUMN
        layoutManager.flexWrap = FlexWrap.WRAP
        //layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.justifyContent = JustifyContent.CENTER
        activity_main_sporttypes_recyclerview.layoutManager = layoutManager

        viewModel.getAllSports().observe(this,
            Observer<List<Sport>> { sportsList ->
                hideProgress()
                if (sportsList != null) {
                    adapter.setSports(sportsList)
                    setupSearch(sportsList)
                } else {
                    Log.d("Sport list is null")
                }
            })

        activity_main_settings_button.setOnClickListener {
            Log.d("on click hidden settings button, requesting PIN")
            displayPinAlertDialog()
        }

        viewModel.snackbarMessage.observe(this, Observer {
            displaySnackBar(this.resources.getString(R.string.snackbar_text), this.resources.getString(R.string.snackbar_retry_button))
        })

        activity_main_search_tv.setOnClickListener {
            clearSearchView()
        }
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


    override fun onBackPressed() {
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

        hideSystemUI()
    }

    private fun subscribeToIntervalIObserver() {
        val interval = Observable
            .interval(INACTIVITY_TIMEOUT_MLS, TimeUnit.MILLISECONDS)
        subscriptionToInactivityTimer = interval
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("Inactivity TIMEOUT!!!!")
                pinDialog?.dismiss()
                clearSearchView()
                hideSystemUI()
                hideKeyboard()
                subscriptionToInactivityTimer!!.dispose()
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

    private fun displayPinAlertDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.password_dialog, null)

        dialogBuilder.setView(dialogView)
        val editText = dialogView.findViewById(R.id.password_dialog_pin_edittext) as EditText

        //dialogBuilder.setTitle(getResources().getString(R.string.pin_dialog_title))
        dialogBuilder.setPositiveButton(resources.getString(R.string.done)) { _, _ ->
            if (editText.text.toString() == PIN) {
                Log.d("User enters correct PIN, goto settings activity")
                displaySettingsActivity()
            } else {
                Log.d("User enters incorrect PIN = ${editText.text}")
                Toast.makeText(MainApplication.applicationContext(), resources.getString(R.string.incorrect_pin), Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }

        dialogBuilder.setOnDismissListener {
            hideKeyboard()
            hideSystemUI()
        }
        pinDialog = dialogBuilder.create()

        pinDialog!!.show()

    }

    private fun displaySettingsActivity() {
        val intent = Intent()
        intent.action = "ru.decathlon.digitalshowroom.SETTINGS"
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val inputMethodManager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
    }

    private fun displaySnackBar(messageText: String, actionText: String) {

        if (snackBar != null) return
        hideProgress()

        snackBar = Snackbar.make(activity_main_layout2, messageText, Snackbar.LENGTH_INDEFINITE)
        snackBar!!.setAction(actionText) {
            snackBar?.dismiss()
            snackBar = null
            Log.d("Retry button in snackbar is pressed")
            showProgress()
            viewModel.retryButtonInSnackBarIsPressed()
        }
        snackBar?.show()
    }

    private fun hideProgress() {
        activity_main_progressBar.visibility = View.INVISIBLE
    }

    private fun showProgress() {
        activity_main_progressBar.visibility = View.VISIBLE
    }

    private fun setupSearch(sportsList: List<Sport>) {
        val sportsListArray = mutableListOf<String>()

        sportsList.forEach { sport ->
            if (!sport.name.ru.isNullOrEmpty()) {
                sportsListArray.add(sport.name.ru.toString())
            }
        }

        val searchAdapter = SearchSportAdapter(this, R.layout.search_sport_item, sportsListArray)
        activity_main_search_tv.threshold = 1
        activity_main_search_tv.setAdapter(searchAdapter)

        activity_main_search_tv.setOnItemClickListener { parent, view, position, id ->
            hideSystemUI()
            val itemOnPosition = parent.getItemAtPosition(position)
            Log.d("Clicked on item in search result, id = $id, position = $position, item on position = $itemOnPosition")

            sportsList.forEach { sport ->
                if (sport.name.ru == itemOnPosition) {
                    val intent = Intent(this, CatalogActivity::class.java)
                    intent.putExtra("sportSparkowId", sport.sparkow_id)
                    intent.putExtra("sportNameRu", sport.name.ru)
                    startActivity(intent)
                }
            }
        }
    }

    private fun clearSearchView() {
        activity_main_search_tv.setText("")
        hideKeyboard()
        activity_main_layout.requestFocus()
        activity_main_search_tv.hideClearButton()
    }
}
