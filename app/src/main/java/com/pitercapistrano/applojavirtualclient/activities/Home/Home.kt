package com.pitercapistrano.applojavirtualclient.activities.Home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.activities.FormLogin.FormLogin
import com.pitercapistrano.applojavirtualclient.activities.Pedidos.Pedidos
import com.pitercapistrano.applojavirtualclient.adapter.AdapterProduto
import com.pitercapistrano.applojavirtualclient.databinding.ActivityHomeBinding
import com.pitercapistrano.applojavirtualclient.dialog.DialogPerfil
import com.pitercapistrano.applojavirtualclient.model.DB
import com.pitercapistrano.applojavirtualclient.model.Produto

class Home : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var adapterProduto: AdapterProduto
    var listaProdutos: MutableList<Produto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerProdutos = binding.recyclerProdutos
        recyclerProdutos.layoutManager = GridLayoutManager(this, 2)
        recyclerProdutos.setHasFixedSize(true)
        adapterProduto = AdapterProduto(this, listaProdutos)
        recyclerProdutos.adapter = adapterProduto

        val db = DB()
        db.obterListaProdutos(listaProdutos, adapterProduto)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.perfil -> iniciarPerfil()
            R.id.pedidos -> irTelaPedidos()
            R.id.sair -> deslogar()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deslogar(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, FormLogin::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(this, "Usuário Deslogado com Sucesso!", Toast.LENGTH_SHORT).show()
    }

    private fun irTelaPedidos(){
        val intent = Intent(this, Pedidos::class.java)
        startActivity(intent)
    }

    private fun iniciarPerfil(){
        val dialogPerfil = DialogPerfil(this)
        dialogPerfil.iniciarPerfil()
        dialogPerfil.recuperarDadosPerfil()
    }
}