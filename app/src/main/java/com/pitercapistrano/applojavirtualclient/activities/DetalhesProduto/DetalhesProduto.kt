package com.pitercapistrano.applojavirtualclient.activities.DetalhesProduto
// Declara o pacote onde a classe DetalhesProduto está localizada.

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
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
// Define a classe DetalhesProduto, que será a tela de detalhes de um produto.

    private lateinit var binding: ActivityDetalhesProdutoBinding
    // Declara a variável "binding" para acessar os elementos da interface com view binding.

    var tamanho_calcado = ""
    // Declara uma variável para armazenar o tamanho do calçado selecionado pelo usuário.

    override fun onCreate(savedInstanceState: Bundle?) {
        // Método onCreate, chamado quando a atividade é criada.

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Ativa a exibição em tela cheia (Edge-to-Edge), usando o espaço das barras de status e navegação.

        binding = ActivityDetalhesProdutoBinding.inflate(layoutInflater)
        // Inicializa o binding para inflar o layout da tela de detalhes do produto.

        setContentView(binding.root)
        // Define o layout da atividade como o conteúdo principal da tela.

        // Adiciona margens para as barras de sistema (status e navegação) no layout principal.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = Color.parseColor("#000000")
        // Define a cor da barra de status como preta.

        window.decorView.systemUiVisibility = 0
        // Define a visibilidade do sistema UI para o padrão.

        // Obtém os dados do produto passados via Intent.
        val foto = intent.extras?.getString("foto")
        val nome = intent.extras?.getString("nome")
        val preco = intent.extras?.getString("preco")

        // Usa a biblioteca Glide para carregar a imagem do produto na ImageView.
        Glide.with(this).load(foto).into(binding.imgFotoProduto)

        // Define o nome e o preço do produto nos TextViews correspondentes.
        binding.nomeProdutoDt.text = nome
        binding.precoProdutoDt.text = "R$ ${preco}"

        // Define um clique para o botão "Finalizar Pedido".
        binding.btFinalizarPedido.setOnClickListener {

            // Obtém as referências dos RadioButtons que representam os tamanhos de calçado.
            val tamanho_calcado_38 = binding.tamanho38
            val tamanho_calcado_39 = binding.tamanho39
            val tamanho_calcado_40 = binding.tamanho40
            val tamanho_calcado_41 = binding.tamanho41
            val tamanho_calcado_42 = binding.tamanho42

            // Verifica qual tamanho de calçado foi selecionado.
            when (true) {
                tamanho_calcado_38.isChecked -> tamanho_calcado = "38"
                tamanho_calcado_39.isChecked -> tamanho_calcado = "39"
                tamanho_calcado_40.isChecked -> tamanho_calcado = "40"
                tamanho_calcado_41.isChecked -> tamanho_calcado = "41"
                tamanho_calcado_42.isChecked -> tamanho_calcado = "42"

                // Caso nenhum tamanho tenha sido selecionado, exibe uma mensagem de erro.
                else -> {
                    val snackbar = Snackbar.make(it, "Escolha o tamanho do calçado", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    // Define a cor de fundo do Snackbar como vermelha.

                    snackbar.setTextColor(Color.WHITE)
                    // Define a cor do texto do Snackbar como branca.

                    snackbar.show()
                    // Exibe o Snackbar.
                }
            }

            // Se algum tamanho foi selecionado, inicia a próxima tela de pagamento.
            if (tamanho_calcado_38.isChecked || tamanho_calcado_39.isChecked || tamanho_calcado_40.isChecked || tamanho_calcado_41.isChecked || tamanho_calcado_42.isChecked) {
                val intent = Intent(this, Pagamento::class.java)
                // Cria um Intent para abrir a atividade de pagamento.

                intent.putExtra("tamanho_calcado", tamanho_calcado)
                // Adiciona o tamanho do calçado como extra no Intent.

                intent.putExtra("nome", nome)
                // Adiciona o nome do produto como extra no Intent.

                intent.putExtra("preco", preco)
                // Adiciona o preço do produto como extra no Intent.

                startActivity(intent)
                // Inicia a atividade de pagamento.
            }
        }
    }
}
