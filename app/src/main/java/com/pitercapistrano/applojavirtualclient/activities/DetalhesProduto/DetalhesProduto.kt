package com.pitercapistrano.applojavirtualclient.activities.DetalhesProduto

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.activities.Pagamento.Pagamento
import com.pitercapistrano.applojavirtualclient.databinding.ActivityDetalhesProdutoBinding

class DetalhesProduto : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesProdutoBinding
    var tamanho_calcado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetalhesProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val foto = intent.extras?.getString("foto")
        val nome = intent.extras?.getString("nome")
        val preco = intent.extras?.getString("preco")

        Glide.with(this).load(foto).into(binding.imgFotoProduto)
        binding.nomeProdutoDt.text = nome
        binding.precoProdutoDt.text = "R$ ${preco}"

        binding.btFinalizarPedido.setOnClickListener {
            val tamanho_calcado_38 = binding.tamanho38
            val tamanho_calcado_39 = binding.tamanho39
            val tamanho_calcado_40 = binding.tamanho40
            val tamanho_calcado_41 = binding.tamanho41
            val tamanho_calcado_42 = binding.tamanho42


            when(true) {
                tamanho_calcado_38.isChecked -> tamanho_calcado = "38"
                tamanho_calcado_39.isChecked -> tamanho_calcado = "39"
                tamanho_calcado_40.isChecked -> tamanho_calcado = "40"
                tamanho_calcado_41.isChecked -> tamanho_calcado = "41"
                tamanho_calcado_42.isChecked -> tamanho_calcado = "42"

                else -> {
                    val snackbar =
                        Snackbar.make(it, "Escolha o tamanho do calçado", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.setTextColor(Color.WHITE)
                    snackbar.show()
                }
            }
            if (tamanho_calcado_38.isChecked || tamanho_calcado_39.isChecked || tamanho_calcado_40.isChecked || tamanho_calcado_41.isChecked || tamanho_calcado_42.isChecked){
                val intent = Intent(this, Pagamento::class.java)
                intent.putExtra("tamanho_calcado", tamanho_calcado)
                intent.putExtra("nome", nome)
                intent.putExtra("preco", preco)
                startActivity(intent)
            }

        }
    }
}