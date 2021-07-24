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

class StopwatchAdapter(
    private val listener: StopwatchListener,
    private val stopwatches: List<Stopwatch>
): ListAdapter<Stopwatch, StopwatchViewHolder>(Stopwatch.itemComparator){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder{
        // инфлейтим View и возвращаем созданный ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater,parent,false)
        val holder = StopwatchViewHolder(listener, binding.root.context.resources,binding)

        with(holder){
            startPauseButton.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION){
                    if (stopwatch.isStarted) {
                        stopTimer(stopwatch)
                        //Log.d("myLogs", "stop(adapter)")

                    } else {
                        startTimer(stopwatch)
                        //Log.d("myLogs", "start(adapter)")
                    }
                }
            }
            deleteButton.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION){
                    //Log.d("myLogs", "deleteButton(Adapter)")
                    listener.delete(stopwatch)
                }
            }
        }

        return holder
    }

    override fun getItemId(position: Int): Long {
        return stopwatches[position].id.toLong()
    }

    override fun onViewAttachedToWindow(holder: StopwatchViewHolder) {
       // Log.d("myLogs","onViewAttachedToWindow ")
        if(holder.stopwatch.isStarted){
            holder.startAnimation()
            holder.startPauseButton.text = "Stop"
        } else holder.stopAnimation()

        if(holder.stopwatch.isFinished){
            holder.changeBackgroundToRed()
        } else holder.changeBackgroundToStandard()

        if(!holder.stopwatch.isStarted && holder.isAnimationStarted) {
            holder.stopAnimation()
            holder.changeButtonNameToStart()
        }
        holder.stopwatch.adapterPosition = holder.adapterPosition
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: StopwatchViewHolder) {
        holder.changeButtonNameToStart()
        holder.stopAnimation()
        holder.stopwatch.adapterPosition = holder.adapterPosition
        super.onViewDetachedFromWindow(holder)
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder,position: Int, payloads: MutableList<Any>){                   // вызывается в момент создания айтема, в моменты пересоздания
        val stopwatchItem = getItem(position)

        stopwatchItem.adapterPosition = holder.adapterPosition

       // payloads.forEach { Log.d("myLogs","$it") }
        if(payloads.isNotEmpty()){
            payloads.forEach{ payload ->
                when(payload){
                    TIME_CHANGED -> {
                        holder.setCurrentMs(stopwatchItem)
                        runBlocking { holder.stepFillingCircle(stopwatchItem) }
                      //  Log.d("myLogs","TIME_CHANGED ${stopwatchItem.adapterPosition} in ${stopwatchItem.currentMs}")
                    }
                    CHANGE_BUTTON -> {
                        //Log.d("myLogs","CHANGE_BUTTON position: ${stopwatchItem.adapterPosition} in ${stopwatchItem.currentMs}")
                        holder.changeButtonNameToStart()
                    }
                    STOP_OLD  -> {
                        Log.d("myLogs","STOP_OLD ${stopwatchItem.adapterPosition} in ${stopwatchItem.currentMs}")
                        holder.stopwatch.isStarted = false
                        holder.stopOldButton()
                    }
                    FINISH -> {
                      //  Log.d("myLogs","FINISH position: ${stopwatchItem.adapterPosition} in ${stopwatchItem.currentMs}")
                        runBlocking { holder.stepFillingCircle(stopwatchItem) }
                        holder.onFinish(stopwatchItem)
                    }
                }
            }
        }
        else holder.bind(stopwatchItem)
}

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
       // Log.d("myLogs", "onBindViewHolder(second) in adapter")
        onBindViewHolder(holder, position, mutableListOf())
    }
    companion object{
        const val TIME_CHANGED = 1
        const val CHANGE_BUTTON = 2
        const val STOP_OLD = 3
        const val FINISH = 4
    }
}