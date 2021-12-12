package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,deStyleInt : Int = 0
) : View(context, attrs,deStyleInt) {
    private var widthSize = 0
    private var heightSize = 0
    private val rect  = RectF()
    private var topRect = RectF()
    private var valueAnimator = ValueAnimator()
    private var customColor: Int = Color.DKGRAY
    private var customTextColor = Color.WHITE
    private var customCircleColor = Color.YELLOW
    private var customProgressColor = Color.RED
    private  var progress : Float = 720f
   private val ovalRect = RectF()


    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT_BOLD
        color = customColor
        textAlign = Paint.Align.CENTER
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT_BOLD
        color = customTextColor
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    private  val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = customCircleColor

    }

    private val progressBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = customProgressColor
        style = Paint.Style.FILL
    }

    private  var buttonText : String = resources.getString(R.string.button_name)

    /*
    //java way

    if(attrs!=null)
     {

      val typedArray = context.obtainStyledAttributes(attrs,R.styleable.LoadingButton)
         customTextColor = typedArray.getColor(R.styleable.LoadingButton_customTextColorValue,Color.WHITE)
         customColor = typedArray.getColor(R.styleable.LoadingButton_customColorValue,Color.DKGRAY)

         backgroundPaint.setColor(customColor)
         paintText.setColor(customTextColor)
         typedArray.recycle()
     }*/



    init {
        //make view clickable
        isClickable = true

        //value animator
        valueAnimator = ValueAnimator.ofFloat(0f, 720f)
        valueAnimator.duration = 2000
        valueAnimator.repeatCount = ValueAnimator.INFINITE

        //get custom color using kotlin extension function on context
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            customColor = getColor(R.styleable.LoadingButton_customButtonBackgroundColorValue, Color.DKGRAY)
            customTextColor = getColor(R.styleable.LoadingButton_customTextColorValue, Color.WHITE)
            customCircleColor = getColor(R.styleable.LoadingButton_customCircleColorValue, Color.YELLOW)
            customProgressColor = getColor(R.styleable.LoadingButton_customProgressColorValue,Color.RED)

        }


    }



        private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, old, new ->

        when(new){
            ButtonState.Loading -> {
                isClickable = false
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator.start()
                valueAnimator.addUpdateListener { animation ->
                    val animatedValue = animation.animatedValue as Float
                    progress = animatedValue
                    invalidate()

                }
            }
            ButtonState.Completed-> {
                isClickable = true
                buttonText = resources.getString(R.string.button_complete)
                valueAnimator.end()


            }
            ButtonState.Clicked-> {
                isClickable = true
                buttonText = resources.getString(R.string.button_name)
                valueAnimator.end()
            }
        }
         //   invalidate()

    }



    override fun performClick(): Boolean {
        if( super.performClick()) return true

        //post invalidate method doesn't block ui
        postInvalidate()
        return true
    }



    override fun onDraw(canvas: Canvas?) {
        rect.left = 0f
        rect.top = 0f
        rect.right = widthSize.toFloat()
        rect.bottom  = heightSize.toFloat()

        topRect = rect
        //set the custom color
        backgroundPaint.setColor(customColor)
        paintText.setColor(customTextColor)

        canvas?.drawRect(rect,backgroundPaint)

        canvas?.drawText(buttonText, (widthSize/2).toFloat(), (heightSize/2).toFloat(),paintText)

        ovalRect.top = 0f + paintText.descent()
        ovalRect.left =  rect.right - 100f
        ovalRect.right = rect.right -10f
        ovalRect.bottom = (heightSize).toFloat()- paintText.descent()

        canvas?.drawArc(ovalRect,0f,progress,true,circlePaint)


        if(progress==720f)
            topRect.right = 0f
        else
            topRect.right = widthSize * (progress/720)


         canvas?.drawRect(topRect,progressBarPaint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun updateButtonState(state : ButtonState)
    {
        buttonState = state
    }

}