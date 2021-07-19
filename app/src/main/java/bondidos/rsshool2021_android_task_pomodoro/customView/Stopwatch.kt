package bondidos.rsshool2021_android_task_pomodoro.customView

import android.util.Log
import androidx.recyclerview.widget.DiffUtil

data class Stopwatch(
    val id: Int,
    //val period: Long,           /** период счёта таймера,, то етсь метод onTick 10 мс*/
    val msInFuture: Long,       /** Установленное время. Значение начального отсчёта*/
    var currentMs: Long,        /** Сдесь хранится текущее состояние таймера*/
    var isStarted: Boolean,     /** Старт/стоп*/
    var isFinished: Boolean     /** отсчёт закончен*/
){
     companion object {
        /**
         * Indicates [playbackRes] has changed.
         */
        const val ITEM_RES_CHANGED = 1

         val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>(){               // Имплементация DiffUtil помогает понять RecyclerView какой айтем
            // изменился (был удален, добавлен) и контент какого айтема изменился
            // - чтобы правильно проиграть анимацию и показать результат пользователю

            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {     // лучше проверять на равество только те параметры модели, которые влияют
                // на её визуальное представление на экране.
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.id == newItem.id
            }

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) =
                if(oldItem.currentMs != newItem.currentMs) {
                    ITEM_RES_CHANGED
                } else null

        }
    }
}