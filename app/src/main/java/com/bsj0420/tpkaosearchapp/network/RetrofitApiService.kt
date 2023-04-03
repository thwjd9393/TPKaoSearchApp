package com.bsj0420.tpkaosearchapp.network

import com.bsj0420.tpkaosearchapp.model.NIdUserInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RetrofitApiService {

    //네이버 아이디 로그인 사용자 정보 API
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization : String) : Call<NIdUserInfo>
    //Authorization : 토큰값 가져올 애

}