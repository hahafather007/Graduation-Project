package com.hello.widget.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.BounceInterpolator
import com.hello.R
import com.hello.utils.DimensionUtil.sp2px

class StepView : View {
    //用来标记已经设置了数据
    private var hasSetData = false
    private var lastAngleLength = 0f;
    /**
     * 圆弧的宽度
     */
    private val borderWidth = 38f
    /**
     * 画步数的数值的字体大小
     */
    private var numberTextSize = 0f
    /**
     * 步数
     */
    private var stepNumber = "0"
    /**
     * 开始绘制圆弧的角度
     */
    private val startAngle = 135f
    /**
     * 终点对应的角度和起始点对应的角度的夹角
     */
    private val angleLength = 270f
    /**
     * 所要绘制的当前步数的红色圆弧终点到起点的夹角
     */
    private var currentAngleLength = 0f
    /**
     * 动画时长
     */
    private val animationLength = 2000

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**中心点的x坐标 */
        val centerX = (width / 2).toFloat()
        /**指定圆弧的外轮廓矩形区域 */
        val rectF = RectF(0 + borderWidth, borderWidth, 2 * centerX - borderWidth, 2 * centerX - borderWidth)

        /**【第一步】绘制整体的黄色圆弧 */
        drawArcYellow(canvas, rectF)
        /**【第二步】绘制当前进度的红色圆弧 */
        drawArcRed(canvas, rectF)
        /**【第三步】绘制当前进度的红色数字 */
        drawTextNumber(canvas, centerX)
        /**【第四步】绘制"步数"的红色数字 */
        drawTextStepString(canvas, centerX)
    }

    /**
     * 1.绘制总步数的黄色圆弧
     *
     * @param canvas 画笔
     * @param rectF  参考的矩形
     */
    private fun drawArcYellow(canvas: Canvas, rectF: RectF) {
        val paint = Paint().apply {
            /** 默认画笔颜色，黄色  */
            @Suppress("DEPRECATION")
            color = resources.getColor(R.color.colorLine)
            /** 结合处为圆弧 */
            strokeJoin = Paint.Join.ROUND
            /** 设置画笔的样式 Paint.Cap.Round ,Cap.SQUARE等分别为圆形、方形 */
            strokeCap = Paint.Cap.ROUND
            /** 设置画笔的填充样式 Paint.Style.FILL  :填充内部;Paint.Style.FILL_AND_STROKE  ：填充内部和描边;  Paint.Style.STROKE  ：仅描边 */
            style = Paint.Style.STROKE
            /**抗锯齿功能 */
            isAntiAlias = true
            /**设置画笔宽度 */
            strokeWidth = borderWidth
        }

        /**绘制圆弧的方法
         * drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)//画弧，
         * 参数一是RectF对象，一个矩形区域椭圆形的界限用于定义在形状、大小、电弧，
         * 参数二是起始角(度)在电弧的开始，圆弧起始角度，单位为度。
         * 参数三圆弧扫过的角度，顺时针方向，单位为度,从右中间开始为零度。
         * 参数四是如果这是true(真)的话,在绘制圆弧时将圆心包括在内，通常用来绘制扇形；如果它是false(假)这将是一个弧线,
         * 参数五是Paint对象；
         */
        canvas.drawArc(rectF, startAngle, angleLength, false, paint)
    }

    /**
     * 2.绘制当前步数的红色圆弧
     */
    private fun drawArcRed(canvas: Canvas, rectF: RectF) {
        val paintCurrent = Paint().apply {
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND//圆角弧度
            style = Paint.Style.STROKE//设置填充样式
            isAntiAlias = true//抗锯齿功能
            strokeWidth = borderWidth//设置画笔宽度
            @Suppress("DEPRECATION")
            color = resources.getColor(R.color.colorLightSkyBlue) //设置画笔颜色
        }

        canvas.drawArc(rectF, startAngle, currentAngleLength, false, paintCurrent)
    }

    /**
     * 3.圆环中心的步数
     */
    private fun drawTextNumber(canvas: Canvas, centerX: Float) {
        val bounds = Rect()
        val vTextPaint = Paint().apply {
            textAlign = Paint.Align.CENTER
            isAntiAlias = true//抗锯齿功能
            textSize = numberTextSize
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)//字体风格
            @Suppress("DEPRECATION")
            color = resources.getColor(R.color.colorPrimary)
            getTextBounds(stepNumber, 0, stepNumber.length, bounds)
        }

        canvas.drawText(stepNumber, centerX, height / 2f + bounds.height() / 2f, vTextPaint)
    }

    /**
     * 4.圆环中心步数的文字
     */
    private fun drawTextStepString(canvas: Canvas, centerX: Float) {
        val stepString = "步数"
        val bounds = Rect()
        val vTextPaint = Paint().apply {
            textSize = sp2px(context, 16f).toFloat()
            textAlign = Paint.Align.CENTER
            isAntiAlias = true//抗锯齿功能
            color = Color.GRAY
            getTextBounds(stepString, 0, stepString.length, bounds)
        }

        canvas.drawText(stepString, centerX, height / 2f + bounds.height() + getFontHeight(numberTextSize), vTextPaint)
    }

    /**
     * 获取当前步数的数字的高度
     *
     * @param fontSize 字体大小
     * @return 字体高度
     */
    private fun getFontHeight(fontSize: Float): Int {
        val bounds = Rect()
        Paint().apply {
            textSize = fontSize
            getTextBounds(stepNumber, 0, stepNumber.length, bounds)
        }

        return bounds.height()
    }

    /**
     * 所走的步数进度
     *
     * @param totalStepNum  设置的步数
     * @param currentCounts 所走步数
     */
    fun setCurrentCount(totalStepNum: Int, currentCounts: Int) {
        var counts = currentCounts
        stepNumber = counts.toString()
        setTextSize(counts)
        /**如果当前走的步数超过总步数则圆弧还是270度，不能成为园 */
        if (counts > totalStepNum) {
            counts = totalStepNum
        }
        /**所走步数占用总共步数的百分比 */
        val scale = counts.toFloat() / totalStepNum
        /**换算成弧度最后要到达的角度的长度-->弧长 */
        val currentAngleLength = scale * angleLength
        /**开始执行动画 */
        if (!hasSetData) {
            setAnimation(lastAngleLength, currentAngleLength, animationLength)
            hasSetData = true
        } else {
            setAnimation(lastAngleLength, currentAngleLength, animationLength / 5)
        }
        /**上一次的弧长*/
        lastAngleLength = currentAngleLength
    }

    /**
     * 为进度设置动画
     * ValueAnimator是整个属性动画机制当中最核心的一个类，属性动画的运行机制是通过不断地对值进行操作来实现的，
     * 而初始值和结束值之间的动画过渡就是由ValueAnimator这个类来负责计算的。
     * 它的内部使用一种时间循环的机制来计算值与值之间的动画过渡，
     * 我们只需要将初始值和结束值提供给ValueAnimator，并且告诉它动画所需运行的时长，
     * 那么ValueAnimator就会自动帮我们完成从初始值平滑地过渡到结束值这样的效果。
     *
     * @param last
     * @param current
     */
    private fun setAnimation(last: Float, current: Float, length: Int) {
        val progressAnimator = ValueAnimator.ofFloat(last, current)
        progressAnimator.duration = length.toLong()
        progressAnimator.setTarget(currentAngleLength)
        progressAnimator.interpolator = BounceInterpolator()
        progressAnimator.addUpdateListener { animation ->
            currentAngleLength = animation.animatedValue as Float
            invalidate()
        }
        progressAnimator.start()
    }

    /**
     * 设置文本大小,防止步数特别大之后放不下，将字体大小动态设置
     *
     * @param num
     */
    private fun setTextSize(num: Int) {
        val s = num.toString()
        val length = s.length
        when {
            length <= 4 -> numberTextSize = sp2px(context, 50f).toFloat()
            length in 5..6 -> numberTextSize = sp2px(context, 40f).toFloat()
            length in 7..8 -> numberTextSize = sp2px(context, 30f).toFloat()
            length > 8 -> numberTextSize = sp2px(context, 25f).toFloat()
        }
    }
}