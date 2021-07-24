package bondidos.rsshool2021_android_task_pomodoro.customView

import android.util.Log
import androidx.recyclerview.widget.DiffUtil

data class Stopwatch(
    val id: Int,
    var isStartedByButton: Boolean,
    var adapterPosition: Int,
    val msInFuture: Long,                                   /** Установленное время. Значение начального отсчёта*/
    var currentMs: Long,                                    /** Сдесь хранится текущее состояние таймера*/
    var isStarted: Boolean,                                 /** Старт/стоп*/
    var isFinished: Boolean                                 /** отсчёт закончен*/
){
     companion object {

         const val ITEM_MS_CHANGED = 1
         const val ITEM_STARTED_CHANGE = 2


         val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>(){               // Имплементация DiffUtil помогает понять RecyclerView какой айтем

            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {     // лучше проверять на равество только те параметры модели, которые влияют
                //Log.d("myLogs","")
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.isStarted == newItem.isStarted &&
                oldItem.currentMs == newItem.currentMs && oldItem.id == newItem.id
            }

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) :MutableList<Any>{
                val result = mutableListOf<Any>()
                if (oldItem.currentMs != newItem.currentMs) {
                    result.add(ITEM_MS_CHANGED)
                }
                if (oldItem.isStarted != newItem.isStarted) {
                    result.add(ITEM_STARTED_CHANGE)
                }
                return  result
            }
        }
   }
}