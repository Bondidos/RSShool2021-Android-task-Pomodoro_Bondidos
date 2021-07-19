package bondidos.rsshool2021_android_task_pomodoro.Interfacies

import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch

interface TimerListener {
    fun startTimer(stopwatch: Stopwatch)
    fun stopTimer(stopwatch: Stopwatch)
    fun setCurrentMs(stopwatch: Stopwatch,millisUntilFinished: Long)
}