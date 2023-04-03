package com.bsj0420.tpkaosearchapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.bsj0420.tpkaosearchapp.G
import com.bsj0420.tpkaosearchapp.R
import com.bsj0420.tpkaosearchapp.databinding.ActivityEmailSigninBinding
import com.bsj0420.tpkaosearchapp.model.UserAccount
import com.google.firebase.firestore.FirebaseFirestore

class EmailSigninActivity : AppCompatActivity() {

    lateinit var binding:ActivityEmailSigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmailSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        //업버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //뒤로가기 그림 바꾸기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrowback)
        //타이틀 삭제
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignin.setOnClickListener { clickSignIn() }

    }

    private fun clickSignIn() {
        var email:String = binding.etEmail.text.toString()
        var passwwd:String = binding.etPasswd.text.toString()

        //파이어베어스 이메일 패스워드 확인 : whereEqulseTo
        var db:FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("emailUsers")
            .whereEqualTo("email", email)
            .whereEqualTo("passwd",passwwd)
            .get().addOnSuccessListener { 
                if(it.documents.size >0) {
                    //로그인 성공
                    var id:String = it.documents[0].id //도큐먼트 명
                    
                    //전역변수 만들어서 사용자정보 여기저기에 쓸수 있도록 만들기
                    G.UserAccount = UserAccount(id, email)
                    
                    //로그인 성공 -> 메인 액티비티로 이동
                    val intent:Intent = Intent(this, MainActivity::class.java)

                    //기존  Task(작업)에 모든 액티비티들 제거하고 새로운 Task시작
                    // (Task 안 백스택 싹 지우기)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    startActivity(intent)
                } else {
                    //로그인 실패
                    AlertDialog.Builder(this).setMessage("일치하는 이메일이 없습니다\n다시 확인 바랍니다").show()

                    binding.etEmail.requestFocus() //이메일에 포커싱
                    binding.etEmail.selectAll()
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}