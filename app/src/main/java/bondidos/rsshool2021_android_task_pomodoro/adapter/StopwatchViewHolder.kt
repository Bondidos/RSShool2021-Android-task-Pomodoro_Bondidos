package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import bondidos.rsshool2021_android_task_pomodoro.MainActivity
import bondidos.rsshool2021_android_task_pomodoro.R
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchViewHolder(
    private val listener: StopwatchListener,                            // экземпляр интерфейса для передачи действия в Мэйн
    private val resources: Resources,                                   //  Для доступа к ресурсам приложения
    private val binding: StopwatchItemBinding                           // передаем во ViewHolder сгенерированный класс байдинга для разметки элемента
                                                                        // RecyclerView
    ): RecyclerView.ViewHolder(binding.root) {                          // передаем ссылку на View данного элемента RecyclerView

    private var period_circle: Long? = null
    private var timer: CountDownTimer? = null                           // экземпляр класса предоставляющий обратный отчёт
    private var current = 0L    //todo for circle // start of countdown?

    fun bind (stopwatch: Stopwatch){                                    //  в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода
                                                                        // onBindViewHolder адаптера и содержит актуальные параметры для данного элемента списка.
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime() //  пока просто выводим время секундомера.
        period_circle = stopwatch.currentMs


        if (stopwatch.isStarted)
            startTimer(stopwatch)                                       // если у объекта stopwatch флаг isStarted = true, начать отсчёт
        else stopTimer(stopwatch)
        initButtonsListeners(stopwatch)

        //todo set period for filling circle
        binding.customViewOne.setPeriod(period_circle ?: 0)             // устанавливаем период для заполняющегося круга
        binding.customViewTwo.setPeriod(period_circle ?: 0)
        //todo courutine for fill circle
        // таким образом, что бы заставить работать круг в синхронизации с таймером нужно:
        // - надо установить PERIOD_CIRCLE = stopwatch.currentMs в момент создания таймера
        // if(stopwatch.isStarted)
        GlobalScope.launch {
            while (current < period_circle ?: 0 && stopwatch.isStarted) {              // пока текущеезначаение меньше периода оборота круга умноженного на количество раз переключения круга
                current += INTERVAL                                 // интервал добавляем к текущему значению заполнения круга
                binding.customViewOne.setCurrent(current)           // устанавливаем текущее значение кастомным вью
                binding.customViewTwo.setCurrent(current)           //
                delay(INTERVAL)                                     // задержка отрисовки круга
            }
        }
    }


    private fun initButtonsListeners(stopwatch: Stopwatch){                 // назначем лиссенеры и действия кнопкам
        binding.startPauseButton.setOnClickListener {
            if(stopwatch.isStarted)
                listener.stop(stopwatch.id, stopwatch.currentMs)
            else
                listener.start(stopwatch.id)
        }
        binding.restartButton.setOnClickListener { listener.reset(stopwatch.id) }
        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch: Stopwatch){
        val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)
        binding.startPauseButton.setImageDrawable(drawable)             // меняем иконку кнопки

        timer?.cancel()                                                 // Отмена отсчёта
        timer = getCountTimer(stopwatch)                                // получаем экземпляр таймера ( с сохранённым отсчётом )
        timer?.start()                                                  // Старт отсчёта

        binding.blinkingIndicator.isInvisible = false                           // включаем анимацию индикатора
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch){
        val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24) // меняем иконку кнопки
        binding.startPauseButton.setImageDrawable(drawable)

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = false                           // выключаем анимацию индикатора
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountTimer(stopwatch: Stopwatch): CountDownTimer{
        return object : CountDownTimer(period_circle ?: 0, UNIT_TEN_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                Log.d("myLogs","countdown started")
                    stopwatch.currentMs -= interval
                    binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()

            }

            override fun onFinish() {
                Log.d("myLogs","countdown finished")
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
        private const val UNIT_TEN_MS = 100L
        //private const val PERIOD = 1000L * 60L * 60L *24L               // Day

        //todo filling circle

        private const val INTERVAL = 1000L
        //private const val PERIOD_CIRCLE = 1000L * 30 // 30 sec то есть тридцать секунд оборот круга

    }
}