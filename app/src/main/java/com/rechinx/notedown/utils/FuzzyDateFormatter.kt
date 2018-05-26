package com.rechinx.notedown.utils

import java.text.SimpleDateFormat
import java.util.*

object FuzzyDateFormatter {

    private val UNIT_MIN: Long = 60 * 1000
    private val UNIT_HOUR: Long = 60 * UNIT_MIN
    private val UNIT_DAY: Long = 24 * UNIT_HOUR
    private val UNIT_WEEK: Long = 7 * UNIT_DAY
    private val UNIT_MONTH: Long = 30 * UNIT_DAY
    private val UNIT_YEAR: Long = 365 * UNIT_DAY

    fun format(time: Long): String {
        var now = System.currentTimeMillis()
        var duration = now - time
        if(duration < UNIT_DAY) {
            val dateFormat = SimpleDateFormat("HH:mm")
            val date = Date(time)
            val timeString = dateFormat.format(date)
            return "今天 $timeString"
        }else if(duration < UNIT_YEAR){
            val dateFormat = SimpleDateFormat("MM月dd日 HH:mm")
            val date = Date(time)
            val timeString = dateFormat.format(date)
            return "$timeString"
        }else {
            val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
            val date = Date(time)
            val timeString = dateFormat.format(date)
            return "$timeString"
        }
    }

}