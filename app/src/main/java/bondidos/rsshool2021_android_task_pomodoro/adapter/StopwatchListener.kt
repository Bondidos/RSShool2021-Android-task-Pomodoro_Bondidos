package bondidos.rsshool2021_android_task_pomodoro.adapter

interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, currentMs: Long)

    fun reset(id: Int,timerStartValue: Long)

    fun delete(id: Int)

    // todo fun finish uses for change color of finished count view
    fun fin(id: Int)
}