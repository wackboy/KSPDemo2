package com.example.kspdemo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ksp_annotation.BindView
import com.example.ksp_annotation.VMHolder
import com.example.kspdemo2.vm.BtnViewModel
import com.example.kspdemo2.vm.TvViewModel
import com.example.kspdemo2.vm.vmHolderInit

@VMHolder(
    [BtnViewModel::class,TvViewModel::class],
    host = MainActivity::class
)
class MainActivity : AppCompatActivity() {

    @BindView(R.id.btnView)
    lateinit var btn: Button

    @BindView(R.id.textView)
    lateinit var tv: TextView

    private val btnVM by lazy {
        ViewModelProvider(this)[BtnViewModel::class.java]
    }

    private val tvVM by lazy {
        ViewModelProvider(this)[TvViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindView()
        vmHolderInit(this)
        var count = 1
        btn.setOnClickListener {
            btnVM.btnLiveData.value = count.toString()
            count += 1
        }
        tvVM.mTvBtnData.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

}

