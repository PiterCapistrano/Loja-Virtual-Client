package com.pitercapistrano.applojavirtualclient.activities.Pagamento;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.pitercapistrano.applojavirtualclient.Api.Api;
import com.pitercapistrano.applojavirtualclient.R;
import com.pitercapistrano.applojavirtualclient.databinding.ActivityPagamentoBinding;
import com.pitercapistrano.applojavirtualclient.model.Endereco;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Pagamento extends AppCompatActivity {

    ActivityPagamentoBinding binding;
    private String tamanho_calcado;
    private String nome;
    private String preco;

    private final String PUBLIC_KEY ="APP_USR-df25dac5-4329-4449-80dd-2731d8ad3355";
    private final String ACCESS_TOKEN ="APP_USR-5384308295965344-100822-cbb3cd209d4dd2c7dbb8f9894b5cc3f8-36119358";

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

           if (cep.isEmpty() || rua.isEmpty() || numero.isEmpty() || bairro.isEmpty() || cidade.isEmpty() || estado.isEmpty()){
               Snackbar snackbar = Snackbar.make(v, "Preencha os campos obrigatórios!", Snackbar.LENGTH_SHORT);
               snackbar.setBackgroundTint(Color.RED);
               snackbar.setTextColor(Color.WHITE);
               snackbar.show();
           } else {

           }
        });
    }

    private void setFormularios(String logradouro, String bairro, String localidade, String uf) {
        binding.rua.setText(logradouro);
        binding.bairro.setText(bairro);
        binding.cidade.setText(localidade);
        binding.estado.setText(uf);
    }
}