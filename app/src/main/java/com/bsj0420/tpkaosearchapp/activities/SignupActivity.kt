package com.bsj0420.tpkaosearchapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bsj0420.tpkaosearchapp.R
import com.bsj0420.tpkaosearchapp.databinding.ActivitySignupBinding
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //화면 연결
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바 액션바로 설정
        setSupportActionBar(binding.toolbar)
        //액션바에 업 버튼 생성
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //뒤로가기 그림 바꾸기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrowback)
        //타이틀 제거
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignup.setOnClickListener { clickSignUp() }

    }

    private fun clickSignUp() {
        //Firebase > FireStore DB에 사용자 정보 저장
        var email:String = binding.etEmail.text.toString()
        var passwd:String = binding.etPasswd.text.toString()
        var passwdConfirm:String = binding.etPasswdConfirm.text.toString()

        //유효성 검사 - 패스워드가 같은지만 확인
        //코트린은 스트링에 == 비교해도 주소값이 아닌 값 비교 해줌
        if (passwd != passwdConfirm) {
            AlertDialog.Builder(this).setMessage("패스워드가 다릅니다. \n다시 확인해 주세요").show()
            binding.etPasswdConfirm.selectAll() //쓴 글자가 모두 잡혀있는 기능
            return
        }
        
        //파이어스토어 디비에 파이어스토어 얻어오기
        val db = FirebaseFirestore.getInstance() // 이 한줄이면 연동 끝~!
        
        //저장할 데이터 이메일 비밀번호 값 hashMap으로 저장
        val user:MutableMap<String, String> = mutableMapOf()
        user.put("email", email)
        user["passwd"] = passwd //이렇게 쓸 수도 있음

        // 이메일 중복 체크 - 회원 중복 불가
        //.whereEqualTo : db안에 똑같은 애 있음 찾아라
        db.collection("emailUsers").whereEqualTo("email", email)
            .get().addOnSuccessListener {

                //같은 값을 가진 도튜먼트가 있다면 .. size 가 0개 이상일 것이므로 체크
                if(it.documents.size > 0){
                    AlertDialog.Builder(this).setMessage("중복된 아이디 있습니다").show()
                    binding.etEmail.requestFocus() //포커스 올리기
                    binding.etEmail.selectAll() //포커스가 있어야 셀렉올 된다
                } else {
                    //컬렉션의 이름은 emailUsers 로 지정 [RDBMS의 테이블명 같은 역할]

                    //db.collection("emailUsers") //없으면 만들고 있으면 위치 참조

                    //랜덤하게 만들어지는 document명을 회원 ID 값으로 사용 할 예정 document() ()안에 아무것도 안쓰면 랜덤하게 만들어짐
                    //db.collection("emailUsers").document().set(user)
                    //db.collection("emailUsers").add(user) //위에 .document().set(user)를 줄여서 쓴 add() 도튜먼트 명을 랜덤하게 만듦

                    db.collection("emailUsers").add(user).addOnSuccessListener {
                        //저장 됨
                        AlertDialog.Builder(this)
                            .setMessage("축하합니다 \n 회원가입이 완료되었습니다")
                            .setPositiveButton("확인", object : OnClickListener{ //다이아로그의 OnClickListener
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    finish()
                                    //회원가입 화면 종료
                                }
                            }).show()
                    }/////
                }

            }

    }

    //supportActionBar의 콜백 메소드는 onSupportNavigateUp로 해야됨,,,,
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}