package com.bsj0420.tpkaosearchapp.model

data class KakaoSerchPlaceResponce(var meta : PlaceMeta, var documents : MutableList<PlaceDocumentsItem>)

data class PlaceMeta(var total_count : Int, var pageable_count : Int, var is_end : Boolean)

data class PlaceDocumentsItem(
    var id : String,
    var place_name : String,
    var phone : String,
    var address_name : String,
    var road_address_name : String,
    var x : String, //longitude (경도)
    var y : String, // latitude(위도)
    var place_url : String,
    var distance : String, //중심 좌표까지의 거리 단, 요청파라미터로 x,y 줬을 때만 나옴 단위는 m
)