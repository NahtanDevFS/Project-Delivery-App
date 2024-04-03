package com.appcliente.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcliente.CongratsBottomSheet
import com.appcliente.PayOutActivity
import com.appcliente.R
import com.appcliente.adapter.CartAdapter
import com.appcliente.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private lateinit var binding:FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)



        val cartFoodName = listOf("Pie de frutas", "Ensalada", "Helado", "Sopa", "pizza", "hamburguesa")
        val cartItemPrice = listOf("Q35","Q25","Q60","Q30", "Q50", "Q45")
        val cartImage = listOf(R.drawable.menu1, R.drawable.menu2, R.drawable.menu3, R.drawable.menu4, R.drawable.menu5, R.drawable.menu6)
        val adapter = CartAdapter(ArrayList(cartFoodName), ArrayList(cartItemPrice), ArrayList(cartImage))
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter

        binding.proceedButton.setOnClickListener {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    companion object {

    }
}