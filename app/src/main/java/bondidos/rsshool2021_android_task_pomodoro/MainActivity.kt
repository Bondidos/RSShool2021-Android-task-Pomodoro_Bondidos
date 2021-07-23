package bondidos.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import bondidos.rsshool2021_android_task_pomodoro.CountDown.CDTimer
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.MainListener
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchViewHolder
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), StopwatchListener {

     private lateinit var binding: ActivityMainBinding
        //https://github.com/android/uamp/blob/main/app/src/main/java/com/example/android/uamp/MediaItemData.kt
     private var timer: CountDownTimer? = null                           // экземпляр класса предоставляющий обратный отчёт
     //
     private lateinit var stopwatchAdapter : StopwatchAdapter
     private val stopwatches = mutableListOf<Stopwatch>()
     private var nextId = 0
     private var isTimerStarted = false
     private var startedStopwatchID = -1
    // private lateinit var listener: MainListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stopwatchAdapter = StopwatchAdapter(this,stopwatches)
        stopwatchAdapter.setHasStableIds(true)

        binding.recycler.apply {                                            // задаём параметры RecyclerList
            layoutManager = LinearLayoutManager(context)                    // лэйаут элементов списка
            adapter = stopwatchAdapter                                      // задаём адептер

        }

        binding.addNewStopwatchButton.setOnClickListener {
            //todo предусмотреть проверки актуальности вводимых значений
            /*val countDownTime = if ((binding.editText.text.toString().toLongOrNull()?: 0) * 60000 <= (24 * 60 * 60000))  // получаем значение в минутах
            (binding.editText.text.toString().toLongOrNull()?: 0) * 60000
            else 24 * 60 * 60000*/
           // Log.d("myLogs","addneSW pushed. $countDownTime")
            stopwatches.add(Stopwatch(nextId++,
                isStartedByButton = false,
                adapterPosition = -10,
                /*countDownTime*/60000L,
                /*countDownTime*/60000L,
                isStarted = false,
                isFinished = false
            ))    // добавляем созданный таймер в список
            stopwatchAdapter.submitList(stopwatches.toList())                // передаём список с таймерамы в RecyclerView

        }

    }
    /**----------------------------------------Inwork---------------------------------------*/
    /** todo Задача старт отсчёта и изменение списка ресайклера*/

    private fun startTimer(stopwatch: Stopwatch){
        Log.d("myLogs","StartTimer")
        stopwatch.isStarted = true
        timer?.cancel()                                                 // Отмена отсчёта
        timer = getCountTimer(stopwatch)                                // получаем экземпляр таймера ( с сохранённым отсчётом )
        timer?.start()                                                  // Старт отсчёта
        isTimerStarted = stopwatch.isStarted
        //startedStopwatchID = stopwatch.id
        startedStopwatchID = stopwatch.adapterPosition
    }

    private fun stopTimer(stopwatch: Stopwatch){
        timer?.cancel()
        stopwatch.isStarted = false
        startedStopwatchID = -1
        isTimerStarted = stopwatch.isStarted
        changeStopwatch(stopwatch)
    }
    private fun resetTimer(stopwatch: Stopwatch){
        timer?.cancel()
        stopwatch.isStarted = false
        stopwatch.currentMs = stopwatch.msInFuture
        Log.d("myLogs","resetTimerMain current: ${stopwatch.currentMs}, inFuture: ${stopwatch.msInFuture}")
        isTimerStarted = stopwatch.isStarted
        startedStopwatchID = -1
        changeStopwatch(stopwatch)
        //stopwatchAdapter.notifyItemChanged(stopwatches.indexOf(stopwatch))
    }
    private fun finish(stopwatch: Stopwatch){
        startedStopwatchID = -1
        isTimerStarted = false
        stopwatch.isFinished = true
        stopwatch.currentMs = stopwatch.msInFuture
        stopwatch.isStartedByButton = false
        stopwatch.isStarted = false
        changeStopwatch(stopwatch)

    }


    private fun getCountTimer(stopwatch: Stopwatch): CountDownTimer{
        return object : CountDownTimer(stopwatch.currentMs, STEP_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта

            override fun onTick(millisUntilFinished: Long) {
                // Log.d("myLogs","${watch.currentMs} listItem = ${stopwatches[stopwatch.id].currentMs}")
                stopwatch.currentMs = millisUntilFinished
                stopwatchAdapter.notifyItemChanged(stopwatch.adapterPosition, TIME_CHANGED)
                //changeStopwatch(stopwatch)
            }
            override fun onFinish() {
                finish(stopwatch)
            }
        }
    }


    override fun start(stopwatch: Stopwatch) {
        if(isTimerStarted && startedStopwatchID != stopwatch.adapterPosition){
            //вызвать кнопку стоп
             stopwatchAdapter.notifyItemChanged(startedStopwatchID,STOP_OLD)
            startedStopwatchID = -1
        }
        startTimer(stopwatch)
        Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
    }


    override fun stop(stopwatch: Stopwatch) {
        Log.d("myLogs","stopBTN(Main)")
        stopTimer(stopwatch)
        Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
    }

    override fun reset(stopwatch: Stopwatch) {
        resetTimer(stopwatch)
        Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
    }

    override fun delete(stopwatch: Stopwatch) {
      if(isTimerStarted && startedStopwatchID == stopwatch.adapterPosition){

          //вызвать кнопку стоп
          timer?.cancel()
          startedStopwatchID = -1
      }
       // stopwatchAdapter.notifyItemChanged(stopwatch.id,DELETED)
        Log.d("myLogs","----------------------")
        stopwatches.forEach { Log.d("myLogs","$it") }
        val index = stopwatch.adapterPosition

        stopwatches.removeAt(stopwatch.adapterPosition)
        stopwatches.forEach { if (it.adapterPosition > index) it.adapterPosition -= 1 }
        stopwatchAdapter.submitList(stopwatches.toList())

    }
    /**---------------------------------------InWork----------------------------------------*/


   // private fun MutableList<Stopwatch>.findById(id: Int) = this.find { it.id == id } ?: throw Exception ("ItemNotFound")

    private fun changeStopwatch (stopwatch: Stopwatch){

            /*stopwatches[stopwatches.indexOf(stopwatches.find {
                it.id == stopwatch.id
            })] = stopwatch.copy()
            stopwatchAdapter.submitList(stopwatches.toList())*/
       // stopwatchAdapter.notifyItemChanged(stopwatch.id)
        stopwatchAdapter.notifyItemChanged(stopwatch.adapterPosition, CHANGE_BUTTON)

    }
    companion object{
        private const val STEP_MS = 1000L
        const val TIME_CHANGED = 1
        const val CHANGE_BUTTON = 2
        const val STOP_OLD = 3
        const val DELETED = 4
    }
}
/**

ПРО АДАПТЕР
а, кажись понял, Адаптер должен отслеживать когда меняется время таймера и обновлять айтем, вызывая собственно bind?
В правильную сторону думаю?)
SecondSLoT (Aleksandr Seloustev) — Today at 13:17
Я так сделал
Только не адаптер отслеживает, а листенер, для которого еще интерфейс сделали
И передает изменения в адаптер через submitList()

Denis Orlov — Today at 13:40
Это вроде понял. Да и в примере оказывается есть submitList. Правда там тоже он срабатывает при кликах.
А как отслеживать текущие изменения? А то на Stackoverflow пока нашел только примеры с кнопками)

SecondSLoT (Aleksandr Seloustev) — Today at 14:27
При срабатывании onTick() обновляешь данные таймера и сабмитишь в адаптер так же, как при нажатии кнопок

Denis Orlov — Today at 14:28
Ага, я туда setCurrent() запихал, так все работает
правда теперь какие-то блики появились, когда нажимаешь стоп и потом снова старт

SecondSLoT (Aleksandr Seloustev) — Today at 14:30
Блики, когда таймер работает или просто при нажатии на кнопки?
Denis Orlov — Today at 14:31
когда стоп намаешь, а потом старт, то как-будто вьюха возвращается в начальное положение( а она у меня залитая стартует) и потом сразу на текущее прыгает
SecondSLoT (Aleksandr Seloustev) — Today at 14:33
Значит надо в onBind() правильно задавать начальное состояние вьюхи, чтобы не отображалась полностью залитая, когда это не нужно
Denis Orlov — Today at 14:36
да я там по сути только setPeriod задаю
вьюха уже эта все мозги выела
SecondSLoT (Aleksandr Seloustev) — Today at 14:37
А надо ещё setCurrent

Denis Orlov — Today at 14:40
Да setCurrent уже тоже пробовал, но вообще ничего не дает. Наверное в другом чем-то накосячил)

Denis Orlov — Today at 15:27
я в onTick поставил setCurrent(-currentMS), чтобы по часовой рисовалось. Из-за этого бликовало. Перенес минус в другое место.
Правда все равно оно из залитой вьюхи на убыль идет. В принципе и так понятно сколько осталось,
но потом попробую разобраться почему залитая сразу ,а не пустая. Пока уже доволен тем, что вообще работает) */