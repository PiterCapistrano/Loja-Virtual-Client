// Pacote da aplicação que contém a classe para interagir com o banco de dados
package com.pitercapistrano.applojavirtualclient.model

// Importa a classe Log para registrar mensagens de log
import android.util.Log
// Importa a classe TextView para manipular textos em Views
import android.widget.TextView
// Importa a classe FirebaseAuth para autenticação de usuários
import com.google.firebase.auth.FirebaseAuth
// Importa classes do Firestore para manipulação de documentos
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
// Importa os adaptadores usados para exibir pedidos e produtos
import com.pitercapistrano.applojavirtualclient.adapter.AdapterPedido
import com.pitercapistrano.applojavirtualclient.adapter.AdapterProduto
// Importa a classe UUID para gerar identificadores únicos
import java.util.UUID

// Classe DB que encapsula as operações de banco de dados
class DB {

    // Função para salvar os dados do usuário no Firestore
    fun salvarDadosUsuario(nome: String, email: String){
        // Obtém o ID do usuário autenticado
        val usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        // Obtém uma instância do Firestore
        val db = FirebaseFirestore.getInstance()

        // Cria um mapa com os dados do usuário a serem salvos
        val usuarios = hashMapOf(
            "nome" to nome,
            "email" to email
        )

        // Define a referência ao documento do usuário no Firestore
        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        // Salva os dados do usuário e configura os listeners para sucesso e falha
        documentReference.set(usuarios).addOnSuccessListener {
            Log.d("DB", "Sucesso ao salvar os dados") // Loga sucesso
        }.addOnFailureListener {
            Log.d("DB_ERROR", "Erro ao salvar os dados! ${it.printStackTrace()}") // Loga erro
        }
    }

    // Função para recuperar os dados do perfil do usuário
    fun recuperarDadosPerfil(nomePerfil: TextView, emailPerfil: TextView){
        // Obtém o ID e email do usuário autenticado
        val usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        val email = FirebaseAuth.getInstance().currentUser!!.email
        // Obtém uma instância do Firestore
        val db = FirebaseFirestore.getInstance()

        // Define a referência ao documento do usuário no Firestore
        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        // Adiciona um listener para ouvir mudanças no documento
        documentReference.addSnapshotListener { documento, error ->
            // Verifica se o documento existe
            if (documento != null){
                // Atualiza os TextViews com os dados recuperados
                nomePerfil.text = documento.getString("nome")
                emailPerfil.text = email
            }
        }
    }

    // Função para obter a lista de produtos do Firestore
    fun  obterListaProdutos(lista_produtos: MutableList<Produto>, adapterProduto: AdapterProduto){
        // Obtém uma instância do Firestore
        val db = FirebaseFirestore.getInstance()
        // Realiza uma consulta à coleção "Produtos"
        db.collection("Produtos").get()
            .addOnCompleteListener { tarefa -> // Adiciona um listener para completar a tarefa
                if (tarefa.isSuccessful) { // Verifica se a tarefa foi bem-sucedida
                    for (documento in tarefa.result!!){ // Itera pelos documentos retornados
                        // Converte o documento para um objeto Produto e adiciona à lista
                        val produto = documento.toObject(Produto::class.java)
                        lista_produtos.add(produto)
                        // Notifica o adaptador sobre a mudança na lista
                        adapterProduto.notifyDataSetChanged()
                    }
                }
            }
    }

    // Função para salvar os dados dos pedidos do usuário
    fun salvarDadosPedidosUsuario(
        endereco: String,
        telefone: String,
        produto: String,
        preco: String,
        tamanho_calcado: String,
        status_pagamento: String,
        status_entrega: String
    ){
        // Obtém uma instância do Firestore
        var db = FirebaseFirestore.getInstance()
        // Obtém o ID do usuário autenticado
        var usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        // Gera um ID único para o pedido
        var pedidoID = UUID.randomUUID().toString()

        // Cria um mapa com os dados do pedido a serem salvos
        val pedidos = hashMapOf(
            "endereco" to endereco,
            "telefone" to telefone,
            "produto" to produto,
            "preco" to preco,
            "tamanho_calcado" to tamanho_calcado,
            "status_pagamento" to status_pagamento,
            "status_entrega" to status_entrega,
            "data_pedido" to System.currentTimeMillis() // Adiciona a data do pedido
        )

        // Define a referência ao documento do pedido no Firestore
        val documentreference = db.collection("Usuario_Pedidos").document(usuarioID)
            .collection("Pedidos").document(pedidoID)

        // Salva os dados do pedido e configura os listeners para sucesso e falha
        documentreference.set(pedidos).addOnSuccessListener {
            Log.d("db_pedidos", "Sucesso ao salvar os pedidos!") // Loga sucesso
        }.addOnFailureListener { e ->
            Log.e("db_pedidos", "Erro ao salvar os pedidos", e) // Loga erro
        }
    }

    // Função para obter a lista de pedidos do usuário
    fun obterListaPedidos(lista_pedidos: MutableList<Pedido>, adapter_pedido: AdapterPedido){
        // Obtém uma instância do Firestore
        var db = FirebaseFirestore.getInstance()
        // Obtém o ID do usuário autenticado
        var usuarioID = FirebaseAuth.getInstance().currentUser!!.uid

        // Realiza uma consulta à coleção de pedidos do usuário
        db.collection("Usuario_Pedidos").document(usuarioID).collection("Pedidos")
            .get().addOnCompleteListener { tarefa -> // Adiciona um listener para completar a tarefa
                if (tarefa.isSuccessful){ // Verifica se a tarefa foi bem-sucedida
                    for (documento in tarefa.result!!){ // Itera pelos documentos retornados
                        // Converte o documento para um objeto Pedido e adiciona à lista
                        val pedidos = documento.toObject(Pedido::class.java)
                        lista_pedidos.add(pedidos)
                        // Notifica o adaptador sobre a mudança na lista
                        adapter_pedido.notifyDataSetChanged()
                    }
                }
            }
    }
}
