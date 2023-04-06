package com.bsj0420.tpkaosearchapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bsj0420.tpkaosearchapp.activities.MainActivity
import com.bsj0420.tpkaosearchapp.activities.PlaceUrlActivity
import com.bsj0420.tpkaosearchapp.databinding.FragmentPlaceMapBinding
import com.bsj0420.tpkaosearchapp.model.KakaoSerchPlaceResponce
import com.bsj0420.tpkaosearchapp.model.PlaceDocumentsItem
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.POIItemEventListener

class PlaceMapFragment : Fragment() {

    //private val binding : FragmentPlaceListBinding by lazy { FragmentPlaceListBinding.inflate(layoutInflater) }
    lateinit var binding : FragmentPlaceMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceMapBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
    
    //맵뷰 참조 변수 
    // MapView => mf 거 써야됨
    val mapView : MapView by lazy { MapView(context) } //맵뷰 객체 생성

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //만든 컨테이너에 지도 추가!
        binding.containerMapView.addView(mapView)

        //순서 중요1!!
        //마커나 또는 마커 말풍선을 클릭 했을 때 이벤트에 반응하는 리스너 등록
        //반드시!!! 마커를 추가하는 거 보다 먼저 해야함
        mapView.setPOIItemEventListener(markerEventListener)


        //지도관련 설정 ( 지도 위치 마커 추가 등 )
        setMapAndMarkers()
    }


    private fun setMapAndMarkers() {
        // 맵의 중심 좌표 움직이기
        // 현재 내위치 위도 경도 좌표 가져오기 37.5549, 126.9708
        var lat:Double = (activity as MainActivity).myLocation?.latitude ?: 37.5549
        //메인 거 쓰기
        var log:Double = (activity as MainActivity).myLocation?.longitude ?: 126.9708

        var myMapPoint : MapPoint = MapPoint.mapPointWithGeoCoord(lat, log)
        mapView.setMapCenterPointAndZoomLevel(myMapPoint, 5, true) //위치 조정

        mapView.zoomIn(true)
        mapView.zoomOut(true)

        //마커 찍기! 내위치 표시 마커
        var marker = MapPOIItem()
        marker.apply {
            //this 는 마커
            itemName = "Me"
            mapPoint = myMapPoint
            markerType = MapPOIItem.MarkerType.BluePin
            selectedMarkerType = MapPOIItem.MarkerType.YellowPin

        }

        mapView.addPOIItem(marker)

        //검색 장소들의 마커 추가
        val documents : MutableList<PlaceDocumentsItem> ?
            = (activity as MainActivity).serchPlaceResponce?.documents

        //null이 아니면 마커 찍기  => ?. : 널이 아니면 해라 이거 하나로 끝!
        documents?.forEach {
            val point : MapPoint = MapPoint.mapPointWithGeoCoord(it.y.toDouble(), it.x.toDouble())

            val marker = MapPOIItem().apply{
                mapPoint = point
                itemName = it.place_name
                markerType = MapPOIItem.MarkerType.RedPin
                selectedMarkerType = MapPOIItem.MarkerType.YellowPin

                //마코객체에 보관하고 싶은 데이터가 있다면
                //즉 해당 마커에 관련된 정보를 가지고 있는 객체를 마커에 저장해놓기
                userObject = it
            }

            mapView.addPOIItem(marker)
        }

    }


    //마커 또는 말풍선 클릭 이벤트 리스너 - 반드시 멤버변수위치여야함
    val markerEventListener : POIItemEventListener = object : POIItemEventListener{
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
            //마커를 클릭 했을 때!! 반응
        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
            // 없어짐 아래 메소드로 대체
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {
            // 말풍선 터치 했을 때 반응
            //두번째 파라미터 p1 : 클릭한 마커 객체!
            //if(p1?.userObject == null) return
            p1?.userObject ?: return

            //userObject로부터 정보 얻어오면 중요한게 있음
            //userObject는 setMapAndMarkers()에 검색 장소들의 마커 추가 때 내가 마커정보 저장한 it

            val place : PlaceDocumentsItem = p1?.userObject as PlaceDocumentsItem

            val intnet  = Intent(context, PlaceUrlActivity::class.java)
            intnet.putExtra("place_url", place.place_url)
            startActivity(intnet)
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
            //마커를 드래그 했을 때 발동
        }

    }

}