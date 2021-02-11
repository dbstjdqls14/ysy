package com.example.ysy.common

import android.app.Application
import com.example.ysy.R
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 카카오 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        // 페이스북 초기화
        AppEventsLogger.activateApp(this)
    }
}