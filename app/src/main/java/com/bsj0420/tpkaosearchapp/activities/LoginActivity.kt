package com.bsj0420.tpkaosearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bsj0420.tpkaosearchapp.G
import com.bsj0420.tpkaosearchapp.R
import com.bsj0420.tpkaosearchapp.databinding.ActivityLoginBinding
import com.bsj0420.tpkaosearchapp.model.NIdUserInfo
import com.bsj0420.tpkaosearchapp.model.UserAccount
import com.bsj0420.tpkaosearchapp.network.RetrofitApiService
import com.bsj0420.tpkaosearchapp.network.RetrofitHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    val binding:ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //둘러보기 버튼으로 로그인 없이 메인화면으로 이동
        binding.tvGo.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        //회원가입 버튼 클릭 했을 떄 반응
        binding.tvSingup.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }

        //이메일 로그인 버튼 클릭
        binding.layoutEmail.setOnClickListener {
            startActivity(Intent(this,EmailSigninActivity::class.java))
        }
        
        //간편로그인 버튼
        binding.ivLoginKakao.setOnClickListener { clickLoginKakao() }
        binding.ivLoginGoogle.setOnClickListener { clickLoginGoogle() }
        binding.ivLoginNaver.setOnClickListener { clickLoginNaver() }

        //카카오 키 해시 값 얻어오기
        val keyHash:String = Utility.getKeyHash(this)
        Log.i("keyHash", keyHash)

    }// onCreate

    private fun clickLoginKakao() {

        //공통 callback 함수
//        val callback:(OAuthToken?, Throwable?) -> Unit = fun(token, error) {
//
//        }
        // 길어 람다로 줄이자
        val callback:(OAuthToken?, Throwable?) -> Unit =  { token, error ->
            if(token != null){
                //토큰 값 있으면 로그인 성공
                Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()
                
                //사용자의 정보 요청 (1.회원 번호, 2.이메일 주소)
                UserApiClient.instance.me { user, error ->
                    if(user != null){
                        var id:String = user.id.toString()
                        var email:String = user.kakaoAccount?.email ?: "" //혹시 null이면 이메일의 기본값은 ""
                        //kakaoAccount 동의 항목은 kakaoAccount 여기 안에 있음

                        // ?: 앞에게 null이면 뒤에 값!!!!************

                        Toast.makeText(this, "$email", Toast.LENGTH_SHORT).show()

                        G.UserAccount = UserAccount(id, email)

                        //메인 화면으로 이동
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                }

            } else {
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        //카카오톡이 설치되어 있으면 카톡으로 로그인 없으면 카카오 계정으로 로그인

        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback) //고차함수
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
        // loginWithKakaoTalk 도 loginWithKakaoAccount 도 callback 받아야함 그래서 공통으로 함수 빼서 만듦
    }

    private fun clickLoginGoogle() {
        //안드로이드 구글 로그인
        
        //구글 로그인 옵션 객체 생성 - 빌더 이용 
        val signInOpthions:GoogleSignInOptions = GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        //회원 번호는 요청 안해도 온다
        
        //구글 로그인 화면(액티비티)를 실행하는 Intent를 통해 구현
        val intent:Intent = GoogleSignIn.getClient(this,signInOpthions).signInIntent
        resultLuancher.launch(intent) //결과값 받아오는 애 필요
    }

    //구글 로그인 화면의 실행결과를 받아오는 계약체결 대행사 만들기
    val resultLuancher:ActivityResultLauncher<Intent>
        = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object : ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            //로그인 결과를 가져온 인텐트 소환
            val intent:Intent? = result?.data

            //돌아온 인텐트로 부터 구글계정 정보를 가져오는 작업 수행
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)

            val account:GoogleSignInAccount = task.result
            var id:String = account.id.toString() //null 값일 수 없어서 강제 String
            var email:String = account.email ?: ""

            Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()

            //글로벌에 등록
            G.UserAccount = UserAccount(id, email)

            //메인 화면으로 이동
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
            finish()
        }
    })

    private fun clickLoginNaver() {

        //네아로 초기화!
        NaverIdLoginSDK.initialize(this,"G9iGDn4kHy9D4jWEGhUf","6ElqZjg9QK","요모조모")

        //네이버 로그인
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "error : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "로그인 실패 : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                //사용자 정보를 가져오는 REST API(url을 통해 알려주는 방식)를 작업할 때 접속토큰 필요
                //REST : 전송에 대한 상태를 전부 표현해 둔 것

                val accessToken : String? = NaverIdLoginSDK.getAccessToken()
                //토큰값 확인
                Log.i("TOKEN", accessToken!!)

                // 레트로핏을 이용해서 사용자 정보 API 가져오기
                //0. 베이스유알엘 과 컨버퍼펙토리 만들기
                val retrofit = RetrofitHelper.getRetrofitInstance(G.naverBaseUrl)

                //1. 인터페이스 명세서 작성
                retrofit.create(RetrofitApiService::class.java).getNidUserInfo("Bearer $accessToken")
                    .enqueue(object : Callback<NIdUserInfo>{
                        override fun onResponse(
                            call: Call<NIdUserInfo>,
                            response: Response<NIdUserInfo>
                        ) {
                            val userInfo : NIdUserInfo? = response.body()
                            val id:String = userInfo?.response?.id ?: "" //앞이 널이면 뒤
                            val email:String = userInfo?.response?.email ?: ""

                            Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()
                            G.UserAccount = UserAccount(id, email)

                            //main 화면으로 이동
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onFailure(call: Call<NIdUserInfo>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, "회원정보 불러오기 실패 ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })



            }

        })



    }
}