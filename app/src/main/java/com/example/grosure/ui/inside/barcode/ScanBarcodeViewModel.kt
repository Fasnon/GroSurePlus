package com.example.grosure.ui.inside.barcode

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.ConnectException

class ScanBarcodeViewModel : ViewModel() {

    val progressState: LiveData<Boolean> get() = _progressState
    private val _progressState = MutableLiveData<Boolean>()

    val navigation: LiveData<NavDirections?> get() = _navigation
    private val _navigation = MutableLiveData<NavDirections?>()

    init {
        _progressState.value = false
    }

    fun searchBarcode(barcode: String) {


    }

    fun doneNavigating() {
        _navigation.value = null
    }
}