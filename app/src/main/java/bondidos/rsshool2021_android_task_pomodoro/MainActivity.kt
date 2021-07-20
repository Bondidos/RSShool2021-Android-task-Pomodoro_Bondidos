package bondidos.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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

        binding.recycler.apply {                                            // задаём параметры RecyclerList
            layoutManager = LinearLayoutManager(context)                    // лэйаут элементов списка
            adapter = stopwatchAdapter                                      // задаём адептер
        }

        binding.addNewStopwatchButton.setOnClickListener {
            //todo предусмотреть проверки актуальности вводимых значений
            val countDownTime = if ((binding.editText.text.toString().toLongOrNull()?: 0) * 60000 <= (24 * 60 * 60000))  // получаем значение в минутах
            (binding.editText.text.toString().toLongOrNull()?: 0) * 60000
            else 24 * 60 * 60000
            Log.d("myLogs","addneSW pushed. $countDownTime")
            stopwatches.add(Stopwatch(nextId++,countDownTime ,countDownTime,isStarted = false,isFinished = false))    // добавляем созданный таймер в список
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
        startedStopwatchID = stopwatch.id
    }

    private fun stopTimer(stopwatch: Stopwatch){

        timer?.cancel()
        stopwatch.isStarted = false
        changeStopwatch(stopwatch.copy())
    }
    private fun resetTimer(stopwatch: Stopwatch){

        timer?.cancel()
        stopwatch.isStarted = false
        stopwatch.currentMs = stopwatch.msInFuture
        Log.d("myLogs","resetTimerMain current: ${stopwatch.currentMs}, inFuture: ${stopwatch.msInFuture}")
        changeStopwatch(stopwatch.copy())
        stopwatchAdapter.notifyItemChanged(stopwatches.indexOf(stopwatch))
        isTimerStarted = stopwatch.isStarted
        startedStopwatchID = -1
    }

    private fun getCountTimer(stopwatch: Stopwatch): CountDownTimer{
        return object : CountDownTimer(stopwatch.currentMs, STEP_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта

            override fun onTick(millisUntilFinished: Long) {
                //Log.d("myLogs","onTick = started")
                if(stopwatch.isStarted) {
                    // Log.d("myLogs","${watch.currentMs} listItem = ${stopwatches[stopwatch.id].currentMs}")
                    stopwatch.currentMs = millisUntilFinished
                    changeStopwatch(stopwatch.copy())
                }
            }
            override fun onFinish() {

            }
        }
    }


    override fun start(id: Int) {
        Log.d("myLogs","buttonStart(Main)")
        if(startedStopwatchID != id){
            stopTimer(requireNotNull(stopwatches.find { it.id == id }))
            startTimer(requireNotNull(stopwatches.find { it.id == id }))
           // listener.startButtonFire()
        }
            else  startTimer(requireNotNull(stopwatches.find { it.id == id }))
        //stopwatchAdapter.hol
       // startTimer(stopwatches[id])
        //startTimer(stopwatches[])



        }


    override fun stop(id: Int) {

        Log.d("myLogs","stopBTN(Main)")
        //stopTimer(stopwatches[id])
        stopTimer(requireNotNull(stopwatches.find { it.id == id }))
       // stopTimer(stopwatchAdapter.getItemId(id) ?: )

            }

    override fun reset(id: Int) {
        resetTimer(requireNotNull(stopwatches.find { it.id == id }))
       // val item = requireNotNull(stopwatches.find { it.id == id })
       // item.currentMs=item.msInFuture
        //stopTimer(item)

    }

    override fun delete(id: Int) {


        stopTimer(requireNotNull(stopwatches.find { it.id == id }))
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())

    }
    /**---------------------------------------InWork----------------------------------------*/

    override fun fin(id: Int) {
        changeStopwatch(stopwatches[id])
    }
    private fun changeStopwatch (stopwatch: Stopwatch/*id: Int, currentMs: Long?, isStarted: Boolean, isFinished: Boolean*/){

                stopwatches.forEach{
                    if(it.id == stopwatch.id) {
                        stopwatches[stopwatches.indexOf(it)] = stopwatch
                    }
                }
                stopwatchAdapter.submitList(stopwatches.toList())
    }
    companion object{
        private const val STEP_MS = 1000L
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