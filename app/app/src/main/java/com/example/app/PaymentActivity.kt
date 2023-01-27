package com.example.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.app.dao.Config
import com.example.app.dao.Database
import com.example.app.models.Product
import com.example.app.ui.orders.OrdersAdapter
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class PaymentActivity : AppCompatActivity() {
    private lateinit var stripePaymentSheet: PaymentSheet
    private var startedPayUPayment = false
    private var globalOrderId: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        title = "Order Details"

        val orderId = intent.getIntExtra("order_id", -1)
        val date = intent.getStringExtra("date")
        val price = intent.getIntExtra("price", -1)
        val paid = intent.getBooleanExtra("paid", false)

        globalOrderId = orderId

        findViewById<TextView>(R.id.order).text = "Order ID: $orderId"
        findViewById<TextView>(R.id.date).text = "Date: $date"
        findViewById<TextView>(R.id.price).text = "Price: ${price / 100.0}"
        findViewById<TextView>(R.id.status).text = "Status: ${if (paid) "Paid" else "Not paid"}"

        val products = Database.getProductsByOrderId(orderId) as ArrayList<Product>
        val orderProductsListView: ListView = findViewById(R.id.products)
        val orderProductsAdapter = OrderProductsAdapter(applicationContext, products)
        orderProductsListView.adapter = orderProductsAdapter

        if (paid) {
            findViewById<Button>(R.id.pay_stripe).visibility = Button.GONE
            findViewById<Button>(R.id.pay_payu).visibility = Button.GONE
        }

        PaymentConfiguration.init(applicationContext, Config.STRIPE_KEY)
        stripePaymentSheet = PaymentSheet(this, ::onStripePaymentSheetResult)

        findViewById<Button>(R.id.pay_stripe).setOnClickListener {
            if (Database.payStripe(orderId)) {
                val order = Database.getOrders().find { it.orderId == orderId }
                val configuration = PaymentSheet.Configuration("Example, Inc.")
                stripePaymentSheet.presentWithPaymentIntent(order!!.payment, configuration)
            } else {
                Toast.makeText(this, R.string.pay_fail, Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.pay_payu).setOnClickListener {
            val url = Database.payPayU(orderId)
            if (url == "")
                Toast.makeText(this, R.string.pay_fail, Toast.LENGTH_SHORT).show()
            else {
                startedPayUPayment = true
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(url)
                startActivity(openURL)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (startedPayUPayment && Database.finalizePayment(globalOrderId)) {
            Toast.makeText(this, R.string.pay_success, Toast.LENGTH_SHORT).show()
            finish()
        } else if (startedPayUPayment) {
            Toast.makeText(this, R.string.pay_fail, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onStripePaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                if (Database.finalizePayment(globalOrderId)) {
                    Toast.makeText(this, R.string.pay_success, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, R.string.pay_fail, Toast.LENGTH_SHORT).show()
                    Log.e("PaymentActivity", "Payment finalization failed")
                }
            }
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(this, R.string.pay_fail, Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(this, R.string.pay_fail, Toast.LENGTH_SHORT).show()
                Log.e("PaymentActivity", paymentResult.error.message ?: "")
            }
        }
    }
}

class OrderProductsAdapter(context: Context, products: List<Product>) :
    ArrayAdapter<Product>(context, R.layout.list_item_order_products, products) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val product = getItem(position) as Product

        val rowView = inflater.inflate(R.layout.list_item_order_products, parent, false)

        val nameTextView = rowView.findViewById(R.id.name) as TextView
        val nameTextViewString = product.inStock.toString() + " x " + product.name
        nameTextView.text = nameTextViewString

        val categoryTextView = rowView.findViewById(R.id.category) as TextView
        categoryTextView.text = product.category?.name ?: "-"

        val priceButton = rowView.findViewById(R.id.price) as Button
        priceButton.text = (product.inStock * product.price / 100.0).toString()

        return rowView
    }
}
