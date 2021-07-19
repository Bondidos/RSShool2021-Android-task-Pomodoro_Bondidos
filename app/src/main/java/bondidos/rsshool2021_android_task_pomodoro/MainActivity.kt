package bondidos.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import bondidos.rsshool2021_android_task_pomodoro.CountDown.CDTimer
import bondidos.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import bondidos.rsshool2021_android_task_pomodoro.Interfacies.StopwatchListener
import bondidos.rsshool2021_android_task_pomodoro.customView.Stopwatch
import bondidos.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener {

     private lateinit var binding: ActivityMainBinding
        //https://github.com/android/uamp/blob/main/app/src/main/java/com/example/android/uamp/MediaItemData.kt
     private var timer: CountDownTimer? = null                           // экземпляр класса предоставляющий обратный отчёт
     //
     private val stopwatchAdapter = StopwatchAdapter(this)
     private val stopwatches = mutableListOf<Stopwatch>()
     private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    }

    private fun stopTimer(stopwatch: Stopwatch){
        stopwatch.isStarted = false
        timer?.cancel()
    }

    private fun getCountTimer(stopwatch: Stopwatch): CountDownTimer{
        return object : CountDownTimer(stopwatch.msInFuture, STEP_MS){            // PERIOD - продолжительность работы, UNIT_TEN_MS - интервал счёта


            override fun onTick(millisUntilFinished: Long) {
                Log.d("myLogs","onTick = started")
                val watch = Stopwatch(stopwatch.id,stopwatch.msInFuture,millisUntilFinished,true,false)
                Log.d("myLogs","${watch.currentMs} listItem = ${stopwatches[stopwatch.id].currentMs}")

                //watch.currentMs = millisUntilFinished
                stopwatches[watch.id] = watch
                stopwatchAdapter.submitList(stopwatches.toList())
               // changeStopwatch(stopwatch)

            }

            override fun onFinish() {

            }
        }
    }


    override fun start(id: Int) {
        Log.d("myLogs","buttonStart")
        startTimer(stopwatches[id])
        //timer.startTimer(stopwatches[id],stopwatchAdapter,stopwatches)
        //stopwatchAdapter.submitList(stopwatches.toList())

        //val changed = timer.startTimer(stopwatches[id])

        //changeStopwatch(changed)
    }

    /**---------------------------------------InWork----------------------------------------*/




    override fun stop(id: Int) {
        Log.d("myLogs","stopBTN")
        stopTimer(stopwatches[id])
       // changeStopwatch(id,currentMs, isStarted = false, isFinished = false)
    }

    override fun reset(id: Int) {
       // changeStopwatch(id,timerStartValue, isStarted = false, isFinished = false)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    override fun fin(id: Int) {
        changeStopwatch(stopwatches[id])
    }
    private fun changeStopwatch (stopwatch: Stopwatch/*id: Int, currentMs: Long?, isStarted: Boolean, isFinished: Boolean*/){
        stopwatches.forEach {

            if(it.id == stopwatch.id)
                stopwatches[it.id] = stopwatch




           /* when(isStarted){
                isStarted -> {
                    if (it.isStarted) {
                        stopwatches[it.id] = Stopwatch(it.id, it.currentMs, false, isFinished)
                    }
                    if (it.id == id) {
                        stopwatches[it.id] = Stopwatch(id, currentMs ?: it.currentMs, isStarted, isFinished)
                    }
                }
                !isStarted-> stopwatches[id] = Stopwatch(id, currentMs ?: it.currentMs, isStarted, isFinished)
            }*/
        }
        stopwatchAdapter.submitList(stopwatches.toList())
    }
    companion object{
        private const val STEP_MS = 10L
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