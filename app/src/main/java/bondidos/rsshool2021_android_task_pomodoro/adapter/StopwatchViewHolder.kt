package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.MainListener
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
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


    val startPauseButton = binding.startPauseButton
    val blinkingIndicator = binding.blinkingIndicator
    val deleteButton = binding.deleteButton
    val customViewOne = binding.customViewOne
    val customViewTwo = binding.customViewTwo
    private var stopwatch_: Stopwatch? = null
    val stopwatch get() = requireNotNull(stopwatch_)
    var isAnimationStarted = false
    var current = 0L

    fun bind(stopwatch: Stopwatch) {                                    //  в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода

        /** инициализация при первом БИНД*/
            stopwatch_ = stopwatch
            //initButtonsListeners(stopwatch)
            initFillingCircle(stopwatch.msInFuture)
            binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()


    //  пока просто выводим время секундомера.
    }
    fun getAdapterPos() = adapterPosition


    fun startTimer(stopwatch: Stopwatch) {
        /** Так как холдер будет обновляться раз в 10мс, то надо предусмотреть случай, если отсчёт уже запущен.
         * То есть нам не нужно включать анимацию и менять иконку кнопки старт/стоп */
        //if (!binding.blinkingIndicator.isActivated) {                                               // проверяем статус индикатора (запущен или нет)

        listener.start(stopwatch)

        binding.startPauseButton.text = "STOP"
        isAnimationStarted =true
        binding.blinkingIndicator.isInvisible = false                                           // включаем отображение индикатора
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()                   // включаем анимацию индикатора
        //  }
    }

    fun stopTimer(stopwatch: Stopwatch) {

        listener.stop(stopwatch)

        binding.startPauseButton.text = "START"
        isAnimationStarted = false
        binding.blinkingIndicator.isInvisible = true                                                // выключаем отображение индикатора
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()                        // выключаем анимацию индикатора
    }
    fun stopAnimation(stopwatch: Stopwatch){
        stopwatch.isStarted = false
        isAnimationStarted = true
        binding.startPauseButton.text = "START"
        binding.blinkingIndicator.isInvisible = true                                                // выключаем отображение индикатора
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()                        // выключаем анимацию индикатора
    }


    fun initFillingCircle(msInFuture: Long) {


        binding.customViewOne.setPeriod(msInFuture)                                                 // устанавливаем период для заполняющегося круга
        binding.customViewTwo.setPeriod(msInFuture)

        binding.customViewOne.setCurrent(current)                                                   // устанавливаем начальное значание
        binding.customViewTwo.setCurrent(current)
    }

    suspend fun stepFillingCircle() =
        withContext(Dispatchers.Default) {                     // suspend функция. Шаг круга отрисовки
            current += STEP_MS
            binding.customViewOne.setCurrent(current)
            binding.customViewTwo.setCurrent(current)
        }

    /**  3 - Смена фона stopwatch если isFinished && stopwatch.currentMs != 0L
     *   если нет, то
     * */
    fun changeBackgroundToRed() {
        binding.root.setBackgroundColor(resources.getColor(R.color.red_second))
        binding.blinkingIndicator.setBackgroundColor(resources.getColor(R.color.red_second))
        binding.deleteButton.setBackgroundColor(resources.getColor(R.color.red_second))
    }

    fun changeBackgroundToStandard() {
        binding.root.setBackgroundColor(Color.WHITE)
        binding.blinkingIndicator.setBackgroundColor(Color.WHITE)
        binding.deleteButton.setBackgroundColor(Color.WHITE)
    }

    /** 4 - проверить логику отрисовки текущего значения
     * по прибытии обновлённого stopwatch срисовываем его current в textView в заданном формате
     * */

    fun setCurrentMs(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        //Log.d(myLogs,"setCurrentMs ${stopwatch.currentMs} displayTime ${stopwatch.currentMs.displayTime()}")
    }

    private fun Long.displayTime(): String {                             // данный метод расширения для Long конвертирует текущее значение таймера в миллисекундах
        // в формат “HH:MM:SS:MsMs” и возвращает соответствующую строку
        if (this <= 0L) return START_TIME

        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        //val ms = this % 1000 / 10

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"//:${displaySlot(ms)
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else "0$count"
    }

    /**  По нажатию кнопок запускаем соответствующие методы в мэйне
     * */
    private fun initButtonsListeners(stopwatch: Stopwatch) {                 // назначем лиссенеры и действия кнопкам
        startPauseButton.setOnClickListener {
            if(stopwatch.isStarted) {
                stopTimer(stopwatch)
                Log.d("myLogs","stop(holder)")

            } else {
                startTimer(stopwatch)
                Log.d("myLogs","start(holder)")
            }
        }
        deleteButton.setOnClickListener {
            Log.d("myLogs","deleteButton(Adapter)")
            listener.delete(stopwatch)
        }
    }

    companion object {
        private const val START_TIME = "00:00:00"
        private const val STEP_MS = 1000L
        private const val myLogs = "myLogs"
    }
}