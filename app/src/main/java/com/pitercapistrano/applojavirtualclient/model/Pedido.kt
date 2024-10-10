package com.pitercapistrano.applojavirtualclient.model

data class Pedido (
    val endereco: String? = null,
    val telefone: String? = null,
    val nomeProduto: String? = null,
    val tamanhoProduto: String? = null,
    val precoProduto: String? = null,
    val statusPagamento: String? = null,
    val statusEntrega: String? = null
)