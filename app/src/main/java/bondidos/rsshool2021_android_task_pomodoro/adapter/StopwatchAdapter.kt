package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class StopwatchAdapter(
    private val listener: StopwatchListener,
    private val stopwatches: List<Stopwatch>
): ListAdapter<Stopwatch, StopwatchViewHolder>(Stopwatch.itemComparator){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder{
        // инфлейтим View и возвращаем созданный ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater,parent,false)
        val holder = StopwatchViewHolder(listener, binding.root.context.resources,binding)
        val position = holder.layoutPosition


        fun List<Stopwatch>.find(position: Int): Stopwatch{
            return this.find { it.adapterPosition == position } ?: throw Exception ("exception in onCreateHolder")
        }
        val item = getItem(position+1)
      //  val stopwatchItem = stopwatches.find(getItemId(position).toInt())

        //https://devcolibri.com/unit/%d1%83%d1%80%d0%be%d0%ba-10-%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%b0-%d1%81-recyclerview-%d0%bd%d0%b0-%d0%bf%d1%80%d0%b8%d0%bc%d0%b5%d1%80%d0%b5-tweetsrecyclerview-2/#link1

        /**---------------------------------------------- */
        with(holder) {
            startPauseButton.setOnClickListener {
                if(adapterPosition!=RecyclerView.NO_POSITION) {
                    if (item.isStarted) {
                        stopTimer(item)
                        Log.d("myLogs", "stop(holder)")

                    } else {
                        startTimer(item)
                        Log.d("myLogs", "start(holder)")
                    }
                }
            }

            deleteButton.setOnClickListener {
                if(adapterPosition!=RecyclerView.NO_POSITION) {
                    Log.d("myLogs", "deleteButton(Adapter)")
                    listener.delete(item)
                }
            }
        }

        /**---------------------------------------------- */


        return holder
    }

    override fun onViewRecycled(holder: StopwatchViewHolder) {
        holder.current= 0L
        holder.changeBackgroundToStandard()

        super.onViewRecycled(holder)
    }


    override fun onBindViewHolder(holder: StopwatchViewHolder,position: Int, payloads: MutableList<Any>){                   // вызывается в момент создания айтема, в моменты пересоздания
        val stopwatchItem = getItem(position)

        /** инициализация холдера, один раз по идее в момент добавления в холдер */
        if(stopwatchItem.currentMs == stopwatchItem.msInFuture && !stopwatchItem.isFinished){
            stopwatchItem.adapterPosition = holder.adapterPosition
            holder.bind(stopwatchItem)
        }

        /** запускаем отсчёт таймера по нажатию кнопки */ //todo после ресет а пропускает одну секунду счёта
        if (stopwatchItem.isStarted) {
            holder.setCurrentMs(stopwatchItem)
            runBlocking { holder.stepFillingCircle() }
            if(stopwatchItem.isFinished){
                holder.changeBackgroundToStandard()
                stopwatchItem.isFinished = false
            }
        }

        /** тут я хочу сделать остановку таймера без кнопки*/
        if(!stopwatchItem.isStarted && stopwatchItem.isStartedByButton){
            holder.stopTimer(stopwatchItem)

        }

        /** действия при завершении отсчёта таймера*/
        if(stopwatchItem.isFinished){
            Log.d("myLogs", "isFinished in adapter")
            listener.reset(stopwatchItem)

            holder.changeBackgroundToRed()
            holder.startPauseButton.text = "START"
            holder.bind(stopwatchItem)

        }



        /*if(stopwatchItem.currentMs == stopwatchItem.msInFuture) {
            holder.bind(stopwatchItem)
            Log.d("myLogs", "Full refresh in adapter")
        }*/

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

    override fun getItemCount(): Int {
        return stopwatches.size
    }
}