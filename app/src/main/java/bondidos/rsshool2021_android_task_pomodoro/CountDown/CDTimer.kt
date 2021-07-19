package bondidos.rsshool2021_android_task_pomodoro.CountDown

import android.os.CountDownTimer
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch

class CDTimer() {

    private var listener: StopwatchListener? = null
    private var timer: CountDownTimer? = null

    /**  ВСЕ СОСТОЯНИЯ ТАЙМЕРОВ ХРАНИМ В ДАТА КЛАССЕ И ИСПОЛЬЗУЕМ ЕГО ДЛЯ УПРАВЛЕНИЯ ТАЙМЕРОМ
    *
    */
    private fun getCountTimer(stopwatch: Stopwatch,
                              stopwatchAdapter: StopwatchAdapter,
                              stopwatches: MutableList<Stopwatch>
                              ): CountDownTimer{
        return object : CountDownTimer(stopwatch.msInFuture, STEP_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs = millisUntilFinished
                stopwatchAdapter.submitList(stopwatches.toList())
            }

            override fun onFinish() {

            }
        }
    }
    //todo here we starting countdown.
    fun startTimer(stopwatch: Stopwatch,stopwatchAdapter: StopwatchAdapter,stopwatches: MutableList<Stopwatch> ){
        timer?.cancel()                                                 // Отмена отсчёта
        timer = getCountTimer(stopwatch,stopwatchAdapter,stopwatches)                                // получаем экземпляр таймера ( с сохранённым отсчётом )
        timer?.start()                                                  // Старт отсчёта
    }

    fun stopTimer(stopwatch: Stopwatch){
        timer?.cancel()
    }
    private fun resetTimer(stopwatch: Stopwatch){

    }
    companion object{
        private const val STEP_MS = 10L
    }
}