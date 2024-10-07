package com.pitercapistrano.applojavirtualclient.activities.FormCadastro

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.databinding.ActivityFormCadastroBinding
import com.pitercapistrano.applojavirtualclient.model.DB
import java.util.UUID

class FormCadastro : AppCompatActivity() {

    private lateinit var binding: ActivityFormCadastroBinding
    private var mSelecionarUri: Uri? = null
    private var usuarioId: String? = null

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                mSelecionarUri = result.data?.data
                binding.imgPerfil.setImageURI(mSelecionarUri) // Exibe a imagem selecionada
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding.mostrarSenha.setOnClickListener {
            if (binding.editSenha.inputType == (android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Mudar para texto normal (senha visível)
                binding.editSenha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Mudar para campo de senha (ocultar senha)
                binding.editSenha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move o cursor para o final do texto
            binding.editSenha.setSelection(binding.editSenha.text.length)
        }

        binding.mostrarConfirmarSenha.setOnClickListener {
            if (binding.editConfirmarSenha.inputType == (android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Mudar para texto normal (senha visível)
                binding.editConfirmarSenha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Mudar para campo de senha (ocultar senha)
                binding.editConfirmarSenha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move o cursor para o final do texto
            binding.editConfirmarSenha.setSelection(binding.editConfirmarSenha.text.length)
        }


        binding.btAtualizarFoto.setOnClickListener {
            selecionarFotoGaleria()
        }

        binding.btCadastrar.setOnClickListener {
            val nome = binding.editNome.text.toString()
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmarSenha = binding.editConfirmarSenha.text.toString()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                val snackbar = Snackbar.make(it, "Preencha Todos os Campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
            } else if (senha != confirmarSenha){
                val snackbar = Snackbar.make(it, "Erro ao confirmar a senha!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            salvarDadosUsuario()
                            Toast.makeText(this, "Cadastro Realizado com Sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }.addOnFailureListener { erroCadastro ->
                        val mensagemErro = when (erroCadastro) {
                            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres!"
                            is FirebaseAuthUserCollisionException -> "Essa conta já foi cadastrada!"
                            is FirebaseNetworkException -> "Sem conexão com a internet!"
                            else -> "Erro ao cadastrar o usuário! Verifique se o E-mail é válido!"
                        }
                        val snackbar = Snackbar.make(it, mensagemErro, Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.setTextColor(Color.WHITE)
                        snackbar.show()
                    }
            }
        }
    }

    fun selecionarFotoGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityResultLauncher.launch(intent)
    }

    // Método para salvar os dados do usuário no Firebase Storage e Firestore
    fun salvarDadosUsuario() {
        // Verifica se uma imagem foi selecionada
        if (mSelecionarUri == null) {
            Log.e("Erro", "Nenhuma imagem selecionada") // Exibe erro no log
            return // Se não houver imagem, interrompe o processo
        }

        // Gera um nome único para o arquivo da imagem
        val nomeArquivo = UUID.randomUUID().toString()
        // Cria uma referência no Firebase Storage para armazenar a imagem
        val reference: StorageReference = FirebaseStorage.getInstance().getReference("/imagens/$nomeArquivo")

        // Realiza o upload da imagem selecionada
        reference.putFile(mSelecionarUri!!).addOnSuccessListener {
            // Após o upload, obtém a URL da imagem no Storage
            reference.downloadUrl.addOnSuccessListener { uri ->
                Log.i("url_img", uri.toString()) // Exibe a URL da imagem no log
                val foto = uri.toString() // Armazena a URL da imagem

                // Inicia a instância do Firebase Firestore para salvar os dados
                val nome = binding.editNome.text.toString() // Ajuste para acessar o campo no layout
                val email = binding.editEmail.text.toString()
                val db = FirebaseFirestore.getInstance()

                // Cria um mapa para armazenar os dados do usuário
                val usuarios = hashMapOf(
                    "nome" to nome,
                    "email" to email,
                    "foto" to foto
                )

                // Obtém o ID do usuário autenticado no Firebase Authentication
                val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

                // Verifica se o ID do usuário é válido
                usuarioId?.let {
                    // Cria uma referência ao documento do usuário no Firestore
                    val documentReference = db.collection("Usuarios").document(it)

                    // Salva os dados do usuário no Firestore
                    documentReference.set(usuarios).addOnSuccessListener {
                        Log.i("db", "Sucesso ao salvar os dados!") // Log de sucesso
                    }.addOnFailureListener { e ->
                        Log.e("db_error", "Erro ao salvar os dados! ${e.message}") // Log de erro
                    }
                }
            }.addOnFailureListener { e ->
                // Tratamento de erro ao obter a URL da imagem
                Log.e("storage_error", "Erro ao obter URL da imagem: ${e.message}") // Log de erro
            }
        }.addOnFailureListener { e ->
            // Tratamento de erro ao realizar o upload da imagem
            Log.e("upload_error", "Erro ao fazer upload da imagem: ${e.message}") // Log de erro
        }
    }
}
