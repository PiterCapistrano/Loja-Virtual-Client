package com.pitercapistrano.applojavirtualclient.activities.FormLogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.activities.FormCadastro.FormCadastro
import com.pitercapistrano.applojavirtualclient.databinding.ActivityFormLoginBinding

class FormLogin : AppCompatActivity() {

    private lateinit var binding: ActivityFormLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = Color.parseColor("#000000")
        supportActionBar!!.hide()

        binding.txtCadastrar.setOnClickListener {
            val intent = Intent(this, FormCadastro::class.java)
            startActivity(intent)
        }
    }
}