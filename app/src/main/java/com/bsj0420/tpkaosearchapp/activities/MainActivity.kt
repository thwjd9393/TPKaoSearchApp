package com.bsj0420.tpkaosearchapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bsj0420.tpkaosearchapp.R
import com.bsj0420.tpkaosearchapp.databinding.ActivityMainBinding
import com.bsj0420.tpkaosearchapp.fragments.PlaceListFragment
import com.bsj0420.tpkaosearchapp.fragments.PlaceMapFragment
import com.bsj0420.tpkaosearchapp.model.KakaoSerchPlaceResponce
import com.bsj0420.tpkaosearchapp.network.RetrofitApiService
import com.bsj0420.tpkaosearchapp.network.RetrofitHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    
    //카카오 검색에 필요한 요청 데이터 : query(검색어, 필수), x(경도), y(위도)
    //1) 검색 장소명
    var searchQuery:String = "화장실" //앱의 초기 검색어 , 내 주변에 개방된 화장실
    //2) 현재 내 위치 정보 객체 (위도 & 경도 정보를 멤버로 보유한 객체 : 안드로이드에 있음 - Location)
    var myLocation : Location? = null //null로 주면 서울 시청으로 나옴

    //내 위치 얻어오기 퓨즈드를 이용해서
    //구글의 Fused Location API 사용 : play-service-location
    //FusedLocationProviderClient는 컨텍스트가 필요함 컨텍스트는 멤버변수로 못쓰기 떄문
    val providerClient : FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    //검색결과 응답객체 참조변수
    var serchPlaceResponce : KakaoSerchPlaceResponce? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바로 제목줄로 대체 옵션메뉴 가지려고
        setSupportActionBar(binding.toolbar)

        //1. 첫번째로 보여질 프래그먼트 동적 추가
        supportFragmentManager.beginTransaction().add(R.id.container_fragment, PlaceListFragment()).commit()

        //2.탭 레이아웃에 탭버튼 클릭 시 보여줄 플래그먼트 변경
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //탭이 눌려질 때
                if(tab?.text == "LIST"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceListFragment()).commit()
                } else if (tab?.text == "MAP") {
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceMapFragment()).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            //검색기능 만들기

        })

        //소프트 키보드의 검색 버튼을 클릭하였을 때
