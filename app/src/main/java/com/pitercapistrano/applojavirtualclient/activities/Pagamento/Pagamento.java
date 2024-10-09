package com.pitercapistrano.applojavirtualclient.activities.Pagamento;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pitercapistrano.applojavirtualclient.R;
import com.pitercapistrano.applojavirtualclient.databinding.ActivityPagamentoBinding;

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
    }
}