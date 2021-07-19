package bondidos.rsshool2021_android_task_pomodoro.customView

data class Stopwatch(
    val id: Int,
    //val period: Long,           /** период счёта таймера,, то етсь метод onTick 10 мс*/
    val msInFuture: Long,       /** Установленное время. Значение начального отсчёта*/
    var currentMs: Long,        /** Сдесь хранится текущее состояние таймера*/
    var isStarted: Boolean,     /** Старт/стоп*/
    var isFinished: Boolean     /** отсчёт закончен*/
)