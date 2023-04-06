package com.bsj0420.tpkaosearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.bsj0420.tpkaosearchapp.R
import com.bsj0420.tpkaosearchapp.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {

    val binding : ActivityPlaceUrlBinding by lazy { ActivityPlaceUrlBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //웹뷰 만들때 아래 세개는 필수 속성!
        //안드로이드 운영체제한테 크롬브라우저가 우선시함
        //1. 현재 웹뷰 안에서 웨문서 열리도록
        binding.webView.webViewClient = WebViewClient()
        
        //2. 웹문서 안에서 다이아로그 같은 것을 발동하도록하는것
        binding.webView.webChromeClient = WebChromeClient()
        
        //3. 웹뷰는 보안문제로 js 동작을 막아둠 - 허용해야 웹문서 안에서 버트 같은 거 누를 때 동작함
        binding.webView.settings.javaScriptEnabled = true
        ////////////////////////////////////////////////////

        val place_url : String = intent.getStringExtra("place_url") ?: ""
        binding.webView.loadUrl(place_url) //웹뷰한테 null은 넣을 수 없음 빈글씨 넣으면 그냥 안보여주고 만다!

    }

    //웹뷰 안에서 상세페이지로 들어갔다가 뒤로가기 하면 다시 메인의 리스트로 돌아가버림
    //현재 액티비티가 꺼져버림 뒤로가기가 액티비티를 끄기 떄문
    //그게 아니라 웹뷰안에 쌓인 상세페이지 뒤로 가기를 하려면 if 문 처리해줘야함!
    override fun onBackPressed() {

        if (binding.webView.canGoBack()) binding.webView.goBack() //돌아갈게 있으면 뒤로가
        else super.onBackPressed()

    }

}