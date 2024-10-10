package com.pitercapistrano.applojavirtualclient.activities.Pagamento;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.pitercapistrano.applojavirtualclient.Api.Api;
import com.pitercapistrano.applojavirtualclient.R;
import com.pitercapistrano.applojavirtualclient.activities.Pedidos.Pedidos;
import com.pitercapistrano.applojavirtualclient.databinding.ActivityPagamentoBinding;
import com.pitercapistrano.applojavirtualclient.interfaceMercadoPago.ComunicacaoServidorMP;
import com.pitercapistrano.applojavirtualclient.model.DB;
import com.pitercapistrano.applojavirtualclient.model.Endereco;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Pagamento extends AppCompatActivity {

    ActivityPagamentoBinding binding;
    private String tamanho_calcado;
    private String nome;
    private String preco;

    private final String PUBLIC_KEY = "APP_USR-bfee4cab-9068-4349-ac47-e14db465f74f";
    private final String ACCESS_TOKEN = "APP_USR-8878276529007772-100919-44a1753c7b684236a012c52510b43d67-2028646208";

    DB db = new DB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPagamentoBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tamanho_calcado = getIntent().getExtras().getString("tamanho_calcado");
        nome = getIntent().getExtras().getString("nome");
        preco = getIntent().getExtras().getString("preco");

        Log.d("tamanho_calcado", tamanho_calcado);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://viacep.com.br/")
                .build();

        Api api = retrofit.create(Api.class);

        binding.btCep.setOnClickListener(v -> {
            String cep = binding.cep.getText().toString();

            if (cep.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v, "Preencha o Cep!", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.RED);
                snackbar.setTextColor(Color.WHITE);
                snackbar.show();
            } else {
                api.setEndereco(cep).enqueue(new Callback<Endereco>() {
                    @Override
                    public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                        if (response.code() == 200) {
                            String logradouro = response.body() != null ? response.body().getLogradouro() : "";
                            String bairro = response.body() != null ? response.body().getBairro() : "";
                            String localidade = response.body() != null ? response.body().getLocalidade() : "";
                            String uf = response.body() != null ? response.body().getUf() : "";

                            setFormularios(logradouro, bairro, localidade, uf);
                        } else {
                            Toast.makeText(getApplicationContext(), "Cep inválido!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Endereco> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Erro inesperado!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.btPagamento.setOnClickListener(v -> {
            String cep = binding.cep.getText().toString();
            String rua = binding.rua.getText().toString();
            String numero = binding.numero.getText().toString();
            String bairro = binding.bairro.getText().toString();
            String cidade = binding.cidade.getText().toString();
            String estado = binding.estado.getText().toString();

            if (cep.isEmpty() || rua.isEmpty() || numero.isEmpty() || bairro.isEmpty() || cidade.isEmpty() || estado.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v, "Preencha os campos obrigatórios!", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.RED);
                snackbar.setTextColor(Color.WHITE);
                snackbar.show();
            } else {
                criarJsonObject();
            }
        });
    }

    private void setFormularios(String logradouro, String bairro, String localidade, String uf) {
        binding.rua.setText(logradouro);
        binding.bairro.setText(bairro);
        binding.cidade.setText(localidade);
        binding.estado.setText(uf);
    }

    private void criarJsonObject() {

        //String emailUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String emailUsuario = "test_user_1225665122@testuser.com";
        String ddd = binding.ddd.getText().toString();
        String telefone = binding.telefone.getText().toString();
        String cep = binding.cep.getText().toString();
        String rua = binding.rua.getText().toString();
        String numero = binding.numero.getText().toString();

        JsonObject dados = new JsonObject();

        // Primeiro item
        JsonArray item_lista = new JsonArray();
        JsonObject item;

        // Segundo item
        JsonObject informaceos_pessoais = new JsonObject();

        // Criando o objeto de telefone
        JsonObject telefoneObj = new JsonObject();
        telefoneObj.addProperty("area_code", ddd);
        telefoneObj.addProperty("number", telefone);

        // Criando o objeto de endereço
        JsonObject endereco = new JsonObject();
        endereco.addProperty("zip_code", cep);
        endereco.addProperty("street_name", rua);
        endereco.addProperty("street_number", Integer.parseInt(numero));
        item = new JsonObject();
        item.addProperty("title", nome);
        item.addProperty("quantity", 1);
        item.addProperty("currency_id", "BRL");
        item.addProperty("unit_price", Double.parseDouble(preco));
        item_lista.add(item);

        dados.add("items", item_lista);

        informaceos_pessoais.addProperty("email", emailUsuario);
        informaceos_pessoais.add("phone", telefoneObj);
        informaceos_pessoais.add("adddress", endereco);

        dados.add("payer", informaceos_pessoais);

        // Terceiro item - Excluir formas de pagamento - nesse caso vai ser o boleto
        /*JsonObject excluir_pagamento = new JsonObject();
        JsonArray ids = new JsonArray();
        JsonObject removerBoleto = new JsonObject();

        removerBoleto.addProperty("id", "ticket");
        ids.add(removerBoleto);
        excluir_pagamento.add("excluded_payment_types", ids);
        excluir_pagamento.addProperty("isntallments", 2);

        dados.add("payment_methods", excluir_pagamento);*/

        Log.d("itens", dados.toString());
        criarPreferenciaPagamento(dados);
    }

    private void criarPreferenciaPagamento(JsonObject dados) {
        String site = "https://api.mercadopago.com";
        String url = "/checkout/preferences?access_token=" + ACCESS_TOKEN;

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(site)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ComunicacaoServidorMP conexao_pagamento = retrofit.create(ComunicacaoServidorMP.class);
        Call<JsonObject> request = conexao_pagamento.enviarPagamento(url, dados);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String preferenceId = response.body().get("id").getAsString();
                criarPagamento(preferenceId);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

            }
        });
    }

    private void criarPagamento(String preferenceId) {
        final AdvancedConfiguration advancedConfiguration =
                new AdvancedConfiguration.Builder().setBankDealsEnabled(false).build();
        new MercadoPagoCheckout
                .Builder(PUBLIC_KEY, preferenceId)
                .setAdvancedConfiguration(advancedConfiguration).build()
                .startPayment(this, 123);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica se o requestCode é o esperado
        if (requestCode == 123) {

            // Verifica se o pagamento foi concluído
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {

                // Obtém o objeto Payment da resposta
                final Payment pagamento = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);

                // Se o pagamento não for nulo, chama a função para mostrar a mensagem
                if (pagamento != null) {
                    respostaMercadoPago(pagamento);
                } else {
                    Toast.makeText(this, "Falha ao obter o resultado do pagamento.", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // Verifica se o pagamento foi cancelado e exibe a mensagem
                if (data != null && data.hasExtra(MercadoPagoCheckout.EXTRA_ERROR)) {
                    final MercadoPagoError mercadoPagoError = (MercadoPagoError) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR);
                    exibirMensagemErro(mercadoPagoError);
                } else {
                    // O usuário cancelou a operação manualmente
                    Toast.makeText(this, "Pagamento cancelado pelo usuário.", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Caso um código de resultado diferente seja retornado
                Toast.makeText(this, "Erro inesperado ao processar o pagamento.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void respostaMercadoPago(Payment pagamento) {

        // Obtém o status do pagamento
        String status = pagamento.getPaymentStatus();

        String cep = binding.cep.getText().toString();
        String rua = binding.rua.getText().toString();
        String numero = binding.numero.getText().toString();
        String bairro = binding.bairro.getText().toString();
        String cidade = binding.cidade.getText().toString();
        String estado = binding.estado.getText().toString();
        String complemento = binding.complemento.getText().toString();
        String ddd = binding.ddd.getText().toString();
        String telefone = binding.telefone.getText().toString();

        String endereco = "Cep: " + cep + "\nRua: " + rua + ", " + numero + "\nBairro: " + bairro + "\nCidade: " + cidade + " / " + estado + "\nComplemento: " + complemento;
        String telefoneComDDD = "Telefone: " + "(" + ddd + ")" + " " + telefone;
        String status_entrega = "Status de Entrega: " + " " + "Em andamento";


        String nomeProduto = "Nome: " + nome;
        String tamanho = "Tamanho: " + tamanho_calcado;
        String precoProduto = "Preço: R$ " + preco;

        // Exibe mensagens com base no status do pagamento
        if (status.equalsIgnoreCase("approved")) {
            String status_pagamento = "Status de Pagamento: " + "Pagamento Aprovado!";

            // Pagamento aprovado
            Snackbar snackbar = Snackbar.make(binding.main, "Pagamento efetuado com sucesso!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.parseColor("#4E9100"));
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();

            db.salvarDadosPedidosUsuario(endereco, telefoneComDDD, nomeProduto, precoProduto, tamanho, status_pagamento, status_entrega);

            // Aguardar 2 segundos antes de chamar a função
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    irParaTelaPedidos();
                }
            }, 2000); // 2000 ms = 2 segundos

        } else if (status.equalsIgnoreCase("rejected")) {
            // Pagamento rejeitado
            Snackbar snackbar = Snackbar.make(binding.main, "Erro ao fazer o pagamento!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.RED);
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();


        } else if (status.equalsIgnoreCase("in_process")) {
            String status_pagamento = "Status de Pagamento: " + "Pagamento em Processamento!";

            // Pagamento pendente
            Snackbar snackbar = Snackbar.make(binding.main, "Pagamento em processamento!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.YELLOW);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();

            status_entrega = "Status de Entrega: " + "Aguardando Pagamento!";

            db.salvarDadosPedidosUsuario(endereco, telefoneComDDD, nomeProduto, precoProduto, tamanho, status_pagamento, status_entrega);

            // Aguardar 2 segundos antes de chamar a função
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    irParaTelaPedidos();
                }
            }, 2000); // 2000 ms = 2 segundos

        } else {
            // Qualquer outro erro
            Snackbar snackbar = Snackbar.make(binding.main, "Erro inesperado!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.RED);
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    private void exibirMensagemErro(MercadoPagoError error) {
        // Verifica se o erro é de rede ou outro erro
        if (error.isNoConnectivityError()) {
            Toast.makeText(this, "Erro de rede ao processar o pagamento.", Toast.LENGTH_SHORT).show();
        } else {
            // Exibe a mensagem de erro detalhada
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void irParaTelaPedidos(){
        Intent intent = new Intent(this, Pedidos.class);
        startActivity(intent);
    }
}