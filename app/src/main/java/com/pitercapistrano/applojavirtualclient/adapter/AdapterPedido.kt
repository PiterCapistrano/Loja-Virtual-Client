// Pacote da aplicação que contém o adaptador de pedidos
package com.pitercapistrano.applojavirtualclient.adapter

// Importações necessárias
import android.content.Context // Para manipular o contexto da aplicação
import android.graphics.Color
import android.view.LayoutInflater // Para inflar layouts
import android.view.View // Para manipulação de visualizações
import android.view.ViewGroup // Para representar um grupo de visualizações
import androidx.recyclerview.widget.RecyclerView // Para utilizar RecyclerView
import com.pitercapistrano.applojavirtualclient.databinding.ActivityPedidosBinding // Para o binding da atividade de pedidos
import com.pitercapistrano.applojavirtualclient.databinding.PedidoItemBinding // Para o binding dos itens de pedido
import com.pitercapistrano.applojavirtualclient.model.Pedido // Para utilizar a classe modelo Pedido

// Classe AdapterPedido que estende RecyclerView.Adapter com um ViewHolder do tipo PedidoViewHolder
class AdapterPedido(val context: Context, val lista_pedidos: MutableList<Pedido>) :
    RecyclerView.Adapter<AdapterPedido.PedidoViewHolder>() {

    // Método chamado para criar um novo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        // Infla o layout do item de pedido usando o binding
        val item_lista = PedidoItemBinding.inflate(LayoutInflater.from(context), parent, false)
        // Retorna uma nova instância do ViewHolder com o layout inflado
        return PedidoViewHolder(item_lista)
    }

    // Método que retorna a quantidade de itens na lista de pedidos
    override fun getItemCount() = lista_pedidos.size

    // Método que vincula os dados do pedido ao ViewHolder correspondente
    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        // Define o endereço do pedido no TextView correspondente
        holder.txtEndereco.text = lista_pedidos.get(position).endereco
        // Define o telefone do pedido no TextView correspondente
        holder.txtTelefone.text = lista_pedidos.get(position).telefone
        // Define o nome do produto no TextView correspondente
        holder.txtNomeProduto.text = lista_pedidos.get(position).produto
        // Define o tamanho do calçado no TextView correspondente
        holder.txtTamanho.text = lista_pedidos.get(position).tamanho_calcado
        // Define o preço do produto no TextView correspondente
        holder.txtPrecoProduto.text = lista_pedidos.get(position).preco
        // Define o status do pagamento no TextView correspondente
        holder.txtStatusPagamento.text = lista_pedidos.get(position).status_pagamento
        // Define o status de entrega no TextView correspondente
        holder.txtStatusEntrega.text = lista_pedidos.get(position).status_entrega

        if (holder.txtStatusPagamento.text.equals("Status de Pagamento: Pagamento Estornado!")){
            holder.txtStatusPagamento.setTextColor(Color.RED)

        } else if (holder.txtStatusPagamento.text.equals("Status de Pagamento: Pagamento Cancelado!")){
            holder.txtStatusPagamento.setTextColor(Color.RED)
        }else{
            holder.txtStatusPagamento.setTextColor(Color.parseColor("#007A05"))
        }

        if (holder.txtStatusEntrega.text.equals("Status de Entrega: Em Separação!")){
            holder.txtStatusEntrega.setTextColor(Color.parseColor("#FF9800"))
        } else if (holder.txtStatusEntrega.text.equals("Status de Entrega: Em Trânsito!")){
            holder.txtStatusEntrega.setTextColor(Color.parseColor("#007A05"))
        } else if (holder.txtStatusEntrega.text.equals("Status de Entrega: Entregue!")){
            holder.txtStatusEntrega.setTextColor(Color.BLUE)
        }
    }

    // Classe interna PedidoViewHolder que estende RecyclerView.ViewHolder
    inner class PedidoViewHolder(binding: PedidoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Inicializa os TextViews a partir do binding do item de pedido
        val txtEndereco = binding.txtEndereco
        val txtTelefone = binding.txtTelefone
        val txtNomeProduto = binding.txtNomeProduto
        val txtTamanho = binding.txtTamanho
        val txtPrecoProduto = binding.txtPrecoProduto
        val txtStatusPagamento = binding.txtStatusPagamento
        val txtStatusEntrega = binding.txtStatusEntrega
    }
}
