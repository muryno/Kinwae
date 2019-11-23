package com.example.kinwae

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kinwae.utils.StepperView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        stepper.stepperClickListeners = object: StepperView.StepperClickListeners {
            override fun onStepClick(position: Int) {

                Toast.makeText(this@MainActivity, position.toString(), Toast.LENGTH_SHORT).show()
            }

        }

    }
}
