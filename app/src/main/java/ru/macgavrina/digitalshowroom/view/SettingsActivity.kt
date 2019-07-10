package ru.macgavrina.digitalshowroom.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_settings.*
import ru.macgavrina.digitalshowroom.INACTIVITY_TIMEOUT_MLS
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.support.Log
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private var subscriptionToInactivityTimer: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        activity_settings_exit_button.setOnClickListener {
            Log.d("onClick exit button")
            finishAffinity()
        }

        onUserInteraction()
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
}