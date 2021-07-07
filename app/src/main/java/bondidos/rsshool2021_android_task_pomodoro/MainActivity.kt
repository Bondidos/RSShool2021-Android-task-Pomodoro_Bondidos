package bondidos.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import bondidos.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

     private lateinit var binding: ActivityMainBinding

     private val stopwatchAdapter = StopwatchAdapter()
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
            stopwatches.add(Stopwatch(nextId++,0,false))    // добавляем созданный таймер в список
            stopwatchAdapter.submitList(stopwatches.toList())                // передаём список с таймерамы в RecyclerView
        }
    }
}