package com.pitercapistrano.applojavirtualclient.activities.Pedidos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.adapter.AdapterPedido
import com.pitercapistrano.applojavirtualclient.databinding.ActivityPedidosBinding
import com.pitercapistrano.applojavirtualclient.model.DB
import com.pitercapistrano.applojavirtualclient.model.Pedido

class Pedidos : AppCompatActivity() {

    lateinit var binding: ActivityPedidosBinding
    lateinit var adapterPedidos: AdapterPedido
    var lista_pedidos: MutableList<Pedido> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recycler_pedidos = binding.recyclerPedidos
        recycler_pedidos.layoutManager = LinearLayoutManager(this)
        recycler_pedidos.setHasFixedSize(true)
        adapterPedidos = AdapterPedido(this, lista_pedidos)
        recycler_pedidos.adapter = adapterPedidos

        val db = DB()
        db.obterListaPedidos(lista_pedidos, adapterPedidos)

    }
}