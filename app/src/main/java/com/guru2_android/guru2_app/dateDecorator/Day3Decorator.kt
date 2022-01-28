package com.guru2_android.guru2_app.dateDecorator

import android.graphics.Color
import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class Day3Decorator (val items:MutableList<CalendarDay>): DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        Log.d("Deco11",items.toString())
        return items.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
//        view?.addSpan(StyleSpan(Typeface.BOLD))
//        view?.addSpan(RelativeSizeSpan(1.4f))
//        view?.addSpan(ForegroundColorSpan(Color.parseColor("#1D872A")))
        view?.addSpan(DotSpan(12F, Color.parseColor("#FF8980")))
    }


}