package com.example.kinwae.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable

import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.AttributeSet
import android.view.View
import android.util.DisplayMetrics
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.kinwae.R


class StepperView : ConstraintLayout {

    val checkViews: MutableList<CheckView> = mutableListOf()
    val guidelines: MutableList<View> = mutableListOf()
    lateinit var lineView: View
    lateinit var completeLineView: View
    var count: Int = 2
    var currentStep: Int = 0

    var stepperClickListeners : StepperClickListeners? = null

    var entries: Array<CharSequence>? = null

    constructor(context: Context) : super(context) {
        init(null)
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.StepperView, 0, 0)

        entries = attr.getTextArray(R.styleable.StepperView_titles)

        count = attr.getInt(R.styleable.StepperView_quantity, 2) - 1

        if (count < 1) {
            count = 1
        }

        createCompleteLine()
        createCheckViews()
        createGuidelines()
        createLines()

        invalidate()

        attr.recycle()
    }

    private fun createCheckViews() {
        for (i in 0..count) {
            val checkView: CheckView = CheckView(context)
            checkView.setText((i + 1).toString())
            try {
                entries?.let {
                    checkView.setLabelText(it[i].toString())
                }
            }catch (e: ArrayIndexOutOfBoundsException){
                e.printStackTrace()
            }

            val lp = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT)

            checkView.id = 1651651651 + i
            checkView.layoutParams = lp

            addView(checkView, -1)

            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            if (i == 0) {
                constraintSet.connect(checkView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
            } else if (i == count) {
                constraintSet.connect(checkView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
            } else {
                constraintSet.connect(checkView.id, ConstraintSet.START, checkViews.get(i - 1).id, ConstraintSet.END, 0)
            }

            checkView.setOnClickListener {
                stepperClickListeners?.onStepClick(i + 1)
                goToStep(i)
            }

            checkViews.add(checkView)
            constraintSet.applyTo(this)
        }

        for (i in 0..count) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            if (!(i == count || i == 0)) {
                constraintSet.connect(checkViews.get(i).id, ConstraintSet.END, checkViews.get(i + 1).id, ConstraintSet.START, 0)
            }

            constraintSet.applyTo(this)
        }
    }

    @SuppressLint("ResourceType")
    private fun createLines() {
        val lineView: View = View(context)

        val lineLayoutParams = LayoutParams(
                0,
                6)

        lineView.id = 1751651651

        lineView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_color))

        lineView.layoutParams = lineLayoutParams

        addView(lineView, 0)

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        constraintSet.connect(lineView.id, ConstraintSet.START, guidelines[0].id, ConstraintSet.START, 0)
        constraintSet.connect(lineView.id, ConstraintSet.END, guidelines[count].id, ConstraintSet.END, 0)

        constraintSet.connect(lineView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, getCompleteLineTopMargin())

        constraintSet.connect(completeLineView.id, ConstraintSet.END, guidelines[0].id, ConstraintSet.END,  0)
        constraintSet.connect(completeLineView.id, ConstraintSet.START, guidelines[0].id, ConstraintSet.START, 0)

        constraintSet.applyTo(this)
        this.lineView = lineView

    }

    private fun createGuidelines() {
        for (i in 0..count) {
            val guideline: View = View(context)

            val lineLayoutParams = LayoutParams(
                    1,
                    1)


            guideline.id = 88196192 + i

            guideline.layoutParams = lineLayoutParams

            addView(guideline, 0)

            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            constraintSet.connect(guideline.id, ConstraintSet.START, checkViews.get(i).id, ConstraintSet.START, 0)
            constraintSet.connect(guideline.id, ConstraintSet.END, checkViews.get(i).id, ConstraintSet.END, 0)
            constraintSet.connect(guideline.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
            constraintSet.connect(guideline.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)

            constraintSet.applyTo(this)

            guidelines.add(guideline)
        }
    }


    @SuppressLint("ResourceType")
    private fun createCompleteLine() {
        val completeLineView = View(context)

        val lineLayoutParams = LayoutParams(
                0,
                6)

        completeLineView.id = 1756131651

        completeLineView.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))

        completeLineView.layoutParams = lineLayoutParams

        addView(completeLineView, -1)

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        constraintSet.connect(completeLineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 5)
        constraintSet.connect(completeLineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)

        constraintSet.connect(completeLineView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, getCompleteLineTopMargin())

        constraintSet.applyTo(this)

        this.completeLineView = completeLineView
    }

    fun goToNextStep() {

        if (checkViews.get(currentStep).isChecked()) {

            if (isInitialStep()) {
                checkViews.get(currentStep).markAsFinished()
                incrementCurrentStep()
            } else {
                val constraintSet = ConstraintSet()
                constraintSet.clone(this)
                constraintSet.connect(completeLineView.id, ConstraintSet.END, guidelines.get(currentStep).id, ConstraintSet.END, 5)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(this, getCompleteLineTransition(checkViews.get(currentStep)))
                } else {
                    checkViews.get(currentStep).markAsFinished()
                    incrementCurrentStep()
                }
                constraintSet.applyTo(this)
            }
        } else {
            checkViews.get(currentStep).checkButton()
            incrementCurrentStep()
        }

    }

    fun goToPreviousStep() {
        decrementCurrentStep()
        if (checkViews.get(currentStep).isMarkAsFinished()) {

            if (isInitialStep()) {
                checkViews.get(currentStep).checkButton()
            } else {
                val constraintSet = ConstraintSet()
                constraintSet.clone(this)
                constraintSet.connect(completeLineView.id, ConstraintSet.END, guidelines.get(currentStep - 1).id, ConstraintSet.END, 0)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(this, getCompleteLineReverseTransition(checkViews.get(currentStep)))
                    checkViews.get(currentStep).checkButton()
                } else {
                    checkViews.get(currentStep).checkButton()
                }
                constraintSet.applyTo(this)
            }
        } else {
            checkViews.get(currentStep).unCheckButton()
        }
    }

    private fun getCompleteLineTopMargin() : Int{
        val valueInPixels = getResources().getDimension(R.dimen.checkview_size).toInt()
        return valueInPixels / 2
    }

    private fun isInitialStep(): Boolean {
        return currentStep == 0
    }

    private fun isLastStep(): Boolean {
        return currentStep == count
    }

    private fun incrementCurrentStep() {
        if (currentStep < count && checkViews[currentStep].isMarkAsFinished()) {
            currentStep++
        }
    }

    private fun decrementCurrentStep() {
        if (currentStep > 0 && checkViews[currentStep].unChecked()) {
            currentStep--
        }
    }

    fun resetStepper() {

        checkViews.forEach {
            it.unChecked()
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        constraintSet.connect(completeLineView.id, ConstraintSet.END, guidelines[0].id, ConstraintSet.END, 0)

        constraintSet.applyTo(this)
    }


    private fun getCompleteLineTransition(checkView: CheckView): TransitionSet? {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionSet()
                    .addTransition(ChangeBounds().setDuration(200).addTarget(completeLineView.id).addListener(object : Transition.TransitionListener {
                        override fun onTransitionEnd(p0: Transition?) {
                            checkView.markAsFinished()
                            incrementCurrentStep()
                        }

                        override fun onTransitionResume(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionPause(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionCancel(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionStart(p0: Transition?) {
                            //no use
                        }

                    }))
                    .setInterpolator(FastOutSlowInInterpolator())
        } else {
            return null
        };
    }


    private fun getCompleteLineReverseTransition(checkView: CheckView): TransitionSet? {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionSet()
                    .addTransition(ChangeBounds().setDuration(300).addTarget(completeLineView.id).addListener(object : Transition.TransitionListener {
                        override fun onTransitionEnd(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionResume(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionPause(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionCancel(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionStart(p0: Transition?) {
                            checkView.checkButton()
                        }

                    }))
                    .setInterpolator(FastOutSlowInInterpolator())
        } else {
            return null
        };
    }


    public override fun onSaveInstanceState(): Parcelable? {
        //begin boilerplate code that allows parent classes to save state
        val superState = super.onSaveInstanceState()

        val ss = superState?.let { SavedState(it) }
        //end

        ss?.currentStep = this.currentStep


        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        //begin boilerplate code so parent classes can restore state
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        //end

        this.currentStep = state.currentStep

        recoverState(currentStep = currentStep)
    }

    private fun recoverState(currentStep: Int = 0) {

        if (currentStep > 0) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            constraintSet.connect(completeLineView.id, ConstraintSet.END, checkViews[currentStep - 1].id, ConstraintSet.END, 5)

            constraintSet.applyTo(this)
        }
    }

    internal class SavedState : View.BaseSavedState {
        var currentStep: Int = 0
        var stepStatus: Int = 0

        constructor(superState: Parcelable) : super(superState) {}

        private constructor(inParcelable: Parcel) : super(inParcelable) {
            this.currentStep = inParcelable.readInt()
            this.stepStatus = inParcelable.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)

            out.writeInt(currentStep)
            out.writeInt(stepStatus)
        }


        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }



    interface StepperClickListeners{
        fun onStepClick(position :Int)
    }

    fun goToStep(position :Int){

        for(i in 0..position){
            if(!checkViews.get(i).isChecked()){
                checkViews.get(i).markAsFinished()
            }
        }

        for(i in position + 1 until checkViews.size){
            checkViews.get(i).unCheckButton()
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        if(position > 0){
            constraintSet.connect(completeLineView.id, ConstraintSet.END, guidelines.get(position).id, ConstraintSet.END, 5)
        }else{
            constraintSet.connect(completeLineView.id, ConstraintSet.END, guidelines[0].id, ConstraintSet.END,  0)
            constraintSet.connect(completeLineView.id, ConstraintSet.START, guidelines[0].id, ConstraintSet.START, 0)
        }

        constraintSet.applyTo(this)

        currentStep = position + 1
    }

    public fun goToCheckedStep(step: Int = 0){
        //TODO add validations

        currentStep = step;

        for(i in 0..checkViews.size -1){
            if(i <= currentStep - 1){
                checkViews.get(i).checkButton()
                checkViews.get(i).markAsFinished()
            }else if(i == currentStep) {
                checkViews.get(i).checkButton()
            }else{
                checkViews.get(i).unCheckButton()
            }
        }

        if (currentStep > 0) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            constraintSet.connect(completeLineView.id, ConstraintSet.END, guidelines.get(currentStep - 1).id, ConstraintSet.END, 0)
            constraintSet.applyTo(this)
        }
        if(currentStep > count){
            currentStep = count;
        }
    }
}
