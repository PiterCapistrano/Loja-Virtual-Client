package com.pitercapistrano.applojavirtualclient.activities.FormLogin

import android.app.Activity // Importa a classe Activity para interação com atividades do Android
import android.content.ContentValues.TAG
import android.content.Intent // Importa a classe Intent para iniciar outras atividades
import android.graphics.Color // Importa a classe Color para manipulação de cores
import android.os.Bundle // Importa a classe Bundle para passar dados entre atividades
import android.os.Handler // Importa a classe Handler para agendar tarefas
import android.os.Looper
import android.util.Log
import android.view.View // Importa a classe View para manipulação de elementos da interface
import android.view.inputmethod.InputMethodManager
import android.widget.Toast // Importa a classe Toast para exibir mensagens curtas
import androidx.activity.enableEdgeToEdge // Permite a interface de tela cheia
import androidx.appcompat.app.AppCompatActivity // Importa a classe AppCompatActivity para compatibilidade de recursos
import androidx.core.view.ViewCompat // Importa a classe ViewCompat para manipulação de visualizações
import androidx.core.view.WindowInsetsCompat // Importa a classe WindowInsetsCompat para gerenciar insets da janela
import com.google.android.gms.auth.api.signin.GoogleSignIn // Importa a classe GoogleSignIn para autenticação do Google
import com.google.android.gms.auth.api.signin.GoogleSignInClient // Importa a classe GoogleSignInClient para interagir com o cliente de login do Google
import com.google.android.gms.auth.api.signin.GoogleSignInOptions // Importa a classe GoogleSignInOptions para configurar opções de login do Google
import com.google.android.gms.common.api.ApiException // Importa a classe ApiException para lidar com exceções de API
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth // Importa a classe FirebaseAuth para autenticação com Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider // Importa a classe GoogleAuthProvider para autenticação do Google com Firebase
import com.google.firebase.firestore.FirebaseFirestore // Importa a classe FirebaseFirestore para manipulação de Firestore
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.activities.FormCadastro.FormCadastro
import com.pitercapistrano.applojavirtualclient.activities.Home.Home
import com.pitercapistrano.applojavirtualclient.databinding.ActivityFormLoginBinding
import com.pitercapistrano.applojavirtualclient.dialog.DialogCarregando


class FormLogin : AppCompatActivity() {

    private lateinit var binding: ActivityFormLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient // Declara uma variável para o cliente de login do Google
    private lateinit var mAuth: FirebaseAuth

    // Constantes usadas para identificar códigos de requisição
    companion object {
        private const val LOGIN_REQUEST_CODE = 1001 // Código para identificar a requisição de login
        private const val RC_SIGN_IN = 9001 // Código para identificar a requisição de login com Google
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = Color.parseColor("#000000")
        supportActionBar!!.hide()

        val dialogCarregando = DialogCarregando(this)

        binding.txtCadastrar.setOnClickListener {
            val intent = Intent(this, FormCadastro::class.java)
            startActivity(intent)
        }

        binding.btEntrar.setOnClickListener {view ->
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

            if (email.isEmpty() || senha.isEmpty()){
                val snackbar = Snackbar.make(view, "Preencha Todos os Campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener {
                    if (it.isSuccessful) {
                        dialogCarregando.iniciarCarregamentoAlertDialog()
                        binding.progressBar.visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({
                            goToHome()
                            dialogCarregando.liberarAlertDialog()
                        }, 3000)
                    }
                }.addOnFailureListener {
                    val snackbar = Snackbar.make(view, "Erro ao efetuar o login!", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.setTextColor(Color.WHITE)
                    snackbar.show()
                }
            }
        }

        // Configura as opções de login com Google, solicitando ID e email
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_server_client_id))  // Solicita token de ID
            .requestEmail()  // Solicita email
            .build() // Constrói as opções de login