//        binding.etSearch.setOnEditorActionListener(object : OnEditorActionListener{
//            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
//                // p2 => 키보드 이벤트 
//                // 리턴값 true하면 액티비티에게 (부모) 에게 키보드 이벤트가 전달이 안된다 
//            }
//        })

        //샘변환
        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent -> 
            searchQuery = binding.etSearch.text.toString()

            //카카오 검색 API를 이용하여 장소를 검색하기
            searchPlace()

            false // return 키워드 쓰면 안됨 그냥 냅다 리턴값 쓴다
        }

        //특정 키워드로 된 단축 검색 버튼에 리스너 처리하는 함수 호출
        setChoiceBtnListener()

        //내 위치 정보 제공에 개한 동적 퍼미션 요청
        //파인과 ACCESS_COARSE_LOCATION 둘중 하나만 퍼미션 받으면 됨
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED) {
            //퍼미션 요청 대행사를 사용한 계약 체결
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            //내 위치 요청
            requestMyLocation()
        }

        //내 위치 재조정
        binding.ivMyLocation.setOnClickListener { requestMyLocation() }


    }//onCreate
    
    //퍼미션 요청 대행사 계약 및 등록 - 스트링 이용
    val permissionLauncher : ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission(),object : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean?) {
            
            if(result!!) requestMyLocation()
            else Toast.makeText(this@MainActivity, "위치 정보 제공에 동의하지 않았습니다. 검색 기능이 제한됩니다", Toast.LENGTH_SHORT).show()
            
        }
    })

    //내 위치 요청 작업 메소드
    private fun requestMyLocation() {
        
        //구글을 쓰기위해 프로바이더 써야함 - 멤버변수로 만듦

        //위치 검색 기준 설정하는 요청 객체
        val requestFailReason : LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()

        // 실시간 위치정보 갱신 요청
        // 퍼미션체크가 현재 서로 다른 메소드이기 때문에 퍼미션 받았는지 모름 퍼미션 체크를 받아야함
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        providerClient.requestLocationUpdates(requestFailReason, locationCallback, Looper.getMainLooper())
        
    }

    //위치검색 결과 콜백 객체
    private val locationCallback : LocationCallback = object  : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation = p0.lastLocation

            //위치 탑색 끝났으면 실시간 업데이트 종료
            providerClient.removeLocationUpdates(this) //this = locationCallback

            //위치 얻었으니 검색 시작
            searchPlace()
        }
    }



    //카카오 장소 검색 API를 파싱하는 작업 메소드
    private fun searchPlace() {
        //Toast.makeText(this, "$searchQuery - ${myLocation?.latitude}, ${myLocation?.longitude}", Toast.LENGTH_SHORT).show()

        //카카오 keword plase serch api 사용 REST API 작업 - Retrofit 사용
        //1. 클래스로 만들어 놓은 RetrofitHelper를 사용
         val retrofit : Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        //2. 서비스 객체
        val retrofitApiService =retrofit.create(RetrofitApiService::class.java)
        retrofitApiService.searchPlace(searchQuery, myLocation?.latitude.toString(), myLocation?.longitude.toString())
            .enqueue(object : Callback<KakaoSerchPlaceResponce>{
                override fun onResponse(
                    call: Call<KakaoSerchPlaceResponce>,
                    response: Response<KakaoSerchPlaceResponce>
                ) {
                    serchPlaceResponce = response.body()

                    //무조건 검색이 완료가 되면 listFragment 보여주도록 한다 목록이 먼저 나오도록
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceListFragment()).commit()
                    //갱신의 의미가 없음 아예 뜯어내고 다시 만드는 거라서

                    //현재 탭버튼과 프래그먼트가 연결이 안되어 있어서 탭을 변경 해주기
                    binding.tabLayout.getTabAt(0)?.select()

                }

                override fun onFailure(call: Call<KakaoSerchPlaceResponce>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
                }
            })

    }



    //특정 키워드로 된 단축 검색 버틀 리스너 처리
    private fun setChoiceBtnListener() {
        binding.layoutChoice.choiceWc.setOnClickListener { clickChoice(it) } //it => 클릭 된 녀석
        binding.layoutChoice.choiceMovie.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceGas.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceEv.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePark.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePharmacy.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceCoffee.setOnClickListener { clickChoice(it) }

    }
    
    //멤버변수로 아이디 저장하는 변수 하나 생성
    var choiceID = R.id.choice_wc 

    private fun clickChoice(view: View) { //view : 현재 클릭된 녀석
        //기존 선택된 버튼을 찾아 배경을 흰생 원으로 바꾸기
        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice)

        //현재 클릭된 배경 그림은 회색으로 변경
        view.setBackgroundResource(R.drawable.bg_choice_select)

        //다음 클릭 시 이전 클릭된 뷰의 아이디를 기억하도록..
        choiceID = view.id 
        
        //초이스한 것에 따라 검색장소 명을 변경허여 다시 재검색
        when(choiceID ) {
            R.id.choice_wc -> searchQuery = "화장실"
            R.id.choice_movie -> searchQuery = "영화관"
            R.id.choice_gas -> searchQuery = "주유소"
            R.id.choice_ev -> searchQuery = "전기차 충전소"
            R.id.choice_pharmacy -> searchQuery = "약국"
            R.id.choice_park -> searchQuery = "화장실"
            R.id.choice_food -> searchQuery = "맛집"
            R.id.choice_coffee -> searchQuery = "카페"
        }

        //새로운 검색 시작
        searchPlace()

        //검색창에 글씨가 있다면 지우기
        binding.etSearch.text.clear()
        binding.etSearch.clearFocus()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.aaa -> Toast.makeText(this, "검색 장소를 입력하세요", Toast.LENGTH_SHORT).show()
            R.id.bbb -> Toast.makeText(this, "Retrofit. Glide. kakaoAPI, NaverAPI, GoogleAPI, GSON", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }

}