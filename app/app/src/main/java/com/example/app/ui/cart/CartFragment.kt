package com.example.app.ui.cart

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
import com.example.app.*
import com.example.app.databinding.FragmentCartBinding
import com.example.app.ui.products.ProductsAdapter
import kotlin.math.roundToInt

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val cartListView: ListView = binding.listViewCart
        val cartAdapter = CartAdapter(this.requireContext(), cartViewModel.cartProducts.value!!)
        cartListView.adapter = cartAdapter
        cartViewModel.cartProducts.observe(viewLifecycleOwner) {
            cartAdapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CartAdapter(context: Context, cartProducts: List<CartProduct>) :
    ArrayAdapter<CartProduct>(context, R.layout.list_item_cart, cartProducts) {

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val product = getItem(position) as CartProduct

        val rowView = inflater.inflate(R.layout.list_item_cart, parent, false)
        rowView.setOnClickListener() {
            val intent = Intent(context, ProductActivity::class.java)
            intent.putExtra("name", product.product.name)
            intent.putExtra("category", product.product.category)
            intent.putExtra("price", product.product.price.toString())
            intent.putExtra("description", product.product.description)
            context.startActivity(intent)
        }

        val nameTextView = rowView.findViewById(R.id.name) as TextView
        nameTextView.text = product.product.name

        val categoryTextView = rowView.findViewById(R.id.category) as TextView
        categoryTextView.text = product.product.category

        val priceTextView = rowView.findViewById(R.id.price) as TextView
        priceTextView.text = product.quantity.toString() + " x " + product.product.price.toString()

        val totalTextView = rowView.findViewById(R.id.total) as TextView
        totalTextView.text =
            "= " + ((product.product.price * product.quantity * 100).roundToInt() / 100.0).toString()

        val removeButton = rowView.findViewById(R.id.remove) as Button
        removeButton.setOnClickListener {
            Cart.remove(product.product)
            this.notifyDataSetChanged()
        }

        val addButton = rowView.findViewById(R.id.add) as Button
        if (product.quantity == product.product.maxQuantity)
            addButton.isEnabled = false
        addButton.setOnClickListener {
            if (!Cart.add(product.product, 1))
                Toast.makeText(context, "Cannot add more items", Toast.LENGTH_SHORT).show()
            this.notifyDataSetChanged()
        }

        return rowView
    }
}
