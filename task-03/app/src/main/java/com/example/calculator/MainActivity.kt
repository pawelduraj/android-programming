package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.operator.Operator

val percent = object : Operator("%", 1, true, PRECEDENCE_POWER + 10_000) {
    override fun apply(vararg args: Double): Double {
        return args[0] / 100
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val textview: TextView = findViewById(R.id.textview)
        val edittext: EditText = findViewById(R.id.edittext)
        edittext.showSoftInputOnFocus = false

        findViewById<Button>(R.id.button_11).setOnClickListener {
            if (textview.text.isNotEmpty()) {
                edittext.setText(textview.text)
                edittext.setSelection(edittext.text.length)
            }
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_13).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).dropLast(1).plus(text.substring(pos)))
            if (pos > 0) edittext.setSelection(pos - 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_21).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("/").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_22).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("7").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_23).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("8").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_24).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("9").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_25).setOnClickListener {
            edittext.setText("")
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_31).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("*").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_32).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("4").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_33).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("5").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_34).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("6").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_35).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("%").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_41).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("-").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_42).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("1").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_43).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("2").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_44).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("3").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_45).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("log()").plus(text.substring(pos)))
            edittext.setSelection(pos + 4)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_51).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("+").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_52).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("()").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_53).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("0").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_54).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus(".").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }

        findViewById<Button>(R.id.button_55).setOnClickListener {
            val pos = edittext.selectionStart
            val text = edittext.text.toString()
            edittext.setText(text.substring(0, pos).plus("^").plus(text.substring(pos)))
            edittext.setSelection(pos + 1)
            eval(edittext, textview)
        }
    }

    private fun eval(edittext: EditText, textview: TextView) {
        val text = edittext.text.toString()
        try {
            val result = ExpressionBuilder(text).operator(percent).build().evaluate().toString()
            if (result.endsWith(".0")) textview.text = result.substring(0, result.length - 2)
            else textview.text = result
        } catch (e: Exception) {
            textview.text = ""
        }
    }
}
