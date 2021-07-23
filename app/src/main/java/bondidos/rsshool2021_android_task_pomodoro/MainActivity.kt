package bondidos.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import bondidos.rsshool2021_android_task_pomodoro.CountDown.CDTimer
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.MainListener
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchViewHolder
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), StopwatchListener {

     private lateinit var binding: ActivityMainBinding
     private var timer: CountDownTimer? = null                           // экземпляр класса предоставляющий обратный отчёт
     private lateinit var stopwatchAdapter : StopwatchAdapter
     private val stopwatches = mutableListOf<Stopwatch>()
     private var nextId = 0
     private var isTimerStarted = false
     private var startedStopwatchID = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stopwatchAdapter = StopwatchAdapter(this,stopwatches)
        stopwatchAdapter.setHasStableIds(true)

        binding.recycler.apply {                                            // задаём параметры RecyclerList
            layoutManager = LinearLayoutManager(context)                    // лэйаут элементов списка
            adapter = stopwatchAdapter                                      // задаём адептер

        }

        binding.addNewStopwatchButton.setOnClickListener {
            //todo предусмотреть проверки актуальности вводимых значений
            /*val countDownTime = if ((binding.editText.text.toString().toLongOrNull()?: 0) * 60000 <= (24 * 60 * 60000))  // получаем значение в минутах
            (binding.editText.text.toString().toLongOrNull()?: 0) * 60000
            else 24 * 60 * 60000*/
           // Log.d("myLogs","addneSW pushed. $countDownTime")
            stopwatches.add(Stopwatch(nextId++,
                isStartedByButton = false,
                adapterPosition = -10,
                /*countDownTime*/5000L,
                /*countDownTime*/5000L,
                isStarted = false,
                isFinished = false
            ))    // добавляем созданный таймер в список
            stopwatchAdapter.submitList(stopwatches.toList())                // передаём список с таймерамы в RecyclerView

        }

    }

    private fun startTimer(stopwatch: Stopwatch){
        Log.d("myLogs","StartTimer")
        stopwatch.isStarted = true
        timer?.cancel()                                                 // Отмена отсчёта
        timer = getCountTimer(stopwatch)                                // получаем экземпляр таймера ( с сохранённым отсчётом )
        timer?.start()                                                  // Старт отсчёта
        isTimerStarted = stopwatch.isStarted
        //startedStopwatchID = stopwatch.id
        startedStopwatchID = stopwatch.adapterPosition
    }
    private fun stopTimer(stopwatch: Stopwatch){
        timer?.cancel()
        stopwatch.isStarted = false
        startedStopwatchID = -1
        isTimerStarted = stopwatch.isStarted
        changeStopwatch(stopwatch)
    }
    private fun resetTimer(stopwatch: Stopwatch){
        timer?.cancel()
        stopwatch.isStarted = false
        stopwatch.currentMs = stopwatch.msInFuture
        Log.d("myLogs","resetTimerMain current: ${stopwatch.currentMs}, inFuture: ${stopwatch.msInFuture}")
        isTimerStarted = stopwatch.isStarted
        startedStopwatchID = -1
        changeStopwatch(stopwatch)
    }
    private fun finish(stopwatch: Stopwatch){
        startedStopwatchID = -1
        isTimerStarted = false
        stopwatch.isFinished = true
        stopwatch.currentMs = stopwatch.msInFuture
        stopwatch.isStartedByButton = false
        stopwatch.isStarted = false
        changeStopwatch(stopwatch)

    }
    private fun getCountTimer(stopwatch: Stopwatch): CountDownTimer{
        return object : CountDownTimer(stopwatch.currentMs, STEP_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта

            override fun onTick(millisUntilFinished: Long) {
                // Log.d("myLogs","${watch.currentMs} listItem = ${stopwatches[stopwatch.id].currentMs}")
                stopwatch.currentMs = millisUntilFinished
                stopwatchAdapter.notifyItemChanged(stopwatch.adapterPosition, TIME_CHANGED)
            }
            override fun onFinish() {
                finish(stopwatch)
            }
        }
    }

    override fun start(stopwatch: Stopwatch) {
        if(isTimerStarted && startedStopwatchID != stopwatch.adapterPosition){
             stopwatchAdapter.notifyItemChanged(startedStopwatchID,STOP_OLD)
            startedStopwatchID = -1
        }
        startTimer(stopwatch)
        Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
    }
    override fun stop(stopwatch: Stopwatch) {
        Log.d("myLogs","stopBTN(Main)")
        stopTimer(stopwatch)
        Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
    }
    override fun reset(stopwatch: Stopwatch) {
        resetTimer(stopwatch)
        Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
    }
    override fun delete(stopwatch: Stopwatch) {
      if(isTimerStarted && startedStopwatchID == stopwatch.adapterPosition){
          timer?.cancel()
          startedStopwatchID = -1
      }

        Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
        /** Запоминаем положение в списке удаляемого элемента*/
        val index = stopwatch.adapterPosition
        /** удаляема*/
        stopwatches.removeAt(stopwatch.adapterPosition)
        /** Корректируем индексы*/
        stopwatches.forEach { if (it.adapterPosition > index) it.adapterPosition -= 1 }
        /** сабмит*/
        stopwatchAdapter.submitList(stopwatches.toList())

    }


    private fun changeStopwatch (stopwatch: Stopwatch){

        stopwatchAdapter.notifyItemChanged(stopwatch.adapterPosition, CHANGE_BUTTON)

    }
    companion object{
        private const val STEP_MS = 1000L
        const val TIME_CHANGED = 1
        const val CHANGE_BUTTON = 2
        const val STOP_OLD = 3
    }
}
