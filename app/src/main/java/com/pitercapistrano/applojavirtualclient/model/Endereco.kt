package com.pitercapistrano.applojavirtualclient.model

// Define uma classe de dados chamada Endereco que representa as informações de um endereço retornado pela API
class Endereco (
    // Atributo logradouro (nome da rua ou avenida), pode ser nulo
    val logradouro: String? = null,

    // Atributo bairro (nome do bairro), pode ser nulo
    val bairro: String? = null,

    // Atributo localidade (nome da cidade), pode ser nulo
    val localidade: String? = null,

    // Atributo uf (Unidade Federativa, estado), pode ser nulo
    val uf: String? = null
)