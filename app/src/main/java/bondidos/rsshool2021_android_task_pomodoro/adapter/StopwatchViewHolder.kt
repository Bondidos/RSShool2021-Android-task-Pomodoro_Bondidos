package bondidos.rsshool2021_android_task_pomodoro.adapter

import androidx.recyclerview.widget.RecyclerView
import bondidos.rsshool2021_android_task_pomodoro.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding                           // передаем во ViewHolder сгенерированный класс байдинга для разметки элемента RecyclerView
): RecyclerView.ViewHolder(binding.root) {                              // передаем ссылку на View данного элемента RecyclerView
    fun bind (stopwatch: Stopwatch){                                 //  в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода
                                                                        // onBindViewHolder адаптера и содержит актуальные параметры для данного элемента списка.
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()  //  пока просто выводим время секундомера.
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
    }
}