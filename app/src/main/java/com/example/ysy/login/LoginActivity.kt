package com.example.ysy.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.ysy.R


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var closeBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        closeBtn = findViewById(R.id.closeBtn)
        closeBtn.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            // 로그인 팝업창 닫기
            R.id.closeBtn -> {
                finish()
            }
        }
    }
}