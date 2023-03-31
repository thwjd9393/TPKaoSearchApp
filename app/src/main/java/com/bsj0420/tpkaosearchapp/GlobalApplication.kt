package com.bsj0420.tpkaosearchapp

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //kokoo SDK 초기화
        KakaoSdk.init(this, "6ee080be0ced90f3ef1d38fe7cf93202") //개발자 사이트에 등록한 네이티브 앱키
    }

}