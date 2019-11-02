package com.example.tsetmc.service.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedPreferencesUtil(context: Context) {
    private lateinit var sharedPreferences: SharedPreferences
    private val name = "LastUpdate"
    private val date = "date"

    private val _lastUpdateDateLive = MutableLiveData<String>()
    val observableLastUpdate: LiveData<String> get() = _lastUpdateDateLive

    var lastUpdate: String? = null
        get() = observableLastUpdate.value
        set(value) {
            sharedPreferences.edit { putString(date, value) }
            _lastUpdateDateLive.postValue(value)
            field = value
        }

    init {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        lastUpdate = sharedPreferences.getString(date, "")
    }

}