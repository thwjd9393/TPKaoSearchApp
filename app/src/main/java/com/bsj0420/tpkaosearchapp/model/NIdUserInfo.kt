package com.bsj0420.tpkaosearchapp.model

data class NIdUserInfo(var resultcode : String, var message : String, var response : NIdUser)

data class NIdUser(var id:String, var email:String)