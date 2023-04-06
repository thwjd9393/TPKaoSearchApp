package com.bsj0420.tpkaosearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bsj0420.tpkaosearchapp.activities.MainActivity
import com.bsj0420.tpkaosearchapp.adapters.PlaceListRecyclerAdaper
import com.bsj0420.tpkaosearchapp.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {

    //private val binding : FragmentPlaceListBinding by lazy { FragmentPlaceListBinding.inflate(layoutInflater) }
    lateinit var binding : FragmentPlaceListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceListBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //메인 액티비티에 있는 대량의 데이터를 소환!!!!
        val ma : MainActivity = requireActivity() as MainActivity //메인 액티비티로 형변환
        
        //주의) 서버에서 데이터 가져올 때 메인에 화면이 붙을 때와 싱크를 맞춰줘야함
        //1.
        //if(ma.serchPlaceResponce == null) return
        //2. 엘비스로 바꾸기
        //ma.serchPlaceResponce ?: return
        //아답터 연결
        //binding.recyclerList.adapter = PlaceListRecyclerAdaper(requireActivity(), ma.serchPlaceResponce!!.documents)

        //3. 스코트 함수로 더 줄이기
        // requireActivity() :: context가 null이면 알아서 null주는 애
        ma.serchPlaceResponce?.apply {
            binding.recyclerList.adapter = PlaceListRecyclerAdaper(requireActivity(), documents)
            //이 스코트 함수에서 this는 serchPlaceResponce이고 this는 생략 가능
            // this.documents -> documents
        }


    }

}