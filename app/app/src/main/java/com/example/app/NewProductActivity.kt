package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.app.dao.Database

class NewProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        val categories = Database.getCategories()
        val stringCategories = categories.map { it.name }
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stringCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        findViewById<Button>(R.id.button).setOnClickListener {
            val name = findViewById<EditText>(R.id.editTextName).text.toString()
            val category = categories[spinner.selectedItemPosition]
            val price = findViewById<EditText>(R.id.editTextPrice).text.toString().toInt()
            val inStock = findViewById<EditText>(R.id.editTextInStock).text.toString().toInt()
            val description =
                findViewById<EditText>(R.id.editTextMultilineDescription).text.toString()
            if (Database.addNewProduct(name, category, price, inStock, description)) {
                finish()
            } else {
                Toast.makeText(this, "Cannot create product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
