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
import com.google.firebase.auth.FirebaseAuth
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.activities.FormLogin.FormLogin
import com.pitercapistrano.applojavirtualclient.databinding.ActivityHomeBinding
import com.pitercapistrano.applojavirtualclient.dialog.DialogPerfil

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.perfil -> iniciarPerfil()
            R.id.pedidos -> Toast.makeText(this, "Ir para tela de pedidos!", Toast.LENGTH_SHORT).show()
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

    private fun iniciarPerfil(){
        val dialogPerfil = DialogPerfil(this)
        dialogPerfil.iniciarPerfil()
        dialogPerfil.recuperarDadosPerfil()
    }
}