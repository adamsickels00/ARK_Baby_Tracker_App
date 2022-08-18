package com.example.arkbabytracker.utils

object TimeDisplayUtil {
    fun secondsToString(sec:Int):String{

        val hours = (sec)/3600
        val minutes = (sec % 3600) / 60
        val seconds = (sec % 60)
        var string = "%02d".format(seconds)

        if(minutes>0 || hours > 0)
            string = "%02d:".format(minutes) + string
        if(hours>0)
            string = "%02d:".format(hours) + string

        return string
    }

    fun secondsToDays(sec: Int): String {
        val days = sec.toFloat() / (3600 * 24).toFloat()
        return "%02.2f days ".format(days)
    }
}