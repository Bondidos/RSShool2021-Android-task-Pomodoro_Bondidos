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
    ): RecyclerView.ViewHolder(binding.root){                          // передаем ссылку на View данного элемента RecyclerView

    var current = 0L                                             //todo for circle // start of countdown?
    /** пробую новый холдер*/
    /**--------------------------------------------------*/



    /**-------------------------------------------------------*/
    val startPauseButton = binding.startPauseButton
    val restartButton = binding.restartButton
    val blinkingIndicator = binding.blinkingIndicator
    val deleteButton = binding.deleteButton
    val customViewOne = binding.customViewOne
    val customViewTwo = binding.customViewTwo
    var runFlag =false
   // var stopwatch: Stopwatch? =null


    fun bind (stopwatch: Stopwatch){                                    //  в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода

        /** Надо реализовать логику поведения при пересоздании объекта stopwatch, т.е.:
         * 1 - Смена изображения кнопки старт/пауза при получении isStarted
         * 2 - 2.1 Организация работы кружка
         *     2.2 Сброс кгружка на исходное состояние при сбросе. Узнаём, что сброс произошёл путём сравнивания
         * currentMs . Если current == 0 то состояние заполняющегося кружка должно быть исходным
         * 3 - Смена фона stopwatch если isFinished
         * 4 - проверить логику отрисовки текущего значения
         * 5 - Отрисовка мигающего кружка. Довести до ума
         * */
       // Log.d(myLogs,"bindHolder")
       // stopwatchManager(stopwatch)
       // binding.startPauseButton.isActivated = false
        initFillingCircle(stopwatch.currentMs)

       // setCurrentMs(stopwatch)                                                                     // отображаем текущее значение таймера
        //initButtonsListeners(stopwatch)                                                             /** инициализация кнопок*/


       // binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()                             //  пока просто выводим время секундомера.
    }

        private fun stopwatchManager(stopwatch: Stopwatch){
        when{
            /** Если stopwatch таймер только что создан, то заполняющийся кружок нуждается в
             * инициализации периода и если холдер переиспользуется то сброс кружка на начальное
             * состояние, то есть на ноль*/
           // stopwatch.currentMs == 0L -> initFillingCircle(stopwatch.msInFuture)                    // инициализация и сброс кружка

            stopwatch.isStarted -> {                                                                // if isStarted

                startTimer()                                                                        // сменить иконку кнопки старт/стоп, анимация индикатора
                runBlocking { stepFillingCircle() }                                                 // корутина, шаг заполняющегося кружка
            }

            !stopwatch.isStarted -> stopTimer()                                                     // стоп

            /**3 - Смена фона stopwatch если isFinished*/
            stopwatch.isFinished -> changeBackgroundToRed()
            /**3 - Возвращаем фон если начинаем новый отсчёт, при этом метод запустится только 1 раз в начале*/
            !stopwatch.isFinished && stopwatch.currentMs == 0L -> changeBackgroundToStandard()
        }

    }

    fun startTimer(){
        /** Так как холдер будет обновляться раз в 10мс, то надо предусмотреть случай, если отсчёт уже запущен.
         * То есть нам не нужно включать анимацию и менять иконку кнопки старт/стоп */
        //if (!binding.blinkingIndicator.isActivated) {                                               // проверяем статус индикатора (запущен или нет)
            val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)                   // находим иконку паузы
            binding.startPauseButton.setImageDrawable(drawable)                                     // меняем иконку кнопки пока идёт отсчёт

            binding.blinkingIndicator.isInvisible = false                                           // включаем отображение индикатора
            (binding.blinkingIndicator.background as? AnimationDrawable)?.start()                   // включаем анимацию индикатора
      //  }
    }

    fun stopTimer(){
        val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)                  // меняем иконку кнопки
        binding.startPauseButton.setImageDrawable(drawable)

        binding.blinkingIndicator.isInvisible = true                                                // выключаем отображение индикатора
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()                        // выключаем анимацию индикатора
    }

    fun initFillingCircle(msInFuture: Long){
        if(current != 0L)
            current = 0L

        binding.customViewOne.setPeriod(msInFuture)                                                 // устанавливаем период для заполняющегося круга
        binding.customViewTwo.setPeriod(msInFuture)

        binding.customViewOne.setCurrent(current)                                                   // устанавливаем начальное значание
        binding.customViewTwo.setCurrent(current)
    }

    suspend fun stepFillingCircle() = withContext(Dispatchers.Default){                     // suspend функция. Шаг круга отрисовки
        current += STEP_MS
        binding.customViewOne.setCurrent(current)
        binding.customViewTwo.setCurrent(current)
    }

    /**  3 - Смена фона stopwatch если isFinished && stopwatch.currentMs != 0L
     *   если нет, то
     * */
    fun changeBackgroundToRed(){
        binding.root.setBackgroundColor(resources.getColor(R.color.red_second))
        binding.blinkingIndicator.setBackgroundColor(resources.getColor(R.color.red_second))
        binding.startPauseButton.setBackgroundColor(resources.getColor(R.color.red_second))
        binding.restartButton.setBackgroundColor(resources.getColor(R.color.red_second))
        binding.deleteButton.setBackgroundColor(resources.getColor(R.color.red_second))
    }
    fun changeBackgroundToStandard(){
        binding.root.setBackgroundColor(Color.WHITE)
        binding.blinkingIndicator.setBackgroundColor(Color.WHITE)
        binding.startPauseButton.setBackgroundColor(Color.WHITE)
        binding.restartButton.setBackgroundColor(Color.WHITE)
        binding.deleteButton.setBackgroundColor(Color.WHITE)
    }

    /** 4 - проверить логику отрисовки текущего значения
     * по прибытии обновлённого stopwatch срисовываем его current в textView в заданном формате
     * */

     fun setCurrentMs(stopwatch: Stopwatch){
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        //Log.d(myLogs,"setCurrentMs ${stopwatch.currentMs} displayTime ${stopwatch.currentMs.displayTime()}")
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

    /**  По нажатию кнопок запускаем соответствующие методы в мэйне
     * */
    private fun initButtonsListeners(stopwatch: Stopwatch){                 // назначем лиссенеры и действия кнопкам
        binding.startPauseButton.setOnClickListener {

            if(!runFlag) {
                listener.start(stopwatch.id)
                startTimer()
                runFlag = true
            } else {
                listener.stop(stopwatch.id)
                stopTimer()
                runFlag = false
            }
        }

        binding.restartButton.setOnClickListener {

        }
        binding.deleteButton.setOnClickListener {
            Log.d(myLogs,"deleteButton(Holder)")
           // listener.delete(stopwatch.id)
        }
    }



    companion object{
        private const val START_TIME = "00:00:00:00"
        private const val STEP_MS = 1000L
        private const val myLogs = "myLogs"
    }

}