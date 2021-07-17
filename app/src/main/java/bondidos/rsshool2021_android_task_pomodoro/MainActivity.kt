package bondidos.rsshool2021_android_task_pomodoro

import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener {

     private lateinit var binding: ActivityMainBinding

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
            stopwatches.add(Stopwatch(nextId++,countDownTime ,false, false))    // добавляем созданный таймер в список
            stopwatchAdapter.submitList(stopwatches.toList())                // передаём список с таймерамы в RecyclerView
        }
    }

    override fun start(id: Int) {
        changeStopwatch(id,null, isStarted = true, isFinished = false)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id,currentMs, isStarted = false, isFinished = false)
    }

    override fun reset(id: Int,timerStartValue: Long) {
        changeStopwatch(id,timerStartValue, isStarted = false, isFinished = false)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    override fun fin(id: Int) {
        changeStopwatch(id,null, isStarted = false, isFinished = true)
    }
    private fun changeStopwatch (id: Int, currentMs: Long?, isStarted: Boolean, isFinished: Boolean){
        stopwatches.forEach {
            when(isStarted){
                isStarted -> {
                    if (it.isStarted) {
                        stopwatches[it.id] = Stopwatch(it.id, it.currentMs, false, isFinished)
                    }
                    if (it.id == id) {
                        stopwatches[it.id] = Stopwatch(id, currentMs ?: it.currentMs, isStarted, isFinished)
                    }
                }
                !isStarted-> stopwatches[id] = Stopwatch(id, currentMs ?: it.currentMs, isStarted, isFinished)
            }
        }
        stopwatchAdapter.submitList(stopwatches.toList())
    }
}