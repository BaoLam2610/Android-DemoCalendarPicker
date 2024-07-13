package com.lambao.calendar_picker.view

import android.view.View
import com.kizitonwose.calendar.view.ViewContainer
import com.lambao.calendar_picker.databinding.LayoutCalendarDayBinding

class DayViewContainer(view: View) : ViewContainer(view) {
    private val binding = LayoutCalendarDayBinding.bind(view)

    val tvDay = binding.tvDay
    val continuousBackgroundView = binding.continuousBackgroundView
    val roundBackgroundView = binding.roundBackgroundView
}