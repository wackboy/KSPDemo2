package com.example.kspdemo2.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BtnViewModel : ViewModel() {

    var btnLiveData = MutableLiveData("hello")
    private var _btnLiveData: LiveData<String> = btnLiveData

}