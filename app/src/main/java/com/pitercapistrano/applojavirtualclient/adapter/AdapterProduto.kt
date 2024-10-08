package com.pitercapistrano.applojavirtualclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pitercapistrano.applojavirtualclient.databinding.ProdutoItemBinding
import com.pitercapistrano.applojavirtualclient.model.Produto

class AdapterProduto(val context: Context, val lista_produtos: MutableList<Produto>):
    RecyclerView.Adapter<AdapterProduto.ProdutoViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val item_lista = ProdutoItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProdutoViewHolder(item_lista)
    }

    override fun getItemCount() = lista_produtos.size

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        lista_produtos.get(position).foto?.let { holder.fotoProduto.setImageResource(it) }
        holder.nomeProduto.text = lista_produtos.get(position).nome
        holder.precoProduto.text = lista_produtos.get(position).preco
    }

    inner class ProdutoViewHolder(binding: ProdutoItemBinding) : RecyclerView.ViewHolder(binding.root){
        val fotoProduto = binding.imgProduto
        val nomeProduto = binding.nomeProduto
        val precoProduto = binding.precoProduto
    }
}