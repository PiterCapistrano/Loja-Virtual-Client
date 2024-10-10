package com.pitercapistrano.applojavirtualclient.model

import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pitercapistrano.applojavirtualclient.adapter.AdapterProduto
import java.util.UUID

class DB {

    fun salvarDadosUsuario(nome: String, email: String){

        val usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()

        val usuarios = hashMapOf(
            "nome" to nome,
            "email" to email
        )

        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        documentReference.set(usuarios).addOnSuccessListener {
            Log.d("DB", "Sucesso ao salvar os dados")
        }.addOnFailureListener {
            Log.d("DB_ERROR", "Erro ao salvar os dados! ${it.printStackTrace()}")
        }
    }

    fun recuperarDadosPerfil(nomePerfil: TextView, emailPerfil: TextView){
        val usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        val email = FirebaseAuth.getInstance().currentUser!!.email
        val db = FirebaseFirestore.getInstance()

        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        documentReference.addSnapshotListener { documento, error ->
            if (documento != null){
                nomePerfil.text = documento.getString("nome")
                emailPerfil.text = email
            }
        }
    }

    fun  obterListaProdutos(lista_produtos: MutableList<Produto>, adapterProduto: AdapterProduto){
        val db = FirebaseFirestore.getInstance()
        db.collection("Produtos").get()
            .addOnCompleteListener { tarefa ->
                if (tarefa.isSuccessful) {
                    for (documento in tarefa.result!!){
                        val produto = documento.toObject(Produto::class.java)
                        lista_produtos.add(produto)
                        adapterProduto.notifyDataSetChanged()
                    }
                }
            }
    }

    fun salvarDadosPedidosUsuario(
        endereco: String,
        telefone: String,
        produto: String,
        preco: String,
        tamanho_calcado: String,
        status_pagamento: String,
        status_entrega: String
    ){

        var db = FirebaseFirestore.getInstance()
        var usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        var pedidoID = UUID.randomUUID().toString()

        val pedidos = hashMapOf(
            "endereco" to endereco,
            "telefone" to telefone,
            "produto" to produto,
            "preco" to preco,
            "tamanho_calcado" to tamanho_calcado,
            "status_pagamento" to status_pagamento,
            "status_entrega" to status_entrega,
            "data_pedido" to System.currentTimeMillis()
        )

        val documentreference = db.collection("Usuario_Pedidos").document(usuarioID)
            .collection("Pedidos").document(pedidoID)

        documentreference.set(pedidos).addOnSuccessListener {
            Log.d("db_pedidos", "Sucesso ao salvar os pedidos!")
        }.addOnFailureListener { e ->
            Log.e("db_pedidos", "Erro ao salvar os pedidos", e)
        }
    }
}