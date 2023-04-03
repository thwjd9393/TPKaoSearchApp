package com.bsj0420.tpkaosearchapp

import com.bsj0420.tpkaosearchapp.model.UserAccount

class G {

    // static = companion object
    companion object {
        var UserAccount: UserAccount? = null //로그인을 안했을 수 있으니까 nullable

        var naverBaseUrl : String = "https://openapi.naver.com"
    }

}