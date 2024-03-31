package com.example.kspdemo2.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ksp_annotation.VMTransition

class TvViewModel : ViewModel() {

    @VMTransition(host = BtnViewModel::class, target = "btnLiveData")
    val mTvBtnData = MutableLiveData<String>()

}