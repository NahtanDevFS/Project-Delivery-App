package com.apprestaurante

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.apprestaurante.adapter.DeliveryAdapter
import com.apprestaurante.databinding.ActivityOutForDeliveryBinding
import com.apprestaurante.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding:ActivityOutForDeliveryBinding by lazy{
     ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList: ArrayList<OrderDetails> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    binding.backButton.setOnClickListener{
        finish()
    }
        // recuperar y mostrar los datos de las órdenes completadas
        retrieveCompleteOrderDetail()

    }

    private fun retrieveCompleteOrderDetail() {
        //inicializar database de firebase
        database = FirebaseDatabase.getInstance()
        val completeOrderReference = database.reference.child("CompleteOrder")
            .orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // limpiar la lista antes de poblarla con nuevos datos
                listOfCompleteOrderList.clear()

                for(orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }

                }
                //poner el reversa la lista, para poner primero al elemento más reciente
                listOfCompleteOrderList.reverse()

                setDataIntoRecyclerView()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setDataIntoRecyclerView() {
        //inicializacion de la lista para mostrar el nombre del cliente y el estado de su pago
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()

        for (order in listOfCompleteOrderList) {
            order.userName?.let { customerName.add(it) }
            moneyStatus.add(order.paymentReceived)
        }

        val adapter = DeliveryAdapter(customerName, moneyStatus)
        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}