// Pacote da aplicação que contém a interface para comunicação com o servidor Mercado Pago
package com.pitercapistrano.applojavirtualclient.interfaceMercadoPago;

// Importa a classe JsonObject do pacote Gson para manipulação de objetos JSON
import com.google.gson.JsonObject;

// Importa as classes necessárias do Retrofit para chamadas HTTP
import retrofit2.Call; // Para representar uma chamada HTTP
import retrofit2.http.Body; // Para indicar que um parâmetro é o corpo da requisição
import retrofit2.http.Headers; // Para adicionar cabeçalhos à requisição
import retrofit2.http.POST; // Para definir o método HTTP como POST
import retrofit2.http.Url; // Para permitir a definição de URLs dinâmicas

// Interface ComunicacaoServidorMP que define a comunicação com o servidor do Mercado Pago
public interface ComunicacaoServidorMP {

    // Adiciona cabeçalhos à requisição, neste caso, especifica o tipo de conteúdo como JSON
    @Headers({
            "Content-Type: application/json" // Define que o conteúdo da requisição é do tipo JSON
    })

    // Define o método HTTP como POST. A URL será passada dinamicamente.
    @POST()
    Call<JsonObject> enviarPagamento(
            @Url String url, // Parâmetro que receberá a URL para a requisição
            @Body JsonObject dados); // Parâmetro que representa o corpo da requisição contendo os dados do pagamento
}
