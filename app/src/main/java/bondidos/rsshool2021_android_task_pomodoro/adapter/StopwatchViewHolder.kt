package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.os.CountDownTimer
import androidx.recyclerview.widget.RecyclerView
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding

class StopwatchViewHolder(

    private val binding: StopwatchItemBinding                           // передаем во ViewHolder сгенерированный класс байдинга для разметки элемента
                                                                        // RecyclerView

    ): RecyclerView.ViewHolder(binding.root) {                              // передаем ссылку на View данного элемента RecyclerView
    fun bind (stopwatch: Stopwatch){                                 //  в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода
                                                                        // onBindViewHolder адаптера и содержит актуальные параметры для данного элемента списка.
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()  //  пока просто выводим время секундомера.
        if (stopwatch.isStarted) startTimer(stopwatch)                      // если у объекта stopwatch флаг isStarted = true, начать отсчёт
    }

    private var timer: CountDownTimer? = null                           // экземпляр класса предоставляющий обратный отчёт

    private fun startTimer(stopwatch: Stopwatch){
        timer?.cancel()                                                 // Отмена отсчёта
        timer = getCountTimer(stopwatch)                                // получаем экземпляр таймера ( с сохранённым отсчётом )
        timer?.start()                                                  // Старт отсчёта
    }

    private fun getCountTimer(stopwatch: Stopwatch): CountDownTimer{
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs += interval
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }
        }
    }

    private fun Long.displayTime(): String{                             // данный метод расширения для Long конвертирует текущее значение таймера в миллисекундах
                                                                        // в формат “HH:MM:SS:MsMs” и возвращает соответствующую строку
        if(this <= 0L) return START_TIME

        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        val ms = this % 1000 / 10

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
    }
    private fun displaySlot(count: Long): String{
        return if (count / 10L > 0) {
            "$count"
        } else "0$count"
    }

    companion object{
        private const val START_TIME = "00:00:00:00"
        private const val UNIT_TEN_MS = 10L
        private const val PERIOD = 1000L * 60L * 60L *24L               // Day
    }
}