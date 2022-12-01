package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        title = "Product details"
        val name = intent.getStringExtra("name")
        val category = intent.getStringExtra("category")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("description")

        findViewById<TextView>(R.id.name).text = name
        findViewById<TextView>(R.id.category).text = category
        findViewById<Button>(R.id.price).text = price
        findViewById<TextView>(R.id.description).text = description
    }
}
