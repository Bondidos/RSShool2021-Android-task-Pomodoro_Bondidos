package bondidos.rsshool2021_android_task_pomodoro

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
            stopwatches.add(Stopwatch(nextId++,0,true))    // добавляем созданный таймер в список
            stopwatchAdapter.submitList(stopwatches.toList())                // передаём список с таймерамы в RecyclerView
        }
    }

    override fun start(id: Int) {
        changeStopwatch(id,null,true)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id,currentMs,false)
    }

    override fun reset(id: Int) {
        changeStopwatch(id,0L,false)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch (id: Int, currentMs: Long?, isStarted: Boolean){
        //val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach{                                                                    // находим требуемый таймер в списке и копируем newTimers изменяя его на новые установки
            if(it.id == id){
                //val buffer = it.currentMs
                //stopwatches.remove(it)
                it.currentMs = currentMs ?: it.currentMs
                it.isStarted = isStarted
                //newTimers.add(Stopwatch(it.id, currentMs ?: it.currentMs,isStarted))
            }// else newTimers.add(it)                                                            // остальыне таймеры просто копируем в newTimers
        }
        //stopwatchAdapter.submitList(newTimers)                                                  // применяем созданный список
        //stopwatches.clear()                                                                     // очищаем неисползующийся список
        //stopwatches.addAll(newTimers)                                                           // и копируем в него список таймеров
        stopwatchAdapter.submitList(stopwatches)
    }
}