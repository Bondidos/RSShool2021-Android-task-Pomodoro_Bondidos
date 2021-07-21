package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.MainListener
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.MainActivity
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.runBlocking

class StopwatchAdapter(
    private val listener: StopwatchListener,
    private val stopwatches: List<Stopwatch>
): ListAdapter<Stopwatch, StopwatchViewHolder>(Stopwatch.itemComparator){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder{
        // инфлейтим View и возвращаем созданный ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater,parent,false)
        val holder = StopwatchViewHolder(listener, binding.root.context.resources,binding)
        val position = holder.adapterPosition
        return holder
    }


    override fun onBindViewHolder(holder: StopwatchViewHolder,position: Int, payloads: MutableList<Any>){                   // вызывается в момент создания айтема, в моменты пересоздания
        val stopwatchItem = getItem(position)
        //var fullRefresh = payloads.isEmpty()
       // if(!payloads.isEmpty()) {
            if (stopwatchItem.isStarted) {
                holder.setIsRecyclable(false)
                holder.setCurrentMs(stopwatchItem)
                runBlocking { holder.stepFillingCircle() }
          //  }
        }   else holder.setIsRecyclable(true)

        if(!stopwatchItem.isStarted && holder.runFlag){
            holder.stopTimer(stopwatchItem)
        }
        if(stopwatchItem.isFinished){
            holder.changeBackgroundToRed()
        }


        if(stopwatchItem.currentMs == stopwatchItem.msInFuture) {
            holder.bind(stopwatchItem)
            Log.d("myLogs", "Full refresh in adapter")
        }
/*

        with(holder){
            startPauseButton.setOnClickListener {
                if(!stopwatchItem.isStarted) {
                    listener.start(stopwatchItem.id)
                    startTimer()
                    runFlag = true
                    Log.d("myLogs","start(Adapter)")
                } else {
                    listener.stop(stopwatchItem.id)
                    stopTimer()
                    runFlag = false
                    Log.d("myLogs","stop(Adapter)")
                }
            }
            restartButton.setOnClickListener {
                listener.reset(stopwatchItem.id)
                android.util.Log.d("myLogs","restartButton(Adapter)")
            }
            deleteButton.setOnClickListener {
                android.util.Log.d("myLogs","deleteButton(Adapter)")
                listener.delete(stopwatchItem.id)
            }
        }*/

                                                                                                // (например, айтем вышел за пределы экрана, затем вернулся) и
        //в моменты обновления айтемов (этим у нас занимается DiffUtil)

       // Log.d("myLogs", "onBindViewHolder (BIG) payloads:${fullRefresh}")

       // payloads.forEach {Log.d("myLogs", "payloads item: $it")  }
       /* if(payloads.isNotEmpty()){
            payloads.forEach{ payload ->
                when(payload){
                    Stopwatch.ITEM_STARTED_CHANGE -> {
                      //  Log.d("myLogs", "ITEM_STARTED_CHANGE case in adapter")
                        holder.startPauseButton.callOnClick()

                    }
                    Stopwatch.ITEM_MS_CHANGED -> {
                    //    Log.d("myLogs", "ITEM_MS_CHANGED case in adapter")
                        holder.setCurrentMs(stopwatchItem)
                       // stopwatchItem.isStarted = true
                        runBlocking { holder.stepFillingCircle() }
                        if(!stopwatchItem.isStarted && holder.runFlag){
                            listener.stop(stopwatchItem.id)
                            holder.stopTimer()
                            holder.runFlag = false
                            Log.d("myLogs", "STOP_AFTER ITEM_CHANGED case in adapter")
                        }
                        //stopwatchItem.currentMs = stop.
                    }

                    else -> fullRefresh = true
                }
            }
        }

        if(fullRefresh){

            holder.bind(stopwatchItem)
            Log.d("myLogs", "Full refresh in adapter")
        }*/


}


    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        //holder.bind(getItem(position))
        Log.d("myLogs", "onBindViewHolder(second) in adapter")
        onBindViewHolder(holder, position, mutableListOf())
    }
}