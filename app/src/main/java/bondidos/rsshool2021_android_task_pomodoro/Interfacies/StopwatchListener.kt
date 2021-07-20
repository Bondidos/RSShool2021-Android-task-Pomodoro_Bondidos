package bondidos.rsshool2021_android_task_pomodoro.Interfacies

import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch

interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int)

    fun reset(id: Int)

    fun delete(id: Int)

    // todo fun finish uses for change color of finished count view
    fun fin(id: Int)
}