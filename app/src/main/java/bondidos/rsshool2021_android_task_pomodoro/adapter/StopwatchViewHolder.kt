package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.util.Log
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import bondidos.rsshool2021_android_task_pomodoro.R
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.*

class StopwatchViewHolder(
    private val listener: StopwatchListener,                            // экземпляр интерфейса для передачи действия в Мэйн
    private val resources: Resources,                                   //  Для доступа к ресурсам приложения
    private val binding: StopwatchItemBinding                           // передаем во ViewHolder сгенерированный класс байдинга для разметки элемента
                                                                        // RecyclerView
    ): RecyclerView.ViewHolder(binding.root) {                          // передаем ссылку на View данного элемента RecyclerView

    private var timerStartValue: Long? = null
    private var timer: CountDownTimer? = null                           // экземпляр класса предоставляющий обратный отчёт
    private var current = 0L    //todo for circle // start of countdown?

    fun bind (stopwatch: Stopwatch){                                    //  в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода
                                                                        // onBindViewHolder адаптера и содержит актуальные параметры для данного элемента списка.
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime() //  пока просто выводим время секундомера.
        timerStartValue = stopwatch.currentMs //todo!!! pause -> reset = BUG надо предусмотреть случай при пересоздании вью (пауза)


        if (stopwatch.isStarted)
            startTimer(stopwatch)                                       // если у объекта stopwatch флаг isStarted = true, начать отсчёт
        else stopTimer(stopwatch)

        initButtonsListeners(stopwatch)

        initFillingCircle(timerStartValue ?: 0)

        if (stopwatch.isFinished) {
            binding.root.setBackgroundColor(resources.getColor(R.color.red_second))
            binding.blinkingIndicator.setBackgroundColor(resources.getColor(R.color.red_second))
            binding.startPauseButton.setBackgroundColor(resources.getColor(R.color.red_second))
            binding.restartButton.setBackgroundColor(resources.getColor(R.color.red_second))
            binding.deleteButton.setBackgroundColor(resources.getColor(R.color.red_second))
        } else {
            binding.root.setBackgroundColor(Color.WHITE)
            binding.blinkingIndicator.setBackgroundColor(Color.WHITE)
            binding.startPauseButton.setBackgroundColor(Color.WHITE)
            binding.restartButton.setBackgroundColor(Color.WHITE)
            binding.deleteButton.setBackgroundColor(Color.WHITE)
        }
    }

    private fun initFillingCircle(timerStartValue: Long){
        //todo set period for filling circle
        binding.customViewOne.setPeriod(timerStartValue)             // устанавливаем период для заполняющегося круга
        binding.customViewTwo.setPeriod(timerStartValue)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch){                 // назначем лиссенеры и действия кнопкам
        binding.startPauseButton.setOnClickListener {
            if(stopwatch.isStarted)
                listener.stop(stopwatch.id, stopwatch.currentMs)
            else
                listener.start(stopwatch.id)
        }
        binding.restartButton.setOnClickListener {
            resetViewTimer(stopwatch)
        }
        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }
    private fun resetViewTimer(stopwatch: Stopwatch){
        //todo для сброса я передаю в мэйн установленное за начало отсчёта значение, чтобы пересоздать
        // вью используя это значение, попутно, я сбрасываю на ноль, текущую отрисовку круга (setCurrent && current)
        binding.customViewOne.setCurrent(0)
        binding.customViewTwo.setCurrent(0)
        current = 0L
        listener.reset(stopwatch.id,timerStartValue ?: 0)
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
        return object : CountDownTimer(timerStartValue ?: 0, STEP_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта
            //var index =0

            override fun onTick(millisUntilFinished: Long) {
                //todo нужна организация задержки. Грузить поток отрисовкой вьюхи, каждые 10мс это слишком
                   /* GlobalScope.launch {
                        stepFillingCircle()             // шаг руга отрисовки
                    }*/
                    runBlocking { stepFillingCircle() }

                    setCurrentMs(stopwatch,millisUntilFinished) // заполнение текствью текущими значениями


            }

            override fun onFinish() {
                //todo окончании отсчёта происходит сброс и можно начать сначала
                Log.d("myLogs","countdown finished")
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                resetViewTimer(stopwatch)
                listener.fin(stopwatch.id)
            }
        }
    }
    //todo courutine for fill circle
    // таким образом, что бы заставить работать круг в синхронизации с таймером нужно:
    // - надо установить PERIOD_CIRCLE = stopwatch.currentMs в момент создания таймера
    // if(stopwatch.isStarted)
    suspend fun stepFillingCircle() = withContext(Dispatchers.Default){
        current += STEP_MS
        binding.customViewOne.setCurrent(current)
        binding.customViewTwo.setCurrent(current)
}
    fun setCurrentMs(stopwatch: Stopwatch,millisUntilFinished: Long){
        stopwatch.currentMs = millisUntilFinished
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
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
        private const val STEP_MS = 1000L
        //private const val PERIOD = 1000L * 60L * 60L *24L               // Day

        //todo filling circle
        private const val NULL = 0L
    }
}