// Pacote que contém as atividades de pagamento do aplicativo
package com.pitercapistrano.applojavirtualclient.activities.Pagamento;

// Importação das classes necessárias para a atividade
import android.content.Intent; // Para iniciar novas atividades
import android.graphics.Color; // Para manipulação de cores
import android.os.Build; // Para verificar a versão do Android
import android.os.Bundle; // Para gerenciar dados de instância da atividade
import android.util.Log; // Para registro de logs
import android.view.View; // Para manipulação de visualizações
import android.widget.TextView; // Para manipulação de TextViews
import android.widget.Toast; // Para exibir mensagens ao usuário

import androidx.activity.EdgeToEdge; // Para habilitar o modo de borda a borda
import androidx.appcompat.app.AppCompatActivity; // Para suportar funcionalidades da ActionBar
import androidx.core.graphics.Insets; // Para manipulação de insets de janela
import androidx.core.view.ViewCompat; // Para compatibilidade com visualizações
import androidx.core.view.WindowInsetsCompat; // Para lidar com insets de janela

import com.google.android.material.snackbar.Snackbar; // Para exibição de mensagens na parte inferior da tela
import com.google.firebase.auth.FirebaseAuth; // Para autenticação com Firebase
import com.google.gson.Gson; // Para manipulação de JSON
import com.google.gson.GsonBuilder; // Para construção de objetos Gson
import com.google.gson.JsonArray; // Para manipulação de arrays JSON
import com.google.gson.JsonObject; // Para manipulação de objetos JSON
import com.mercadopago.android.px.configuration.AdvancedConfiguration; // Configuração avançada do Mercado Pago
import com.mercadopago.android.px.core.MercadoPagoCheckout; // Classe para checkout do Mercado Pago
import com.mercadopago.android.px.model.Payment; // Modelo para pagamento
import com.mercadopago.android.px.model.exceptions.MercadoPagoError; // Exceção do Mercado Pago
import com.pitercapistrano.applojavirtualclient.Api.Api; // Classe para a API do aplicativo
import com.pitercapistrano.applojavirtualclient.R; // Para acesso a recursos
import com.pitercapistrano.applojavirtualclient.activities.Pedidos.Pedidos; // Atividade de pedidos
import com.pitercapistrano.applojavirtualclient.databinding.ActivityPagamentoBinding; // Para vinculação de dados da atividade
import com.pitercapistrano.applojavirtualclient.interfaceMercadoPago.ComunicacaoServidorMP; // Interface para comunicação com o servidor do Mercado Pago
import com.pitercapistrano.applojavirtualclient.model.DB; // Modelo para acesso ao banco de dados
import com.pitercapistrano.applojavirtualclient.model.Endereco; // Modelo para endereço

import retrofit2.Call; // Para chamadas de API
import retrofit2.Callback; // Para manipulação de callbacks de chamadas de API
import retrofit2.Response; // Para manipulação de respostas de API
import retrofit2.Retrofit; // Para configuração do Retrofit
import retrofit2.converter.gson.GsonConverterFactory; // Para conversão de JSON usando Gson
import retrofit2.converter.scalars.ScalarsConverterFactory; // Para conversão de tipos primitivos

// Classe que representa a tela de pagamento do aplicativo
public class Pagamento extends AppCompatActivity {

    ActivityPagamentoBinding binding; // Variável para vinculação da atividade
    private String tamanho_calcado; // Variável para armazenar o tamanho do calçado
    private String nome; // Variável para armazenar o nome do produto
    private String preco; // Variável para armazenar o preço do produto

    // Chaves de autenticação do Mercado Pago
    private final String PUBLIC_KEY = "APP_USR-bfee4cab-9068-4349-ac47-e14db465f74f";
    private final String ACCESS_TOKEN = "APP_USR-8878276529007772-100919-44a1753c7b684236a012c52510b43d67-2028646208";

    DB db = new DB(); // Instância do banco de dados

