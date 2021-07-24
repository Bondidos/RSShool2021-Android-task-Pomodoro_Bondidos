package bondidos.rsshool2021_android_task_pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.Service.*
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

     private lateinit var binding: ActivityMainBinding
     private var timer: CountDownTimer? = null                           // экземпляр класса предоставляющий обратный отчёт
     private lateinit var stopwatchAdapter : StopwatchAdapter
     private val stopwatches = mutableListOf<Stopwatch>()
     private var nextId = 0
     private var isTimerStarted = false
     private var startedStopwatchID = -1
  //  private lateinit var stopwatch_: Stopwatch
    private var startTime =0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stopwatchAdapter = StopwatchAdapter(this, stopwatches)
        stopwatchAdapter.setHasStableIds(true)

        binding.recycler.apply {                                            // задаём параметры RecyclerList
            layoutManager =
                LinearLayoutManager(context)                    // лэйаут элементов списка
            adapter = stopwatchAdapter                                      // задаём адептер
        }

        binding.addNewStopwatchButton.setOnClickListener {

            val countDownTime = (binding.editText.text.toString().toLongOrNull() ?: 0) * 60000

            // Log.d("myLogs","addneSW pushed. $countDownTime")
            if (countDownTime != 0L) {
                stopwatches.add(
                    Stopwatch(
                        nextId++,
                        isStartedByButton = false,
                        adapterPosition = -10,
                        countDownTime,
                        countDownTime,
                        isStarted = false,
                        isFinished = false
                    )
                )    // добавляем созданный таймер в список
                stopwatchAdapter.submitList(stopwatches.toList())                // передаём список с таймерамы в RecyclerView}
            } else Toast.makeText(
                this,
                "Incorrect input, can't countdown from 0",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun startTimer(stopwatch: Stopwatch){
       // Log.d("myLogs","StartTimer")
        stopwatch.isStarted = true
        isTimerStarted = stopwatch.isStarted
        timer?.cancel()                                                 // Отмена отсчёта
        timer = getCountTimer(stopwatch)                                // получаем экземпляр таймера ( с сохранённым отсчётом )
        timer?.start()
        startedStopwatchID = stopwatch.adapterPosition
        // время для корутины в сервисе
        startTime = if(stopwatch.currentMs == stopwatch.msInFuture) {
            System.currentTimeMillis() + stopwatch.msInFuture
        } else System.currentTimeMillis() +stopwatch.msInFuture - (stopwatch.msInFuture - stopwatch.currentMs)



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
      //  Log.d("myLogs","resetTimerMain current: ${stopwatch.currentMs}, inFuture: ${stopwatch.msInFuture}")
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
        stopwatchAdapter.notifyItemChanged(stopwatch.adapterPosition, FINISH)
        Toast.makeText(
            this,
            "Timer finished!",
            Toast.LENGTH_LONG
        ).show()

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

        if(stopwatch.isFinished) stopwatch.isFinished = false

        startTimer(stopwatch)
        //stopwatches.forEach { Log.d("myLogs","$it") }
    }
    override fun stop(stopwatch: Stopwatch) {
        //Log.d("myLogs","stopBTN(Main)")
        stopTimer(stopwatch)
        stopwatches.forEach { Log.d("myLogs","$it") }
    }
    override fun reset(stopwatch: Stopwatch) {
        resetTimer(stopwatch)
       // Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
    }
    override fun delete(stopwatch: Stopwatch) {
      if(isTimerStarted && startedStopwatchID == stopwatch.adapterPosition){
          timer?.cancel()
          startedStopwatchID = -1
      }

        //Log.d("myLogs","----------------------")
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


    private fun changeStopwatch (stopwatch: Stopwatch) = stopwatchAdapter.notifyItemChanged(stopwatch.adapterPosition, CHANGE_BUTTON)


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        if(isTimerStarted) {
            startService(startIntent)
        } else return
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }


    companion object{
        private const val STEP_MS = 10L
        const val TIME_CHANGED = 1
        const val CHANGE_BUTTON = 2
        const val STOP_OLD = 3
        const val FINISH = 4

    }
}
