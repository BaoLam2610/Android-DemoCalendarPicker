package com.lambao.calendar_picker.binder

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.lambao.calendar_picker.R
import com.lambao.calendar_picker.helper.ContinuousSelectionHelper.getSelection
import com.lambao.calendar_picker.helper.ContinuousSelectionHelper.isInDateBetweenSelection
import com.lambao.calendar_picker.helper.ContinuousSelectionHelper.isOutDateBetweenSelection
import com.lambao.calendar_picker.helper.DateSelection
import com.lambao.calendar_picker.view.DayViewContainer
import java.time.LocalDate

open class CalendarBinder(
    private val calendarView: CalendarView
) : MonthDayBinder<DayViewContainer> {

    private var selectedDate: LocalDate? = null
    private var selection = DateSelection()
    private val today = LocalDate.now()

    // Config selection date
    val clipLevelHalf = 5000
    val ctx = calendarView.context
    val rangeStartBackground =
        ContextCompat.getDrawable(ctx, R.drawable.example_4_continuous_selected_bg_start).also {
            it?.level = clipLevelHalf // Used by ClipDrawable
        }!!
    val rangeEndBackground =
        ContextCompat.getDrawable(ctx, R.drawable.example_4_continuous_selected_bg_end).also {
            it?.level = clipLevelHalf // Used by ClipDrawable
        }!!
    val rangeMiddleBackground =
        ContextCompat.getDrawable(ctx, R.drawable.example_4_continuous_selected_bg_middle)!!
    val singleBackground = ContextCompat.getDrawable(ctx, R.drawable.example_4_single_selected_bg)!!
    val todayBackground = ContextCompat.getDrawable(ctx, R.drawable.example_4_today_bg)!!

    override fun create(view: View) = DayViewContainer(view)

    override fun bind(container: DayViewContainer, data: CalendarDay) {

        /*
        // Single click
        container.tvDay.text = data.date.dayOfMonth.toString()

        container.view.setOnClickListener {
            // Check the day position as we do not want to select in or out dates.
            if (data.position == DayPosition.MonthDate) {
                // Keep a reference to any previous selection
                // in case we overwrite it and need to reload it.
                val currentSelection = selectedDate
                if (currentSelection == data.date) {
                    // If the user clicks the same date, clear selection.
                    selectedDate = null
                    // Reload this date so the dayBinder is called
                    // and we can REMOVE the selection background.
                    calendarView.notifyDateChanged(currentSelection)
                } else {
                    selectedDate = data.date
                    // Reload the newly selected date so the dayBinder is
                    // called and we can ADD the selection background.
                    calendarView.notifyDateChanged(data.date)
                    if (currentSelection != null) {
                        // We need to also reload the previously selected
                        // date so we can REMOVE the selection background.
                        calendarView.notifyDateChanged(currentSelection)
                    }

                }
            } else {
                selectedDate = data.date
                val month = YearMonth.from(data.date)
                calendarView.smoothScrollToMonth(month)
                calendarView.notifyCalendarChanged()
            }
        }

        container.tvDay.setBackgroundResource(0)

        if (selectedDate != null && selectedDate == data.date && data.position == DayPosition.MonthDate) {
            container.tvDay.setTextColor(Color.BLACK)
            container.tvDay.setBackgroundResource(R.drawable.bg_circle_selected)
            return
        }

        if (data.date == LocalDate.now()) {
            container.tvDay.setTextColor(Color.RED)
            return
        }

        if (data.position == DayPosition.MonthDate) {
            container.tvDay.setTextColor(Color.WHITE)
        } else {
            container.tvDay.setTextColor(Color.GRAY)
        }
        */

        /// Config continuous selection
        container.tvDay.text = null
        container.roundBackgroundView.makeInVisible()
        container.continuousBackgroundView.makeInVisible()
        val (startDate, endDate) = selection

        when (data.position) {
            DayPosition.MonthDate -> {
                container.tvDay.text = data.date.dayOfMonth.toString()
                if (data.date.isBefore(today)) {
                    container.tvDay.setTextColor(Color.GRAY)
                } else {
                    when {
                        startDate == data.date && endDate == null -> {
                            container.tvDay.setTextColor(Color.WHITE)
                            container.roundBackgroundView.applyBackground(singleBackground)
                        }

                        data.date == startDate -> {
                            container.tvDay.setTextColor(Color.WHITE)
                            container.continuousBackgroundView.applyBackground(rangeStartBackground)
                            container.roundBackgroundView.applyBackground(singleBackground)
                        }

                        startDate != null && endDate != null && (data.date > startDate && data.date < endDate) -> {
                            container.tvDay.setTextColor(Color.GRAY)
                            container.continuousBackgroundView.applyBackground(rangeMiddleBackground)
                        }

                        data.date == endDate -> {
                            container.tvDay.setTextColor(Color.WHITE)
                            container.continuousBackgroundView.applyBackground(rangeEndBackground)
                            container.roundBackgroundView.applyBackground(singleBackground)
                        }

                        data.date == today -> {
                            container.tvDay.setTextColor(Color.GRAY)
                            container.roundBackgroundView.applyBackground(todayBackground)
                        }

                        else -> container.tvDay.setTextColor(Color.GRAY)
                    }
                }
            }
            // Make the coloured selection background continuous on the
            // invisible in and out dates across various months.
            DayPosition.InDate ->
                if (startDate != null && endDate != null &&
                    isInDateBetweenSelection(data.date, startDate, endDate)
                ) {
                    container.continuousBackgroundView.applyBackground(rangeMiddleBackground)
                }

            DayPosition.OutDate ->
                if (startDate != null && endDate != null &&
                    isOutDateBetweenSelection(data.date, startDate, endDate)
                ) {
                    container.continuousBackgroundView.applyBackground(rangeMiddleBackground)
                }
        }

        container.view.setOnClickListener {
            if (data.position == DayPosition.MonthDate &&
                (data.date == today || data.date.isAfter(today))
            ) {
                selection = getSelection(
                    clickedDate = data.date,
                    dateSelection = selection,
                )
                calendarView.notifyCalendarChanged()
            }
        }

    }
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.applyBackground(drawable: Drawable) {
    makeVisible()
    background = drawable
}
