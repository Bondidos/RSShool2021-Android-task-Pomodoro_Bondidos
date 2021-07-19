package bondidos.rsshool2021_android_task_pomodoro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding

class StopwatchAdapter(
    private val listener: StopwatchListener
): ListAdapter<Stopwatch, StopwatchViewHolder>(itemComparator) {

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

        if(payloads.isNotEmpty()){
            payloads.forEach{ payload ->
                when(payload){
                    PLAYBACK_RES_CHANGED -> {
                        stopwatchItem.currentMs = payload as Long
                    }
                    else -> fullRefresh = true
                }
            }
        }

        if(fullRefresh){
            //holder.item = mediaItem
        }

        holder.bind(getItem(position))

        if(getItem(position).isStarted) {
            holder.setIsRecyclable(false)
        }



    }

    private companion object {
        /**
         * Indicates [playbackRes] has changed.
         */
        const val PLAYBACK_RES_CHANGED = 1

        private val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>(){               // Имплементация DiffUtil помогает понять RecyclerView какой айтем
                                                                                                    // изменился (был удален, добавлен) и контент какого айтема изменился
                                                                                                // - чтобы правильно проиграть анимацию и показать результат пользователю
            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {     // лучше проверять на равество только те параметры модели, которые влияют
                                                                                                // на её визуальное представление на экране.
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted
            }

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) {
                if(oldItem.currentMs != newItem.currentMs) {
                PLAYBACK_RES_CHANGED
                } else null
            }
        }
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }
}