    // Método chamado na criação da atividade
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Chama o método da superclasse
        binding = ActivityPagamentoBinding.inflate(getLayoutInflater()); // Infla o layout da atividade e vincula à variável de binding
        EdgeToEdge.enable(this); // Habilita o modo de borda a borda para a atividade
        setContentView(binding.getRoot()); // Define a visualização da atividade

        // Define um listener para aplicar insets de janela na visualização principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // Obtém os insets das barras do sistema
            // Define o padding da visualização de acordo com os insets
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; // Retorna os insets
        });

        // Verifica se a versão do Android é Lollipop ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#000000")); // Define a cor da barra de status
            getWindow().getDecorView().dispatchSystemUiVisibilityChanged(0); // Notifica sobre mudanças na visibilidade da UI do sistema
        }

        // Obtém o tamanho do calçado passado como extra na Intent
        tamanho_calcado = getIntent().getExtras().getString("tamanho_calcado");
        // Obtém o nome do produto passado como extra na Intent
        nome = getIntent().getExtras().getString("nome");
        // Obtém o preço do produto passado como extra na Intent
        preco = getIntent().getExtras().getString("preco");

        // Registra no log o tamanho do calçado para fins de depuração
        Log.d("tamanho_calcado", tamanho_calcado);

        // Cria uma instância do Retrofit para realizar chamadas de API
        Retrofit retrofit = new Retrofit.Builder()
                // Adiciona o conversor Gson para converter objetos Java em JSON e vice-versa
                .addConverterFactory(GsonConverterFactory.create())
                // Define a URL base para as chamadas de API
                .baseUrl("https://viacep.com.br/")
                // Constrói a instância do Retrofit
                .build();

        // Cria uma instância da interface Api para realizar chamadas de API
        Api api = retrofit.create(Api.class);

        // Configura o listener para o botão de busca de CEP
        binding.btCep.setOnClickListener(v -> {
            // Obtém o valor do CEP inserido pelo usuário
            String cep = binding.cep.getText().toString();

            // Verifica se o campo de CEP está vazio
            if (cep.isEmpty()) {
                // Cria e exibe um Snackbar com uma mensagem de erro
                Snackbar snackbar = Snackbar.make(v, "Preencha o Cep!", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.RED); // Define a cor de fundo do Snackbar como vermelho
                snackbar.setTextColor(Color.WHITE); // Define a cor do texto do Snackbar como branco
                snackbar.show(); // Mostra o Snackbar
            } else {
                // Realiza a chamada à API para buscar o endereço correspondente ao CEP
                api.setEndereco(cep).enqueue(new Callback<Endereco>() {
                    @Override
                    public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                        // Verifica se a resposta da API foi bem-sucedida
                        if (response.code() == 200) {
                            // Extrai os dados do endereço da resposta
                            String logradouro = response.body() != null ? response.body().getLogradouro() : "";
                            String bairro = response.body() != null ? response.body().getBairro() : "";
                            String localidade = response.body() != null ? response.body().getLocalidade() : "";
                            String uf = response.body() != null ? response.body().getUf() : "";

                            // Chama um método para preencher os formulários com os dados do endereço
                            setFormularios(logradouro, bairro, localidade, uf);
                        } else {
                            // Exibe um Toast informando que o CEP é inválido
                            Toast.makeText(getApplicationContext(), "Cep inválido!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Endereco> call, Throwable t) {
                        // Exibe um Toast informando sobre um erro inesperado
                        Toast.makeText(getApplicationContext(), "Erro inesperado!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Configura o listener para o botão de pagamento
        binding.btPagamento.setOnClickListener(v -> {
            // Obtém os valores inseridos nos campos de endereço
            String cep = binding.cep.getText().toString();
            String rua = binding.rua.getText().toString();
            String numero = binding.numero.getText().toString();
            String bairro = binding.bairro.getText().toString();
            String cidade = binding.cidade.getText().toString();
            String estado = binding.estado.getText().toString();

            // Verifica se algum dos campos obrigatórios está vazio
            if (cep.isEmpty() || rua.isEmpty() || numero.isEmpty() || bairro.isEmpty() || cidade.isEmpty() || estado.isEmpty()) {
                // Cria e exibe um Snackbar com uma mensagem de erro
                Snackbar snackbar = Snackbar.make(v, "Preencha os campos obrigatórios!", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.RED); // Define a cor de fundo do Snackbar como vermelho
                snackbar.setTextColor(Color.WHITE); // Define a cor do texto do Snackbar como branco
                snackbar.show(); // Mostra o Snackbar
            } else {
                // Chama um método para criar um objeto JSON com os dados do endereço
                criarJsonObject();
            }
        });

    }

    // Método para definir os valores dos formulários de endereço
    private void setFormularios(String logradouro, String bairro, String localidade, String uf) {
        // Define o texto do campo "rua" com o valor de 'logradouro'
        binding.rua.setText(logradouro);
        // Define o texto do campo "bairro" com o valor de 'bairro'
        binding.bairro.setText(bairro);
        // Define o texto do campo "cidade" com o valor de 'localidade'
        binding.cidade.setText(localidade);
        // Define o texto do campo "estado" com o valor de 'uf'
        binding.estado.setText(uf);
    }

    // Método para criar um objeto JSON com os dados do usuário
    private void criarJsonObject() {
        // Obtém o e-mail do usuário autenticado
        String emailUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        // Obtém o DDD do campo de entrada
        String ddd = binding.ddd.getText().toString();
        // Obtém o número de telefone do campo de entrada
        String telefone = binding.telefone.getText().toString();
        // Obtém o CEP do campo de entrada
        String cep = binding.cep.getText().toString();
        // Obtém o nome da rua do campo de entrada
        String rua = binding.rua.getText().toString();
        // Obtém o número da casa/apartamento do campo de entrada
        String numero = binding.numero.getText().toString();

        // Cria um novo objeto JSON para armazenar os dados
        JsonObject dados = new JsonObject();

        // Cria um array JSON para armazenar os itens da lista
        JsonArray item_lista = new JsonArray();
        JsonObject item;

        // Cria um objeto JSON para armazenar informações pessoais
        JsonObject informacoes_pessoais = new JsonObject();

        // Cria um objeto JSON para armazenar informações de telefone
        JsonObject telefoneObj = new JsonObject();
        // Adiciona o DDD ao objeto de telefone
        telefoneObj.addProperty("area_code", ddd);
        // Adiciona o número de telefone ao objeto de telefone
        telefoneObj.addProperty("number", telefone);

        // Cria um objeto JSON para armazenar o endereço
        JsonObject endereco = new JsonObject();
        // Adiciona o CEP ao objeto de endereço
        endereco.addProperty("zip_code", cep);
        // Adiciona o nome da rua ao objeto de endereço
        endereco.addProperty("street_name", rua);
        // Adiciona o número da casa ao objeto de endereço, convertendo para inteiro
        endereco.addProperty("street_number", Integer.parseInt(numero));

        // Cria um novo item JSON para adicionar à lista de itens
        item = new JsonObject();
        // Adiciona o nome do item ao objeto
        item.addProperty("title", nome);
        // Define a quantidade do item como 1
        item.addProperty("quantity", 1);
        // Define a moeda como BRL (Real Brasileiro)
        item.addProperty("currency_id", "BRL");
        // Define o preço unitário do item, convertendo para double
        item.addProperty("unit_price", Double.parseDouble(preco));
        // Adiciona o item à lista de itens
        item_lista.add(item);

        // Adiciona a lista de itens ao objeto de dados
        dados.add("items", item_lista);

        // Adiciona o e-mail ao objeto de informações pessoais
        informacoes_pessoais.addProperty("email", emailUsuario);
        // Adiciona o objeto de telefone às informações pessoais
        informacoes_pessoais.add("phone", telefoneObj);
        // Adiciona o objeto de endereço às informações pessoais
        informacoes_pessoais.add("adddress", endereco);

        // Adiciona as informações pessoais ao objeto de dados
        dados.add("payer", informacoes_pessoais);

        // (Comentado) Código para excluir formas de pagamento, como boleto
    /*
    JsonObject excluir_pagamento = new JsonObject();
    JsonArray ids = new JsonArray();
    JsonObject removerBoleto = new JsonObject();

    // Adiciona o tipo de pagamento "ticket" para remoção
    removerBoleto.addProperty("id", "ticket");
    ids.add(removerBoleto);
    excluir_pagamento.add("excluded_payment_types", ids);
    excluir_pagamento.addProperty("isntallments", 2);

    // Adiciona as formas de pagamento ao objeto de dados
    dados.add("payment_methods", excluir_pagamento);
    */

        // Log dos dados em formato JSON
        Log.d("itens", dados.toString());
        // Chama o método para criar preferências de pagamento passando os dados
        criarPreferenciaPagamento(dados);
    }

    // Método para criar uma preferência de pagamento usando a API do Mercado Pago
    private void criarPreferenciaPagamento(JsonObject dados) {
        // URL base da API do Mercado Pago
        String site = "https://api.mercadopago.com";
        // Endpoint para criar preferências de checkout, incluindo o token de acesso
        String url = "/checkout/preferences?access_token=" + ACCESS_TOKEN;

        // Cria uma instância do Gson com configurações permissivas
        Gson gson = new GsonBuilder().setLenient().create();

        // Cria uma instância do Retrofit com a URL base e os conversores necessários
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(site)
                .addConverterFactory(ScalarsConverterFactory.create()) // Conversor para strings
                .addConverterFactory(GsonConverterFactory.create(gson)) // Conversor para JSON
                .build();

        // Cria uma interface para comunicação com a API do Mercado Pago
        ComunicacaoServidorMP conexao_pagamento = retrofit.create(ComunicacaoServidorMP.class);
        // Cria a chamada para enviar os dados de pagamento
        Call<JsonObject> request = conexao_pagamento.enviarPagamento(url, dados);
        // Envia a requisição de forma assíncrona
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Obtém o ID da preferência de pagamento da resposta
                String preferenceId = response.body().get("id").getAsString();
                // Chama o método para iniciar o pagamento com o ID da preferência
                criarPagamento(preferenceId);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                // Tratamento de falhas na requisição (não implementado aqui)
            }
        });
    }

    // Método para iniciar o pagamento com o ID da preferência
    private void criarPagamento(String preferenceId) {
        // Configuração avançada para desativar ofertas de bancos
        final AdvancedConfiguration advancedConfiguration =
                new AdvancedConfiguration.Builder().setBankDealsEnabled(false).build();
        // Cria uma instância do checkout do Mercado Pago e inicia o pagamento
        new MercadoPagoCheckout
                .Builder(PUBLIC_KEY, preferenceId) // Passa a chave pública e ID da preferência
                .setAdvancedConfiguration(advancedConfiguration) // Aplica as configurações avançadas
                .build()
                .startPayment(this, 123); // Inicia o pagamento e define um código de requisição
    }

    // Método que recebe o resultado da atividade após o pagamento
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Chama o método da superclasse

        // Verifica se o requestCode é o esperado (123)
        if (requestCode == 123) {
            // Verifica se o pagamento foi concluído com sucesso
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
                // Obtém o objeto Payment da resposta
                final Payment pagamento = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);

                // Se o pagamento não for nulo, processa a resposta
                if (pagamento != null) {
                    respostaMercadoPago(pagamento);
                } else {
                    // Exibe mensagem de falha ao obter o resultado do pagamento
                    Toast.makeText(this, "Falha ao obter o resultado do pagamento.", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Se o pagamento foi cancelado, exibe a mensagem correspondente
                if (data != null && data.hasExtra(MercadoPagoCheckout.EXTRA_ERROR)) {
                    // Obtém e exibe o erro de pagamento, se existir
                    final MercadoPagoError mercadoPagoError = (MercadoPagoError) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR);
                    exibirMensagemErro(mercadoPagoError);
                } else {
                    // Mensagem se o usuário cancelou a operação manualmente
                    Toast.makeText(this, "Pagamento cancelado pelo usuário.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Mensagem para erro inesperado ao processar o pagamento
                Toast.makeText(this, "Erro inesperado ao processar o pagamento.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para processar a resposta do pagamento
    private void respostaMercadoPago(Payment pagamento) {
        // Obtém o status do pagamento
        String status = pagamento.getPaymentStatus();

        // Obtém dados do endereço e telefone do formulário
        String cep = binding.cep.getText().toString();
        String rua = binding.rua.getText().toString();
        String numero = binding.numero.getText().toString();
        String bairro = binding.bairro.getText().toString();
        String cidade = binding.cidade.getText().toString();
        String estado = binding.estado.getText().toString();
        String complemento = binding.complemento.getText().toString();
        String ddd = binding.ddd.getText().toString();
        String telefone = binding.telefone.getText().toString();

        // Formata o endereço e telefone
        String endereco = "Cep: " + cep + "\nRua: " + rua + ", " + numero + "\nBairro: " + bairro + "\nCidade: " + cidade + " / " + estado + "\nComplemento: " + complemento;
        String telefoneComDDD = "Telefone: " + "(" + ddd + ")" + " " + telefone;
        String status_entrega = "Status de Entrega: " + " " + "Em andamento";

        // Formata informações do produto
        String nomeProduto = "Nome: " + nome;
        String tamanho = "Tamanho: " + tamanho_calcado;
        String precoProduto = "Preço: R$ " + preco;

        // Exibe mensagens com base no status do pagamento
        if (status.equalsIgnoreCase("approved")) {
            // Mensagem de sucesso para pagamento aprovado
            String status_pagamento = "Status de Pagamento: " + "Pagamento Aprovado!";
            Snackbar snackbar = Snackbar.make(binding.main, "Pagamento efetuado com sucesso!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.parseColor("#4E9100"));
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();

            // Salva os dados do pedido no banco de dados
            db.salvarDadosPedidosUsuario(endereco, telefoneComDDD, nomeProduto, precoProduto, tamanho, status_pagamento, status_entrega);

            // Aguardar 2 segundos antes de ir para a tela de pedidos
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    irParaTelaPedidos();
                }
            }, 2000); // 2000 ms = 2 segundos

        } else if (status.equalsIgnoreCase("rejected")) {
            // Mensagem de erro para pagamento rejeitado
            Snackbar snackbar = Snackbar.make(binding.main, "Erro ao fazer o pagamento!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.RED);
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();
        } else if (status.equalsIgnoreCase("in_process")) {
            // Mensagem para pagamento em processamento
            String status_pagamento = "Status de Pagamento: " + "Pagamento em Processamento!";
            Snackbar snackbar = Snackbar.make(binding.main, "Pagamento em processamento!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.YELLOW);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();

            // Atualiza status de entrega para aguardando pagamento
            status_entrega = "Status de Entrega: " + "Aguardando Pagamento!";
            db.salvarDadosPedidosUsuario(endereco, telefoneComDDD, nomeProduto, precoProduto, tamanho, status_pagamento, status_entrega);

            // Aguardar 2 segundos antes de ir para a tela de pedidos
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    irParaTelaPedidos();
                }
            }, 2000); // 2000 ms = 2 segundos
        } else {
            // Mensagem para erro inesperado
            Snackbar snackbar = Snackbar.make(binding.main, "Erro inesperado!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.RED);
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    // Método para exibir mensagens de erro ao processar o pagamento
    private void exibirMensagemErro(MercadoPagoError error) {
        // Verifica se o erro é de conectividade
        if (error.isNoConnectivityError()) {
            Toast.makeText(this, "Erro de rede ao processar o pagamento.", Toast.LENGTH_SHORT).show();
        } else {
            // Exibe a mensagem de erro detalhada
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Método para navegar para a tela de pedidos
    private void irParaTelaPedidos() {
        // Cria uma nova Intent para abrir a tela de pedidos
        Intent intent = new Intent(this, Pedidos.class);
        startActivity(intent); // Inicia a nova atividade
    }
}