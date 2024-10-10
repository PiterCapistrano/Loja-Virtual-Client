// Pacote da aplicação que contém o adaptador de produtos
package com.pitercapistrano.applojavirtualclient.adapter

// Importações necessárias
import android.content.Context // Para manipular o contexto da aplicação
import android.content.Intent // Para iniciar novas atividades
import android.view.LayoutInflater // Para inflar layouts
import android.view.View // Para manipulação de visualizações
import android.view.ViewGroup // Para representar um grupo de visualizações
import androidx.recyclerview.widget.RecyclerView // Para utilizar RecyclerView
import com.bumptech.glide.Glide // Para carregar imagens de forma eficiente
import com.pitercapistrano.applojavirtualclient.activities.DetalhesProduto.DetalhesProduto // Atividade para mostrar detalhes do produto
import com.pitercapistrano.applojavirtualclient.databinding.ProdutoItemBinding // Para o binding dos itens de produto
import com.pitercapistrano.applojavirtualclient.model.Produto // Para utilizar a classe modelo Produto

// Classe AdapterProduto que estende RecyclerView.Adapter com um ViewHolder do tipo ProdutoViewHolder
class AdapterProduto(val context: Context, val lista_produtos: MutableList<Produto>) :
    RecyclerView.Adapter<AdapterProduto.ProdutoViewHolder>() {

    // Método chamado para criar um novo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        // Infla o layout do item de produto usando o binding
        val item_lista = ProdutoItemBinding.inflate(LayoutInflater.from(context), parent, false)
        // Retorna uma nova instância do ViewHolder com o layout inflado
        return ProdutoViewHolder(item_lista)
    }

    // Método que retorna a quantidade de itens na lista de produtos
    override fun getItemCount() = lista_produtos.size

    // Método que vincula os dados do produto ao ViewHolder correspondente
    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        // Usa Glide para carregar a imagem do produto no ImageView correspondente
        Glide.with(context).load(lista_produtos.get(position).foto).into(holder.fotoProduto)
        // Define o nome do produto no TextView correspondente
        holder.nomeProduto.text = lista_produtos.get(position).nome
        // Define o preço do produto no TextView correspondente
        holder.precoProduto.text = "R$ ${lista_produtos.get(position).preco}"

        // Configura um listener para o clique no item da lista
        holder.itemView.setOnClickListener {
            // Cria uma nova Intent para iniciar a atividade DetalhesProduto
            val intent = Intent(context, DetalhesProduto::class.java)
            // Adiciona informações do produto à Intent como extras
            intent.putExtra("foto", lista_produtos.get(position).foto)
            intent.putExtra("nome", lista_produtos.get(position).nome)
            intent.putExtra("preco", lista_produtos.get(position).preco)
            // Inicia a atividade DetalhesProduto
            context.startActivity(intent)
        }
    }

    // Classe interna ProdutoViewHolder que estende RecyclerView.ViewHolder
    inner class ProdutoViewHolder(binding: ProdutoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Inicializa os componentes do layout do item de produto a partir do binding
        val fotoProduto = binding.imgProduto
        val nomeProduto = binding.nomeProduto
        val precoProduto = binding.precoProduto
    }
}