        // Inicializa o cliente de login do Google
        googleSignInClient = GoogleSignIn.getClient(this, gso) // Obtém o cliente de login do Google com as opções configuradas

        // Define o comportamento de clique no botão de login com Google
        binding.btGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent  // Cria o Intent de login do Google
            startActivityForResult(signInIntent, RC_SIGN_IN)  // Inicia a Activity de login
        }
    }



    // Método que redireciona o usuário para a Activity Home
    private fun goToHome() {
        val intent = Intent(this, Home::class.java)  // Cria um Intent para a Home
        startActivity(intent) // Inicia a Activity Home
        Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show() // Exibe mensagem de sucesso
        finish()  // Fecha a Activity de login
    }

    // Método para tratar o resultado das Activities iniciadas
    @Deprecated("Deprecated in Java") // Indica que o método está obsoleto
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // Chama o método da superclasse
        if (requestCode == LOGIN_REQUEST_CODE) { // Verifica se o código de requisição é o de login
            if (resultCode == Activity.RESULT_OK) { // Verifica se o resultado é OK
                val intent = Intent(this, Home::class.java)  // Cria um Intent para a Home
                startActivity(intent) // Inicia a Activity Home
                Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show() // Exibe mensagem de sucesso
                finish()  // Fecha a Activity de login
            }
        } else if (requestCode == RC_SIGN_IN) { // Verifica se o código de requisição é o de login com Google
            // Lida com o retorno do login com Google
            val task = GoogleSignIn.getSignedInAccountFromIntent(data) // Obtém a conta Google autenticada
            try {
                val account = task.getResult(ApiException::class.java)!!  // Obtém a conta Google autenticada
                firebaseAuthWithGoogle(account.idToken!!)  // Autentica com Firebase usando Google
            } catch (e: ApiException) {
                Log.w("LoginGoogle", "Google sign in failed", e)
                Toast.makeText(this, "Erro ao logar!", Toast.LENGTH_SHORT).show() // Log de falha de login
            }
        }
    }

    // Autenticando com Firebase usando o token do Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val dialogCarregando = DialogCarregando(this)
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d("LoginGoogle", "Login com Google bem-sucedido: ${user?.displayName}")

                    // Salvando o usuário no Firestore
                    user?.displayName?.let { displayName ->
                        val db = FirebaseFirestore.getInstance()
                        val userId = user.uid
                        val userMap = hashMapOf("name" to displayName)

                        db.collection("Usuarios").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Log.d("Firestore", "DocumentSnapshot successfully written!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error writing document", e)
                            }
                    }
                    dialogCarregando.iniciarCarregamentoAlertDialog()
                    binding.progressBar.visibility = View.VISIBLE
                    Handler().postDelayed({

                        saveUserToFirestore(user)
                        dialogCarregando.liberarAlertDialog()
                        goToHome()
                    }, 3000)
                } else {
                    Log.w("LoginGoogle", "Erro ao autenticar com Google", task.exception)
                    Toast.makeText(this, "Erro ao autenticar!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Função para salvar os dados do usuário no Firestore
    private fun saveUserToFirestore(user: FirebaseUser?) {
        val db = FirebaseFirestore.getInstance() // Obtém uma instância do Firestore

        // Cria um mapa para armazenar os dados do usuário
        val userMap = hashMapOf(
            "nome" to user?.displayName, // Armazena o nome do usuário
            "email" to user?.email, // Armazena o e-mail do usuário
            "foto" to (user?.photoUrl?.toString() ?: "") // Armazena a URL da foto do usuário ou uma string vazia
        )

        // Salva os dados no Firestore com o ID do usuário
        user?.uid?.let {
            db.collection("Usuarios").document(it) // Referencia a coleção de usuários
                .set(userMap) // Salva os dados do usuário
                .addOnSuccessListener {
                    Log.d(TAG, "Dados do usuário salvos no Firestore.") // Log de sucesso
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Erro ao salvar dados do usuário.", e) // Log de erro
                }
        }
    }

}