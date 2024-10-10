// Pacote que contém as atividades do aplicativo
package com.pitercapistrano.applojavirtualclient.activities.Home

// Importa as classes necessárias para a atividade
import android.content.Intent // Para iniciar novas atividades
import android.graphics.Color // Para manipulação de cores
import android.os.Bundle // Para gerenciar dados de instância da atividade
import android.view.Menu // Para manipulação de menus
import android.view.MenuItem // Para manipulação de itens de menu
import android.widget.Toast // Para exibir mensagens ao usuário
import androidx.activity.enableEdgeToEdge // Para habilitar o modo de borda a borda
import androidx.appcompat.app.AppCompatActivity // Para suportar funcionalidades da ActionBar
import androidx.core.view.ViewCompat // Para compatibilidade com visualizações
import androidx.core.view.WindowInsetsCompat // Para lidar com insets de janela
import androidx.recyclerview.widget.GridLayoutManager // Para layout de grid no RecyclerView
import com.google.firebase.auth.FirebaseAuth // Para autenticação com Firebase
import com.pitercapistrano.applojavirtualclient.R // Para acesso a recursos
import com.pitercapistrano.applojavirtualclient.activities.FormLogin.FormLogin // Atividade de login
import com.pitercapistrano.applojavirtualclient.activities.Pedidos.Pedidos // Atividade de pedidos
import com.pitercapistrano.applojavirtualclient.adapter.AdapterProduto // Adaptador para produtos
import com.pitercapistrano.applojavirtualclient.databinding.ActivityHomeBinding // Para vinculação de dados
import com.pitercapistrano.applojavirtualclient.dialog.DialogPerfil // Diálogo de perfil do usuário
import com.pitercapistrano.applojavirtualclient.model.DB // Modelo para acesso ao banco de dados
import com.pitercapistrano.applojavirtualclient.model.Produto // Modelo para produto

// Classe que representa a tela inicial do aplicativo
class Home : AppCompatActivity() {

    // Variável para vinculação da atividade
    lateinit var binding: ActivityHomeBinding
    // Variável para o adaptador de produtos
    lateinit var adapterProduto: AdapterProduto
    // Lista mutável para armazenar os produtos
    var listaProdutos: MutableList<Produto> = mutableListOf()

    // Método chamado na criação da atividade
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Chama o método da superclasse
        enableEdgeToEdge() // Habilita o modo de borda a borda para a atividade
        // Infla o layout da atividade e vincula a variável de binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root) // Define a visualização da atividade

        // Define um listener para aplicar insets de janela na visualização principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Obtém os insets das barras do sistema
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Define o padding da visualização de acordo com os insets
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Retorna os insets
        }

        // Define a cor da barra de status
        window.statusBarColor = Color.parseColor("#000000")

        // Configura o RecyclerView para exibir produtos
        val recyclerProdutos = binding.recyclerProdutos
        recyclerProdutos.layoutManager = GridLayoutManager(this, 2) // Define o gerenciador de layout como Grid com 2 colunas
        recyclerProdutos.setHasFixedSize(true) // Define que o tamanho do RecyclerView é fixo
        adapterProduto = AdapterProduto(this, listaProdutos) // Inicializa o adaptador de produtos
        recyclerProdutos.adapter = adapterProduto // Define o adaptador para o RecyclerView

        // Cria uma instância do banco de dados
        val db = DB()
        // Obtém a lista de produtos do banco de dados e a armazena na lista de produtos
        db.obterListaProdutos(listaProdutos, adapterProduto)
    }

    // Método para criar o menu da atividade
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Infla o menu a partir do recurso menu_principal
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true // Retorna true para indicar que o menu foi criado
    }

    // Método chamado quando um item do menu é selecionado
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { // Verifica qual item do menu foi selecionado
            R.id.perfil -> iniciarPerfil() // Chama o método para iniciar o perfil
            R.id.pedidos -> irTelaPedidos() // Chama o método para ir à tela de pedidos
            R.id.sair -> deslogar() // Chama o método para deslogar
        }
        return super.onOptionsItemSelected(item) // Chama o método da superclasse
    }

    // Método para deslogar o usuário
    private fun deslogar() {
        FirebaseAuth.getInstance().signOut() // Desloga o usuário atual do Firebase
        val intent = Intent(this, FormLogin::class.java) // Cria um intent para a tela de login
        startActivity(intent) // Inicia a atividade de login
        finish() // Finaliza a atividade atual
        Toast.makeText(this, "Usuário Deslogado com Sucesso!", Toast.LENGTH_SHORT).show() // Exibe uma mensagem de sucesso
    }

    // Método para ir à tela de pedidos
    private fun irTelaPedidos() {
        val intent = Intent(this, Pedidos::class.java) // Cria um intent para a tela de pedidos
        startActivity(intent) // Inicia a atividade de pedidos
    }

    // Método para iniciar o diálogo de perfil
    private fun iniciarPerfil() {
        val dialogPerfil = DialogPerfil(this) // Cria uma instância do diálogo de perfil
        dialogPerfil.iniciarPerfil() // Inicia o diálogo de perfil
        dialogPerfil.recuperarDadosPerfil() // Recupera os dados do perfil
    }
}
