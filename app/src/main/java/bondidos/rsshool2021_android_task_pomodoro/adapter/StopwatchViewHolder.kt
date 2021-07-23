package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
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
        //var current = 0L

    fun bind(stopwatch: Stopwatch) {                                    //  в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода

        /** инициализация при первом БИНД*/
            stopwatch_ = stopwatch
            //initButtonsListeners(stopwatch)
            initFillingCircle(stopwatch.msInFuture)
            binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()


    //  пока просто выводим время секундомера.
    }

    fun startTimer(stopwatch: Stopwatch) {
        startAnimation()
        listener.start(stopwatch)
        binding.startPauseButton.text = "STOP"
        Log.d("myLog","startTimer Holder")
    }
    fun startAnimation(){
        Log.d("myLog","startAnimation Holder")
        isAnimationStarted =true
        if(stopwatch.isFinished) changeBackgroundToStandard()
        binding.blinkingIndicator.isInvisible = false                                           // включаем отображение индикатора
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }
    fun stopAnimation(){
        Log.d("myLog","stopAnimation Holder")
        isAnimationStarted = false
        binding.blinkingIndicator.isInvisible = true                                                // выключаем отображение индикатора
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    fun stopTimer(stopwatch: Stopwatch) {
        Log.d("myLog","stopTimer Holder")
        listener.stop(stopwatch)
        binding.startPauseButton.text = "START"
        stopAnimation()                                                                             // выключаем анимацию индикатора
    }
    fun stopOldButton(){
        Log.d("myLog","stopAnimation Holder")
        stopAnimation()
        binding.startPauseButton.text = "START"
    }

    fun onFinish(stopwatch: Stopwatch){
        Log.d("myLogs","$stopwatch")
        stopwatch.currentMs = stopwatch.msInFuture
        setCurrentMs(stopwatch)
        finFillingCircle()
        changeBackgroundToRed()
        changeButtonNameToStart()
        stopAnimation()
        Log.d("myLog","onFinish Holder")
    }

    /**  3 - Смена фона stopwatch если isFinished && stopwatch.currentMs != 0L
     *   если нет, то
     * */
    fun changeButtonNameToStart(){
        binding.startPauseButton.text = "Start"
    }
    fun changeBackgroundToRed() {
        binding.root.setBackgroundColor(resources.getColor(R.color.red_second))
        //binding.blinkingIndicator.setBackgroundColor(resources.getColor(R.color.red_second))
        binding.deleteButton.setBackgroundColor(resources.getColor(R.color.red_second))
    }

    fun changeBackgroundToStandard() {
        binding.root.setBackgroundColor(Color.WHITE)
       // binding.blinkingIndicator.setBackgroundColor(Color.WHITE)
        binding.deleteButton.setBackgroundColor(Color.WHITE)
    }



    fun initFillingCircle(msInFuture: Long) {
        binding.customViewOne.setPeriod(msInFuture)                                                 // устанавливаем период для заполняющегося круга
        binding.customViewTwo.setPeriod(msInFuture)
        val current = stopwatch.msInFuture - stopwatch.currentMs
        binding.customViewOne.setCurrent(current)                                                   // устанавливаем начальное значание
        binding.customViewTwo.setCurrent(current)
    }

    suspend fun stepFillingCircle(stopwatch: Stopwatch) =
        withContext(Dispatchers.Default) {                     // suspend функция. Шаг круга отрисовки
            val current = stopwatch.msInFuture - stopwatch.currentMs
            binding.customViewOne.setCurrent(current)
            binding.customViewTwo.setCurrent(current)
        }
    fun finFillingCircle() {
        val current = stopwatch.msInFuture - stopwatch.currentMs
        binding.customViewOne.setCurrent(current)
        binding.customViewTwo.setCurrent(current)
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

    companion object {
        private const val START_TIME = "00:00:00"
        private const val STEP_MS = 1000L
        private const val myLogs = "myLogs"
    }
}