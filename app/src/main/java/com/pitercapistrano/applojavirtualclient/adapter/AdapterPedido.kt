package com.pitercapistrano.applojavirtualclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pitercapistrano.applojavirtualclient.databinding.ActivityPedidosBinding
import com.pitercapistrano.applojavirtualclient.databinding.PedidoItemBinding
import com.pitercapistrano.applojavirtualclient.model.Pedido

class AdapterPedido(val context: Context, val lista_pedidos: MutableList<Pedido>):
    RecyclerView.Adapter<AdapterPedido.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val item_lista = PedidoItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return PedidoViewHolder(item_lista)
    }

    override fun getItemCount() = lista_pedidos.size


    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.txtEndereco.text = lista_pedidos.get(position).endereco
        holder.txtTelefone.text = lista_pedidos.get(position).telefone
        holder.txtNomeProduto.text = lista_pedidos.get(position).produto
        holder.txtTamanho.text = lista_pedidos.get(position).tamanho_calcado
        holder.txtPrecoProduto.text = lista_pedidos.get(position).preco
        holder.txtStatusPagamento.text = lista_pedidos.get(position).status_pagamento
        holder.txtStatusEntrega.text = lista_pedidos.get(position).status_entrega
    }

    inner class PedidoViewHolder(binding: PedidoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val txtEndereco = binding.txtEndereco
        val txtTelefone = binding.txtTelefone
        val txtNomeProduto = binding.txtNomeProduto
        val txtTamanho = binding.txtTamanho
        val txtPrecoProduto = binding.txtPrecoProduto
        val txtStatusPagamento = binding.txtStatusPagamento
        val txtStatusEntrega = binding.txtStatusEntrega
    }
}