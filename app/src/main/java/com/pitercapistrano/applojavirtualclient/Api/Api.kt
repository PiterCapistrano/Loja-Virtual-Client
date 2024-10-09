package com.pitercapistrano.applojavirtualclient.Api

// Importa a classe Endereco, que representa a estrutura dos dados de resposta da API
import com.pitercapistrano.applojavirtualclient.model.Endereco

// Importa as classes necessárias para fazer chamadas à API usando Retrofit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// Define a interface Api, que contém as funções para interagir com a API do ViaCEP
interface Api {

    // Função que faz uma requisição GET à API do ViaCEP.
    // O endpoint contém um caminho variável {cep}, que será substituído pelo valor passado
    // A função retorna um Call de Endereco, que é um wrapper para a resposta da API
    @GET("ws/{cep}/json/")
    fun setEndereco(@Path("cep") cep: String): Call<Endereco>

}