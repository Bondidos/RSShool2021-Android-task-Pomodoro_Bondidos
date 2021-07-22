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
        //https://github.com/android/uamp/blob/main/app/src/main/java/com/example/android/uamp/MediaItemData.kt
     private var timer: CountDownTimer? = null                           // экземпляр класса предоставляющий обратный отчёт
     //
     private lateinit var stopwatchAdapter : StopwatchAdapter
     private val stopwatches = mutableListOf<Stopwatch>()
     private var nextId = 0
     private var isTimerStarted = false
    private var startedStopwatchID = -1
    // private lateinit var listener: MainListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stopwatchAdapter = StopwatchAdapter(this,stopwatches)

        binding.recycler.apply {                                            // задаём параметры RecyclerList
            layoutManager = LinearLayoutManager(context)                    // лэйаут элементов списка
            adapter = stopwatchAdapter                                      // задаём адептер

        }

        binding.addNewStopwatchButton.setOnClickListener {
            //todo предусмотреть проверки актуальности вводимых значений
           /* val countDownTime = if ((binding.editText.text.toString().toLongOrNull()?: 0) * 60000 <= (24 * 60 * 60000))  // получаем значение в минутах
            (binding.editText.text.toString().toLongOrNull()?: 0) * 60000
            else 24 * 60 * 60000*/
            val countDownTime = 2000L
            Log.d("myLogs","addneSW pushed. $countDownTime")
            stopwatches.add(Stopwatch(nextId++,countDownTime ,countDownTime,isStarted = false,isFinished = false))    // добавляем созданный таймер в список
            stopwatchAdapter.submitList(stopwatches.toList())                // передаём список с таймерамы в RecyclerView
        }

    }
    /**----------------------------------------Inwork---------------------------------------*/
    /** todo Задача старт отсчёта и изменение списка ресайклера*/

    private fun startTimer(stopwatch: Stopwatch){
        Log.d("myLogs","StartTimer")
        stopwatch.isStarted = true
        timer?.cancel()                                                 // Отмена отсчёта
        timer = getCountTimer(stopwatch)                                // получаем экземпляр таймера ( с сохранённым отсчётом )
        timer?.start()                                                  // Старт отсчёта
        isTimerStarted = stopwatch.isStarted
        startedStopwatchID = stopwatch.id
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
        //stopwatchAdapter.notifyItemChanged(stopwatches.indexOf(stopwatch))
    }

    private fun getCountTimer(stopwatch: Stopwatch): CountDownTimer{
        return object : CountDownTimer(stopwatch.currentMs, STEP_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта

            override fun onTick(millisUntilFinished: Long) {
                // Log.d("myLogs","${watch.currentMs} listItem = ${stopwatches[stopwatch.id].currentMs}")
                stopwatch.currentMs = millisUntilFinished
                changeStopwatch(stopwatch)
            }
            override fun onFinish() {
                isTimerStarted = false
                startedStopwatchID = -1
                stopwatch.isFinished = true
                stopwatch.isStarted = false
                changeStopwatch(stopwatch)
                stopwatchAdapter.notifyItemChanged(stopwatch.id)
            }
        }
    }


    override fun start(stopwatch: Stopwatch) {
        Log.d("myLogs","buttonStart(Main)")
        if(startedStopwatchID == -1)
            startTimer(stopwatch)
        else {
           try {
               stopTimer(stopwatches.findById(startedStopwatchID))             //stoping old timer
               }
           catch(c: Exception){
               Log.d("myLogs","$c id: $startedStopwatchID")
               Toast.makeText(this,"$c, id $startedStopwatchID",Toast.LENGTH_SHORT).show()

           }
            startTimer(stopwatch)                                        //starting new timer
            }
        }


    override fun stop(stopwatch: Stopwatch) {
        Log.d("myLogs","stopBTN(Main)")
        stopTimer(stopwatch)
    }

    override fun reset(stopwatch: Stopwatch) {
        resetTimer(stopwatch)
    }

    override fun delete(stopwatch: Stopwatch) {
       if(stopwatch.id == startedStopwatchID){
           stopTimer(stopwatches.findById(startedStopwatchID)).run{
               startedStopwatchID = -1
           }

       }

        stopwatches.remove(stopwatches.find { it.id == stopwatch.id })
        stopwatchAdapter.submitList(stopwatches.toList())
    //    stopwatchAdapter.notifyItemRemoved(stopwatch.id)

    }
    /**---------------------------------------InWork----------------------------------------*/

    override fun fin(stopwatch: Stopwatch) {

    }

    private fun MutableList<Stopwatch>.findById(id: Int) = this.find { it.id == id } ?: throw Exception ("ItemNotFound")

    private fun changeStopwatch (stopwatch: Stopwatch){
            val item = stopwatches.findById(stopwatch.id)
            stopwatches[stopwatches.indexOf(item)] = stopwatch.copy()
            stopwatchAdapter.submitList(stopwatches.toList())

    }
    companion object{
        private const val STEP_MS = 1000L
    }
}
