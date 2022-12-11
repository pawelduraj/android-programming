package com.example.app.ui.products

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
import com.example.app.ProductActivity
import com.example.app.R
import com.example.app.dao.Database
import com.example.app.databinding.FragmentProductsBinding
import com.example.app.models.Product

class ProductsFragment : Fragment() {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val productsViewModel = ViewModelProvider(this)[ProductsViewModel::class.java]
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val productsListView: ListView = binding.listViewProducts
        val productsAdapter =
            ProductsAdapter(this.requireContext(), productsViewModel.products.value!!)
        productsListView.adapter = productsAdapter
        productsViewModel.products.observe(viewLifecycleOwner) {
            productsAdapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ProductsAdapter(context: Context, products: List<Product>) :
    ArrayAdapter<Product>(context, R.layout.list_item_products, products) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val product = getItem(position) as Product

        val rowView = inflater.inflate(R.layout.list_item_products, parent, false)
        rowView.setOnClickListener {
            val intent = Intent(context, ProductActivity::class.java)
            intent.putExtra("name", product.name)
            intent.putExtra("category", product.category?.name ?: "-")
            intent.putExtra("price", (product.price / 100.0).toString())
            intent.putExtra("description", product.description)
            context.startActivity(intent)
        }

        val nameTextView = rowView.findViewById(R.id.name) as TextView
        nameTextView.text = product.name

        val categoryTextView = rowView.findViewById(R.id.category) as TextView
        categoryTextView.text = product.category?.name ?: "-"

        val priceButton = rowView.findViewById(R.id.price) as Button
        priceButton.text = (product.price / 100.0).toString()
        priceButton.setOnClickListener {
            if (Database.addToCart(product, 1))
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context, "Max quantity reached", Toast.LENGTH_SHORT).show()
            this.notifyDataSetChanged()
        }

        return rowView
    }
}
