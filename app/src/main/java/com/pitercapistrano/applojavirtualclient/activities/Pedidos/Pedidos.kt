// Pacote da aplicação que contém a classe Pedidos
package com.pitercapistrano.applojavirtualclient.activities.Pedidos

// Importações necessárias para o funcionamento da classe
import android.graphics.Color // Para manipulação de cores
import android.os.Bundle // Para gerenciar o estado da atividade
import android.view.View // Para manipulação de visualizações
import androidx.activity.enableEdgeToEdge // Para permitir que a atividade use a tela inteira
import androidx.appcompat.app.AppCompatActivity // Classe base para atividades compatíveis com ActionBar
import androidx.core.view.ViewCompat // Para compatibilidade com visualizações
import androidx.core.view.WindowInsetsCompat // Para gerenciar insets da janela
import androidx.recyclerview.widget.LinearLayoutManager // Para exibir a lista de pedidos em um layout linear
import com.pitercapistrano.applojavirtualclient.R // Importa os recursos da aplicação
import com.pitercapistrano.applojavirtualclient.adapter.AdapterPedido // Importa o adaptador para a lista de pedidos
import com.pitercapistrano.applojavirtualclient.databinding.ActivityPedidosBinding // Importa o binding da atividade
import com.pitercapistrano.applojavirtualclient.model.DB // Importa a classe para manipulação do banco de dados
import com.pitercapistrano.applojavirtualclient.model.Pedido // Importa a classe modelo de Pedido

// Classe Pedidos que herda de AppCompatActivity
class Pedidos : AppCompatActivity() {

    // Declaração das variáveis necessárias
    lateinit var binding: ActivityPedidosBinding // Variável para o binding da atividade
    lateinit var adapterPedidos: AdapterPedido // Adaptador para a lista de pedidos
    var lista_pedidos: MutableList<Pedido> = mutableListOf() // Lista mutável para armazenar os pedidos

    // Método chamado quando a atividade é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Chama o método da superclasse
        enableEdgeToEdge() // Habilita o modo de tela cheia para a atividade

        // Infla o layout da atividade e associa ao binding
        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root) // Define o conteúdo da atividade

        // Define um listener para aplicar insets de janela à visualização principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // Obtém os insets das barras do sistema
            // Define padding para a visualização com base nos insets das barras do sistema
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Retorna os insets
        }

        // Define a cor da barra de status da janela
        window.statusBarColor = Color.parseColor("#000000")
        // Define a visibilidade da interface do usuário para a janela
        window.decorView.systemUiVisibility = 0

        // Inicializa o RecyclerView para exibir a lista de pedidos
        val recycler_pedidos = binding.recyclerPedidos
        recycler_pedidos.layoutManager = LinearLayoutManager(this) // Define o layout manager como linear
        recycler_pedidos.setHasFixedSize(true) // Otimiza o RecyclerView, pois o tamanho não mudará

        // Cria uma nova instância do adaptador para a lista de pedidos
        adapterPedidos = AdapterPedido(this, lista_pedidos)
        recycler_pedidos.adapter = adapterPedidos // Define o adaptador no RecyclerView

        // Cria uma instância da classe de banco de dados
        val db = DB()
        // Obtém a lista de pedidos do banco de dados e a atualiza no adaptador
        db.obterListaPedidos(lista_pedidos, adapterPedidos)
    }
}
