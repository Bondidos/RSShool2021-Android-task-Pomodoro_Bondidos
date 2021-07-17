package bondidos.rsshool2021_android_task_pomodoro.customView

data class Stopwatch(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    var isFinished: Boolean
)