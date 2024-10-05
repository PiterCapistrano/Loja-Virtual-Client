package com.pitercapistrano.applojavirtualclient.activities.FormCadastro

import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.databinding.ActivityFormCadastroBinding
import com.pitercapistrano.applojavirtualclient.model.DB

class FormCadastro : AppCompatActivity() {

    private lateinit var binding: ActivityFormCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = Color.parseColor("#000000")
        supportActionBar!!.hide()

        val db = DB()

        binding.btCadastrar.setOnClickListener {

            val nome = binding.editNome.text.toString()
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()){
                val snackbar = Snackbar.make(it, "Preencha Todos os Campos!",Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener {
                    if (it.isSuccessful){
                        db.salvarDadosUsuario(nome, email,)
                        Toast.makeText(this, "Cadastro Realizado com Sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }.addOnFailureListener { erroCadastro ->
                    val mensagemErro = when(erroCadastro){
                        is FirebaseAuthWeakPasswordException -> "Digite uma senha com no ménimo 6 caracteres!"
                        is FirebaseAuthUserCollisionException -> "Essa conta já foi cadsatrada!"
                        is FirebaseNetworkException -> "Sem conexão com a internet!"
                        else -> "Erro ao cadastrar o usuário! Verifique se o E-mail é válido!"
                    }
                    val snackbar = Snackbar.make(it, mensagemErro,Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.setTextColor(Color.WHITE)
                    snackbar.show()
                }

            }
        }
    }
}