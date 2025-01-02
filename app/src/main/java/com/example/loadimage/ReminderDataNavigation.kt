package com.example.loadimage

import com.google.common.util.concurrent.FakeTimeLimiter

data class ReminderDataNavigation(
    val steps: Int,
    var currentStep: Int,
    var data: FakeData? = null,
    val isShowFull: Boolean = false,
//    val video : Pair<String, Long>?=null
)
data class FakeData(
    val order : String,
    val topNhaBan : String,
    val doanhthu: String,
    val thang : String,
    val name : String,
    val slKhachHang:String,
    val topYeuThich : String,
    val khachHang : String,
    val danhGiaKH : String,
    val danhGiaCuaBan : String,
    val soLanSD : String
)
