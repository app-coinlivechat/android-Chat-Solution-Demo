package com.coinlive.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.coinlive.chat.Coinlive
import com.coinlive.demo.databinding.ActivityMainBinding
import com.coinlive.demo.viewmodels.MainActivityViewModel
import com.coinlive.uikit.callbacks.InputViewUnknownUserListener
import com.coinlive.uikit.views.CoinLiveToast
import java.util.*

class MainActivity : AppCompatActivity(), InputViewUnknownUserListener {
    lateinit var viewModel: MainActivityViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Coinlive.init(context = this, isDebug = true, locale = Locale.getDefault())
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.initUiKit(this)
        setContentView(binding.root)
    }

    override fun click() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("로그인이 필요합니다.")
//        builder.setMessage("채팅을 사용하기 위해서는 로그인이 필요합니다.\n로그인하시겠습니까?")
//        builder.setPositiveButton("확인") { _, _ ->
//            supportFragmentManager.popBackStack()
//            supportFragmentManager.popBackStack()
//        }
//        val dialog = builder.create()
//        dialog.show()
        CoinLiveToast.make(binding.root,getString(R.string.please_login)).show()
    }
}