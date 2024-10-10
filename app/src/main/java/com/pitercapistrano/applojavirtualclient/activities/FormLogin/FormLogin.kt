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
        // Define a cor da barra de status como preto usando o código hexadecimal
        window.statusBarColor = Color.parseColor("#000000")

// Define a visibilidade da interface do sistema como padrão (sem modificações)
        window.decorView.systemUiVisibility = 0

// Cria uma instância do diálogo de carregamento, que será exibido durante operações de longa duração
        val dialogCarregando = DialogCarregando(this)

// Define o comportamento ao clicar no texto de cadastro
        binding.txtCadastrar.setOnClickListener {
            // Cria um Intent para navegar para a atividade de cadastro
            val intent = Intent(this, FormCadastro::class.java)
            // Inicia a atividade de cadastro
            startActivity(intent)
        }

// Define o comportamento ao clicar no botão de login
        binding.btEntrar.setOnClickListener { view ->
            // Obtém o texto inserido no campo de email
            val email = binding.editEmail.text.toString()
            // Obtém o texto inserido no campo de senha
            val senha = binding.editSenha.text.toString()
            // Obtém o gerenciador de input do sistema
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            // Oculta o teclado virtual ao clicar no botão de login
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

            // Verifica se os campos de email ou senha estão vazios
            if (email.isEmpty() || senha.isEmpty()) {
                // Cria uma Snackbar para exibir uma mensagem de erro se os campos estiverem vazios
                val snackbar = Snackbar.make(view, "Preencha Todos os Campos!", Snackbar.LENGTH_SHORT)
                // Define a cor de fundo da Snackbar como vermelho
                snackbar.setBackgroundTint(Color.RED)
                // Define a cor do texto da Snackbar como branco
                snackbar.setTextColor(Color.WHITE)
                // Exibe a Snackbar com a mensagem de erro
                snackbar.show()
            } else {
                // Tenta fazer login com email e senha usando Firebase Authentication
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener {
                    // Verifica se a autenticação foi bem-sucedida
                    if (it.isSuccessful) {
                        // Inicia o diálogo de carregamento
                        dialogCarregando.iniciarCarregamentoAlertDialog()
                        // Torna a barra de progresso visível para indicar que a operação está em andamento
                        binding.progressBar.visibility = View.VISIBLE
                        // Agenda uma tarefa para ser executada no thread principal após 3 segundos
                        Handler(Looper.getMainLooper()).postDelayed({
                            // Chama o método para navegar para a Home
                            goToHome()
                            // Libera o diálogo de carregamento após a navegação
                            dialogCarregando.liberarAlertDialog()
                        }, 3000) // Define o atraso de 3000 milissegundos
                    }
                }.addOnFailureListener {
                    // Cria uma Snackbar para exibir uma mensagem de erro se o login falhar
                    val snackbar = Snackbar.make(view, "Erro ao efetuar o login!", Snackbar.LENGTH_SHORT)
                    // Define a cor de fundo da Snackbar como vermelho
                    snackbar.setBackgroundTint(Color.RED)
                    // Define a cor do texto da Snackbar como branco
                    snackbar.setTextColor(Color.WHITE)
                    // Exibe a Snackbar com a mensagem de erro
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

    // Sobrescreve o método onStart, que é chamado quando a atividade se torna visível para o usuário
    override fun onStart() {
        // Chama o método onStart da superclasse para garantir que a lógica padrão seja executada
        super.onStart()

        // Obtém a instância atual do FirebaseAuth e armazena o usuário autenticado
        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        // Verifica se um usuário está autenticado (ou seja, se usuarioAtual não é nulo)
        if (usuarioAtual != null) {
            // Chama o método goToHome para navegar para a tela inicial se o usuário estiver autenticado
            goToHome()
        }
    }


    // Método que redireciona o usuário para a Activity Home
    private fun goToHome() {
        val intent = Intent(this, Home::class.java)  // Cria um Intent para a Home
        startActivity(intent) // Inicia a Activity Home
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
    // Método privado para autenticar um usuário com o Google usando um token de ID
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Cria credenciais para autenticação usando o token de ID do Google
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Obtém a instância do FirebaseAuth e tenta fazer login com as credenciais criadas
        FirebaseAuth.getInstance().signInWithCredential(credential)
            // Adiciona um listener para escutar o resultado da autenticação
            .addOnCompleteListener(this) { task ->
                // Verifica se a autenticação foi bem-sucedida
                if (task.isSuccessful) {
                    // Cria uma instância de DialogCarregando para exibir um diálogo de carregamento
                    val dialogCarregando = DialogCarregando(this)
                    // Obtém o usuário autenticado atual
                    val user = FirebaseAuth.getInstance().currentUser
                    // Exibe uma mensagem de sucesso ao usuário
                    Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                    // Registra um log indicando que o login com Google foi bem-sucedido
                    Log.d("LoginGoogle", "Login com Google bem-sucedido: ${user?.displayName}")

                    // Salva as informações do usuário no Firestore
                    user?.displayName?.let { displayName ->
                        // Obtém a instância do Firestore
                        val db = FirebaseFirestore.getInstance()
                        // Obtém o ID do usuário autenticado
                        val userId = user.uid
                        // Cria um mapa com o nome do usuário
                        val userMap = hashMapOf("name" to displayName)

                        // Armazena os dados do usuário no Firestore na coleção "Usuarios"
                        db.collection("Usuarios").document(userId).set(userMap)
                            .addOnSuccessListener {
                                // Registra um log indicando que o documento foi salvo com sucesso
                                Log.d("Firestore", "DocumentSnapshot successfully written!")
                            }
                            .addOnFailureListener { e ->
                                // Registra um log de erro se a gravação falhar
                                Log.w("Firestore", "Error writing document", e)
                            }
                    }
                    // Inicia o diálogo de carregamento
                    dialogCarregando.iniciarCarregamentoAlertDialog()
                    // Torna a barra de progresso visível
                    binding.progressBar.visibility = View.VISIBLE
                    // Agendamento de tarefa para ser executado após 3 segundos
                    Handler().postDelayed({
                        // Chama o método para salvar o usuário no Firestore
                        saveUserToFirestore(user)
                        // Libera o diálogo de carregamento
                        dialogCarregando.liberarAlertDialog()
                        // Redireciona o usuário para a tela inicial
                        goToHome()
                    }, 3000) // Atraso de 3000 milissegundos (3 segundos)
                } else {
                    // Registra um log indicando que houve um erro na autenticação com Google
                    Log.w("LoginGoogle", "Erro ao autenticar com Google", task.exception)
                    // Exibe uma mensagem de erro ao usuário
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