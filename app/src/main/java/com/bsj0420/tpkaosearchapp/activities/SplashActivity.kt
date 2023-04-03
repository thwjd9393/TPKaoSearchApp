package com.bsj0420.tpkaosearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bsj0420.tpkaosearchapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash) //테마 이용하여 화면을 구현
        
        // 1.5초 후에 로그인 액티비티(화면)로 전환
        // 메인 스레드를 잠재우면 절대 안됨
        // 별도 스레드에 요청
//        Handler(Looper.getMainLooper()).postDelayed(object:Runnable{
//            override fun run() {
//
//            }
//
//        } ,1500) //메인 루퍼를 이용해 UI 핸들러 생성
        // runable : 해야할 작
        // delay : 딜레이 할 시간

        //위 코드 람다로 줄이기
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        },1500)

    }
}