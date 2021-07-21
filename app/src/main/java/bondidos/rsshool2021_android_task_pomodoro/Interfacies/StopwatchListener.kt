package bondidos.rsshool2021_android_task_pomodoro.Interfacies

import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch

interface StopwatchListener {

    fun start(stopwatch: Stopwatch)

    fun stop(stopwatch: Stopwatch)

    fun reset(stopwatch: Stopwatch)

    fun delete(stopwatch: Stopwatch)

    // todo fun finish uses for change color of finished count view
    fun fin(stopwatch: Stopwatch)
}