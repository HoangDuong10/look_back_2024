package com.example.loadimage

sealed class LookBackNavigation(val route: String) {
    data object Screen1: LookBackNavigation("screen1")
    data object Screen2 : LookBackNavigation("screen2")
    data object Screen3: LookBackNavigation("screen3")
    data object Screen4: LookBackNavigation("screen4")
    data object Screen5:LookBackNavigation("screen5")
    data object Screen6:LookBackNavigation("screen6")
    data object Screen7:LookBackNavigation("screen7")
    data object Screen8:LookBackNavigation("screen8")
    data object Screen9:LookBackNavigation("screen9")
    data object Screen10:LookBackNavigation("screen10")
    data object Screen11:LookBackNavigation("screen11")
    data object Screen12:LookBackNavigation("screen12")
}
