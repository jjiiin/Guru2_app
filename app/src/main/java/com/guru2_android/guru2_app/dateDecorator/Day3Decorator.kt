package com.guru2_android.guru2_app.dateDecorator

import android.graphics.Color
import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
//최종평가에서 <최고예요!>를 받았을 때 병아리 달력에 빨간색 원이 날짜 밑에 뜨는 클래스
class Day3Decorator (val items:MutableList<CalendarDay>): DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return items.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {

        view?.addSpan(DotSpan(12F, Color.parseColor("#FF8980")))
    }


}