package com.example.tsetmc.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tsetmc.service.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel: ViewModel() {
    private val job = Job()
    protected val scope = CoroutineScope(job + Dispatchers.Main)
    protected val repository = Repository()

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    interface ClickEvent{
        fun actionIsSuccessful()
        fun actionIsFailed()
    }
}