package com.pitercapistrano.applojavirtualclient.activities.Pagamento;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        String tamanho_calcado = getIntent().getExtras().getString("tamanho_calcado");
        Log.d("tamanho_calcado", tamanho_calcado);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://viacep.com.br/")
                .build();

        Api api = retrofit.create(Api.class);

        binding.btCep.setOnClickListener(v -> {
            String cep = binding.cep.getText().toString();

            if (cep.isEmpty()) {
                Toast.makeText(this, "Preencha o Cep!", Toast.LENGTH_SHORT).show();
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
    }

    private void setFormularios(String logradouro, String bairro, String localidade, String uf) {
        binding.rua.setText(logradouro);
        binding.bairro.setText(bairro);
        binding.cidade.setText(localidade);
        binding.estado.setText(uf);
    }
}