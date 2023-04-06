package com.bsj0420.tpkaosearchapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bsj0420.tpkaosearchapp.activities.PlaceUrlActivity
import com.bsj0420.tpkaosearchapp.databinding.RecyclerItemListFragmentBinding
import com.bsj0420.tpkaosearchapp.model.PlaceDocumentsItem

class PlaceListRecyclerAdaper(var context: Context, var documents : MutableList<PlaceDocumentsItem>) : Adapter<PlaceListRecyclerAdaper.VH>() {

    //ViewHolder는 만든 아이템 레이아웃을 생성자로 줘야함
    inner class VH(var binding : RecyclerItemListFragmentBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RecyclerItemListFragmentBinding.inflate(LayoutInflater.from(context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        val place : PlaceDocumentsItem = documents[position]

        holder.binding.tvPlaceName.text = place.place_name
//        if(place.road_address_name == "") holder.binding.tvAddress.text = place.address_name
//        else holder.binding.tvAddress.text = place.road_address_name

        holder.binding.tvAddress.text = if (place.road_address_name == "") place.address_name else place.road_address_name

        holder.binding.tvDistance.text = "${place.distance}m" //서식문자 만들기 -> 문자열 템플릿
        
        // 상세 페이지로 이동
        holder.binding.root.setOnClickListener { 
            //주소를 가지고 웹뷰화면으로 이동

            val intent : Intent = Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("place_url", place.place_url) //값 같이 넘겨줌
            context.startActivity(intent)

        }

    }

}