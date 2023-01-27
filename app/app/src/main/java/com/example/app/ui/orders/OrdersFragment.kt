package com.example.app.ui.orders

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.app.PaymentActivity
import com.example.app.R
import com.example.app.dao.Database
import com.example.app.databinding.FragmentOrdersBinding
import com.example.app.models.Order

class OrdersFragment : Fragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ordersViewModel = ViewModelProvider(this)[OrdersViewModel::class.java]
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val ordersListView: ListView = binding.listViewOrders
        val ordersAdapter = OrdersAdapter(this.requireContext(), ordersViewModel.orders.value!!)
        ordersListView.adapter = ordersAdapter
        ordersViewModel.orders.observe(viewLifecycleOwner) {
            ordersAdapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class OrdersAdapter(context: Context, orders: List<Order>) :
    ArrayAdapter<Order>(context, R.layout.list_item_orders, orders) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val order = getItem(position) as Order

        val rowView = inflater.inflate(R.layout.list_item_orders, parent, false)
        rowView.setOnClickListener {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra("order_id", order.orderId)
            intent.putExtra("user_id", order.userId)
            intent.putExtra("date", order.date)
            intent.putExtra("price", order.price)
            intent.putExtra("paid", order.paid)
            intent.putExtra("payment", order.payment)
            context.startActivity(intent)
        }

        val orderTextView: TextView = rowView.findViewById(R.id.order)
        val orderTextViewString = "${context.getString(R.string.orders_order_id)}: ${order.orderId}"
        orderTextView.text = orderTextViewString

        val dateTextView: TextView = rowView.findViewById(R.id.date)
        val dateTextViewString = "${context.getString(R.string.orders_date)}: ${order.date}"
        dateTextView.text = dateTextViewString

        val priceTextView: TextView = rowView.findViewById(R.id.price)
        val priceTextViewString =
            "${context.getString(R.string.orders_price)}: ${(order.price / 100.0)}"
        priceTextView.text = priceTextViewString

        val statusTextView: TextView = rowView.findViewById(R.id.status)
        val statusTextViewString =
            "${context.getString(R.string.orders_status)}: ${if (order.paid) "Paid" else "Not paid"}"
        statusTextView.text = statusTextViewString

        val button = rowView.findViewById(R.id.button) as Button
        button.visibility = if (order.paid) View.GONE else View.VISIBLE
        button.setOnClickListener {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra("order_id", order.orderId)
            intent.putExtra("user_id", order.userId)
            intent.putExtra("date", order.date)
            intent.putExtra("price", order.price)
            intent.putExtra("paid", order.paid)
            intent.putExtra("payment", order.payment)
            context.startActivity(intent)
        }

        return rowView
    }
}
