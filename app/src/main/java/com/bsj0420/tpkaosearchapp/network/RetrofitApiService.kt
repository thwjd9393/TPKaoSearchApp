package com.bsj0420.tpkaosearchapp.network

import com.bsj0420.tpkaosearchapp.G
import com.bsj0420.tpkaosearchapp.GlobalApplication
import com.bsj0420.tpkaosearchapp.model.KakaoSerchPlaceResponce
import com.bsj0420.tpkaosearchapp.model.NIdUserInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitApiService {

    //네이버 아이디 로그인 사용자 정보 API
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization : String) : Call<NIdUserInfo>
    //Authorization : 토큰값 가져올 애
    //헤더 정보가 계속 변화되면 파라미터로

    //카카오의 키워드 검색 API에 대한 요청 작업
    //헤더 정보가 고정값이면 이 위에
    @Headers("Authorization: KakaoAK 13519e0fe75a3b9a2ac8fe763f691929")
    @GET("/v2/local/search/keyword")
    fun searchPlace(@Query("query")query:String, @Query("y")latitude:String, @Query("x")longitude: String):Call<KakaoSerchPlaceResponce>
    // @Query = 서버에서 오는 식별자

    //

}