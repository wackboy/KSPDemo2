package com.example.kspdemo2

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.kspdemo2.vm.BtnViewModel
import com.example.kspdemo2.vm.TvViewModel
import kotlin.reflect.KClass

fun vmHolderInit2(owner: Activity) {
    val sourceVM = generate(BtnViewModel::class, owner)
    val targetVM = generate(TvViewModel::class, owner)
    sourceVM.btnLiveData.observe(owner as LifecycleOwner) {
        targetVM.mTvBtnData.value = it
    }

}

fun <T: ViewModel> generate(clazz: KClass<T>, owner: Activity): T {
    return ViewModelProvider(owner as ViewModelStoreOwner).get(clazz.java)
}