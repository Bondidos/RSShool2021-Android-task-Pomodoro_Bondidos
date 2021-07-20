package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.runBlocking

class StopwatchAdapter(
    private val listener: StopwatchListener
): ListAdapter<Stopwatch, StopwatchViewHolder>(Stopwatch.itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder{     // инфлейтим View и возвращаем созданный ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater,parent,false)
        return StopwatchViewHolder(listener, binding.root.context.resources,binding)
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder,position: Int, payloads: MutableList<Any>){                   // вызывается в момент создания айтема, в моменты пересоздания
                                                                                                // (например, айтем вышел за пределы экрана, затем вернулся) и
                                                                                                //в моменты обновления айтемов (этим у нас занимается DiffUtil)


        val stopwatchItem = getItem(position)
        var fullRefresh = payloads.isEmpty()

       /* if(stopwatchItem.isStarted)
            holder.setIsRecyclable(false)
        }*/
       // Log.d("myLogs", "onBindViewHolder (BIG) payloads:${fullRefresh}")

      //  payloads.forEach {Log.d("myLogs", "payloads item: $it")  }
        if(payloads.isNotEmpty()){
            payloads.forEach{ payload ->
                when(payload){
                    Stopwatch.ITEM_MS_CHANGED -> {
                       // Log.d("myLogs", "ITEM_MS_CHANGED case in adapter")
                        holder.setCurrentMs(stopwatchItem)
                       // stopwatchItem.isStarted = true
                        runBlocking { holder.stepFillingCircle() }
                        //stopwatchItem.currentMs = stop.
                    }
                    else -> fullRefresh = true
                }
            }
        }

        if(fullRefresh){

            holder.bind(stopwatchItem)
            Log.d("myLogs", "Full refresh in adapter")
        }

}

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        //holder.bind(getItem(position))
        Log.d("myLogs", "onBindViewHolder(second) in adapter")
        onBindViewHolder(holder, position, mutableListOf())
    }

}