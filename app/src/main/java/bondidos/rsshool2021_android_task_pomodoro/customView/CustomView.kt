package bondidos.rsshool2021_android_task_pomodoro.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import bondidos.rsshool2021_android_task_pomodoro.R

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
): View(context,attrs,defStyleAttr) {
    private var periodMs = 0L                                   // период таймера, для отрисовки?
    private var currentMs = 0L                                  // текужщий отсчёт
    private var color = 0                                       // цвет для рисования?
    private var style = FILL                                    // стиль рисования (сейчас заливка)
    private var paint = Paint()                                 // объект Пэинт

    init {                                                      // инициализация кастомной вью
        if(attrs != null){
            val styledAttrs = context.theme.obtainStyledAttributes(         // атрибуты стиля прописанные в файле attrs.xml
                attrs,
                R.styleable.CustomView,
                defStyleAttr,
                0
            )
            color = styledAttrs.getColor(R.styleable.CustomView_custom_color,Color.RED)     // тут понятно
            style = styledAttrs.getInt(R.styleable.CustomView_custom_style, FILL)
            styledAttrs.recycle()
        }

        paint.color = color
        paint.style = if (style == FILL) Paint.Style.FILL else Paint.Style.STROKE // выбираем кисть или заливка
        paint.strokeWidth = 5F                                                    // ширина кисти
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //если нет отсчёта в данный момент ничего не делаем
        if(currentMs == 0L || periodMs == 0L) return
        // начальный угол нашей анимации заполнения
        val startAngle = (((currentMs % periodMs).toFloat() / periodMs) * 360)

        // рисуем дугу
        canvas.drawArc(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            -90f,
            startAngle,
            true,
            paint
        )
    }
    // установка актуального времени
    fun setCurrent(current: Long){
        currentMs = current
        invalidate()
    }
    // установка актуального периода
    fun setPeriod(period: Long){
        periodMs = period
    }

    private companion object{
        private const val FILL = 0
    }
}