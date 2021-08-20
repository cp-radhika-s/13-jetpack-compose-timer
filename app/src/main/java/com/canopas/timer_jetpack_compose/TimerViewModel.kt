package com.canopas.timer_jetpack_compose

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    private var countDownTimer: CountDownTimer? = null
    private val _seconds = MutableLiveData(0)

    val seconds: LiveData<Int>
        get() = _seconds

    private val _minutes = MutableLiveData(0)

    val minutes: LiveData<Int>
        get() = _minutes

    private val _hours = MutableLiveData(0)

    val hours: LiveData<Int>
        get() = _hours


    private val _isRunning = MutableLiveData(false)

    val isRunning: LiveData<Boolean>
        get() = _isRunning

    private val _progress = MutableLiveData(1f)

    val progress: LiveData<Float>
        get() = _progress

    private val _time = MutableLiveData("00:00:00")
    val time: LiveData<String>
        get() = _time

    var totalTime = 0L

    fun startTimer() {
        if (countDownTimer != null) {
            stopTimer()
        }
        totalTime = (getSeconds() * 1000).toLong()

        countDownTimer = object  :CountDownTimer(totalTime,1000){
            override fun onTick(millis: Long) {
                val secs = (millis / MSECS_IN_SEC % SECS_IN_MINUTES).toInt()
                if (secs != seconds.value) {
                    _seconds.postValue(secs)
                }

                val minutes = (millis / MSECS_IN_SEC / SECS_IN_MINUTES % SECS_IN_MINUTES).toInt()
                if (minutes != this@TimerViewModel.minutes.value) {
                    _minutes.postValue(minutes)
                }

                val hours = (millis / MSECS_IN_SEC / MINUTES_IN_HOUR / SECS_IN_MINUTES).toInt()
                if (hours != this@TimerViewModel.hours.value) {
                    _hours.postValue(hours)
                }

                _progress.postValue(millis.toFloat() / totalTime.toFloat())
                _time.postValue(formateTime(hours, minutes, secs))
            }

            override fun onFinish() {
                _progress.postValue(1.0f)
                _isRunning.postValue(false)
            }
        }
        countDownTimer?.start()
        _isRunning.postValue(true)
    }


    fun stopTimer() {
        countDownTimer?.cancel()
        _isRunning.postValue(false)
    }

    fun updateTime(timeUnit: TimeUnit, timeOperator: TimeOperator) {
        var seconds = seconds.value ?: 0
        var minutes = minutes.value ?: 0
        var hours = hours.value ?: 0

        when (timeUnit) {
            TimeUnit.SEC -> {
                seconds = calculateTime(seconds, timeOperator).coerceIn(0, 59)
            }
            TimeUnit.MIN ->{
                minutes = calculateTime(minutes, timeOperator).coerceIn(0, 59)
            }
            TimeUnit.HOUR ->{
                hours = calculateTime(hours, timeOperator).coerceIn(0, 23)
            }
        }

        // update time
        _seconds.postValue(seconds)
        _minutes.postValue(minutes)
        _hours.postValue(hours)

        _time.postValue(formateTime(hours, minutes, seconds))
    }

    private fun calculateTime(currentValue: Int, timeOperator: TimeOperator): Int {
        return when (timeOperator) {
            TimeOperator.INCREASE -> currentValue + 1
            TimeOperator.DECREASE -> currentValue - 1
        }
    }
    private fun formateTime(hours : Int, minutes : Int, seconds : Int) =
        String.format("%02d:%02d:%02d", hours, minutes, seconds)

    private fun getSeconds() = ((hours.value ?: 0) * MINUTES_IN_HOUR * SECS_IN_MINUTES) + ((minutes.value
        ?: 0) * SECS_IN_MINUTES) + (seconds.value ?: 0)

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
    companion object {
        enum class TimeOperator {
            INCREASE, DECREASE
        }

        enum class TimeUnit {
            SEC, MIN, HOUR
        }

        const val MINUTES_IN_HOUR = 60
        const val SECS_IN_MINUTES = 60
        const val MSECS_IN_SEC = 1000
    }

}