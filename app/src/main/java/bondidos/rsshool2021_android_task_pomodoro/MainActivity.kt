package bondidos.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.recyclerview.widget.LinearLayoutManager
import bondidos.rsshool2021_android_task_pomodoro.CountDown.CDTimer
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener {

     private lateinit var binding: ActivityMainBinding
        //https://github.com/android/uamp/blob/main/app/src/main/java/com/example/android/uamp/MediaItemData.kt
     private lateinit var timer: CDTimer
     //
     private val stopwatchAdapter = StopwatchAdapter(this)
     private val stopwatches = mutableListOf<Stopwatch>()
     private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {                                            // задаём параметры RecyclerList
            layoutManager = LinearLayoutManager(context)                    // лэйаут элементов списка
            adapter = stopwatchAdapter                                      // задаём адептер
        }

        binding.addNewStopwatchButton.setOnClickListener {
            //todo предусмотреть проверки актуальности вводимых значений
            val countDownTime = if ((binding.editText.text.toString().toLongOrNull()?: 0) * 60000 <= (24 * 60 * 60000))  // получаем значение в минутах
            (binding.editText.text.toString().toLongOrNull()?: 0) * 60000
            else 24 * 60 * 60000
            stopwatches.add(Stopwatch(nextId++,countDownTime ,countDownTime,isStarted = false,isFinished = false))    // добавляем созданный таймер в список
            stopwatchAdapter.submitList(stopwatches.toList())                // передаём список с таймерамы в RecyclerView
        }

        timer = CDTimer()  /** Создаём экземпляр "Многоразового таймера" */

    }
    /**----------------------------------------Inwork---------------------------------------*/
    /** todo Задача старт отсчёта и изменение списка ресайклера*/

    override fun start(id: Int) {

        timer.startTimer(stopwatches[id],stopwatchAdapter,stopwatches)
        //stopwatchAdapter.submitList(stopwatches.toList())

        //val changed = timer.startTimer(stopwatches[id])

        //changeStopwatch(changed)
    }


    /**---------------------------------------InWork----------------------------------------*/




    override fun stop(id: Int) {
       // changeStopwatch(id,currentMs, isStarted = false, isFinished = false)
    }

    override fun reset(id: Int) {
       // changeStopwatch(id,timerStartValue, isStarted = false, isFinished = false)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    override fun fin(id: Int) {
        changeStopwatch(stopwatches[id])
    }
    private fun changeStopwatch (stopwatch: Stopwatch/*id: Int, currentMs: Long?, isStarted: Boolean, isFinished: Boolean*/){
        stopwatches.forEach {

            //if(it.id == stopwatch.id)





           /* when(isStarted){
                isStarted -> {
                    if (it.isStarted) {
                        stopwatches[it.id] = Stopwatch(it.id, it.currentMs, false, isFinished)
                    }
                    if (it.id == id) {
                        stopwatches[it.id] = Stopwatch(id, currentMs ?: it.currentMs, isStarted, isFinished)
                    }
                }
                !isStarted-> stopwatches[id] = Stopwatch(id, currentMs ?: it.currentMs, isStarted, isFinished)
            }*/
        }
        stopwatchAdapter.submitList(stopwatches.toList())
    }

}