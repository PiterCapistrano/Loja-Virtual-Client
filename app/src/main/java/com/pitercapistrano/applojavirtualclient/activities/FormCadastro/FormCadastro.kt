package com.pitercapistrano.applojavirtualclient.activities.FormCadastro

import android.content.Intent // Importa a classe Intent para iniciar atividades
import android.graphics.Color // Importa a classe Color para manipulação de cores
import android.net.Uri // Importa a classe Uri para manipulação de URIs
import android.os.Bundle // Importa a classe Bundle para armazenar dados de atividades
import android.util.Log // Importa a classe Log para registro de mensagens de log
import android.view.View // Importa a classe View para manipulação de componentes de UI
import android.view.inputmethod.InputMethodManager // Importa para gerenciar o teclado
import android.widget.Toast // Importa a classe Toast para exibir mensagens breves
import androidx.activity.result.ActivityResultLauncher // Importa a classe para gerenciar resultados de atividades
import androidx.activity.result.contract.ActivityResultContracts // Importa contratos para atividades resultantes
import androidx.appcompat.app.AppCompatActivity // Importa a classe base para atividades com suporte a ActionBar
import androidx.core.view.ViewCompat // Importa a classe para compatibilidade de visualizações
import androidx.core.view.WindowInsetsCompat // Importa a classe para lidar com ajustes de insets
import com.google.android.gms.tasks.OnFailureListener // Importa a interface para manipulação de falhas
import com.google.android.material.snackbar.Snackbar // Importa a classe Snackbar para exibir mensagens
import com.google.firebase.FirebaseNetworkException // Importa exceção para falhas de rede no Firebase
import com.google.firebase.auth.FirebaseAuth // Importa a classe para autenticação Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException // Importa exceção para colisão de usuários
import com.google.firebase.auth.FirebaseAuthWeakPasswordException // Importa exceção para senhas fracas
import com.google.firebase.firestore.FirebaseFirestore // Importa a classe para interagir com o Firestore
import com.google.firebase.storage.FirebaseStorage // Importa a classe para interagir com o Firebase Storage
import com.google.firebase.storage.StorageReference // Importa a classe para referência de armazenamento
import com.google.firebase.storage.UploadTask // Importa a classe para tarefas de upload
import com.pitercapistrano.applojavirtualclient.R // Importa os recursos da aplicação
import com.pitercapistrano.applojavirtualclient.databinding.ActivityFormCadastroBinding // Importa a classe de vinculação de layout
import com.pitercapistrano.applojavirtualclient.model.DB // Importa a classe do modelo de banco de dados
import java.util.UUID // Importa a classe para gerar identificadores únicos

class FormCadastro : AppCompatActivity() {

    private lateinit var binding: ActivityFormCadastroBinding // Declaração do binding para o layout
    private var mSelecionarUri: Uri? = null // Declaração de variável para armazenar URI da imagem selecionada
    private var usuarioId: String? = null // Declaração de variável para armazenar ID do usuário

    // Declara e registra um launcher de atividade para obter resultados
    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) { // Verifica se o resultado da atividade foi bem-sucedido
                mSelecionarUri = result.data?.data // Obtém a URI da imagem selecionada
                binding.imgPerfil.setImageURI(mSelecionarUri) // Exibe a imagem selecionada no ImageView
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Chama o método da superclasse
        binding = ActivityFormCadastroBinding.inflate(layoutInflater) // Infla o layout usando binding
        setContentView(binding.root) // Define o layout da atividade

        // Ajusta os insets da janela para garantir que a UI não seja coberta pela barra de status
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // Obtém os insets do sistema
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // Define padding
            insets // Retorna os insets
        }

        window.statusBarColor = Color.parseColor("#000000") // Define a cor da barra de status para preto
        window.decorView.systemUiVisibility = 0 // Reseta a visibilidade do sistema UI

        val db = DB() // Cria uma instância do banco de dados

        // Configura o listener para mostrar/ocultar a senha
        binding.mostrarSenha.setOnClickListener {
            // Verifica se o campo de senha está oculto ou visível
            if (binding.editSenha.inputType == (android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Muda para texto normal (senha visível)
                binding.editSenha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Muda para campo de senha (ocultar senha)
                binding.editSenha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move o cursor para o final do texto
            binding.editSenha.setSelection(binding.editSenha.text.length)
        }

        // Configura o listener para mostrar/ocultar a confirmação da senha
        binding.mostrarConfirmarSenha.setOnClickListener {
            // Verifica se o campo de confirmação de senha está oculto ou visível
            if (binding.editConfirmarSenha.inputType == (android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Muda para texto normal (senha visível)
                binding.editConfirmarSenha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Muda para campo de senha (ocultar senha)
                binding.editConfirmarSenha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move o cursor para o final do texto
            binding.editConfirmarSenha.setSelection(binding.editConfirmarSenha.text.length)
        }

        // Configura o listener para o botão de atualização da foto
        binding.btAtualizarFoto.setOnClickListener {
            selecionarFotoGaleria() // Chama método para selecionar uma foto da galeria
        }

        // Configura o listener para o botão de cadastro
        binding.btCadastrar.setOnClickListener {
            // Obtém os valores inseridos nos campos de texto
            val nome = binding.editNome.text.toString()
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmarSenha = binding.editConfirmarSenha.text.toString()
            // Esconde o teclado ao tocar no botão
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

            // Valida se todos os campos estão preenchidos
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                // Exibe uma Snackbar com mensagem de erro
                val snackbar = Snackbar.make(it, "Preencha Todos os Campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED) // Define a cor de fundo da Snackbar
                snackbar.setTextColor(Color.WHITE) // Define a cor do texto da Snackbar
                snackbar.show() // Exibe a Snackbar
            } else if (senha != confirmarSenha) { // Valida se a confirmação da senha é igual
                // Exibe uma Snackbar com mensagem de erro
                val snackbar = Snackbar.make(it, "Erro ao confirmar a senha!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED) // Define a cor de fundo da Snackbar
                snackbar.setTextColor(Color.WHITE) // Define a cor do texto da Snackbar
                snackbar.show() // Exibe a Snackbar
            } else {
                // Tenta criar um novo usuário com o email e senha fornecidos
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task -> // Listener para o resultado da tarefa
                        if (task.isSuccessful) { // Verifica se o cadastro foi bem-sucedido
                            salvarDadosUsuario() // Chama método para salvar os dados do usuário
                            Toast.makeText(this, "Cadastro Realizado com Sucesso!", Toast.LENGTH_SHORT).show() // Exibe uma mensagem de sucesso
                            finish() // Finaliza a atividade
                        }
                    }.addOnFailureListener { erroCadastro -> // Listener para tratamento de falha
                        // Define a mensagem de erro baseada na exceção recebida
                        val mensagemErro = when (erroCadastro) {
                            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres!" // Mensagem para senha fraca
                            is FirebaseAuthUserCollisionException -> "Essa conta já foi cadastrada!" // Mensagem para colisão de usuários
                            is FirebaseNetworkException -> "Sem conexão com a internet!" // Mensagem para falha de rede
                            else -> "Erro ao cadastrar o usuário! Verifique se o E-mail é válido!" // Mensagem padrão de erro
                        }
                        // Exibe a Snackbar com a mensagem de erro
                        val snackbar = Snackbar.make(it, mensagemErro, Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED) // Define a cor de fundo da Snackbar
                        snackbar.setTextColor(Color.WHITE) // Define a cor do texto da Snackbar
                        snackbar.show() // Exibe a Snackbar
                    }
            }
        }
    }

    // Método para selecionar uma foto da galeria
    fun selecionarFotoGaleria() {
        val intent = Intent(Intent.ACTION_PICK) // Cria um Intent para selecionar uma imagem
        intent.type = "image/*" // Define o tipo de mídia a ser selecionado
        activityResultLauncher.launch(intent) // Inicia a atividade para obter o resultado
    }

    // Método para salvar os dados do usuário no Firebase Storage e Firestore
    fun salvarDadosUsuario() {
        // Verifica se uma imagem foi selecionada
        if (mSelecionarUri == null) {
            Log.e("Erro", "Nenhuma imagem selecionada") // Exibe erro no log
            return // Se não houver imagem, interrompe o processo
        }

        // Gera um nome único para o arquivo da imagem
        val nomeArquivo = UUID.randomUUID().toString() // Gera um UUID para o nome do arquivo
        // Cria uma referência no Firebase Storage para armazenar a imagem
        val reference: StorageReference = FirebaseStorage.getInstance().getReference("/imagens/$nomeArquivo")

        // Realiza o upload da imagem selecionada
        reference.putFile(mSelecionarUri!!).addOnSuccessListener {
            // Após o upload, obtém a URL da imagem no Storage
            reference.downloadUrl.addOnSuccessListener { uri -> // Obtém a URL da imagem
                Log.i("url_img", uri.toString()) // Exibe a URL da imagem no log
                val foto = uri.toString() // Armazena a URL da imagem

                // Inicia a instância do Firebase Firestore para salvar os dados
                val nome = binding.editNome.text.toString() // Ajuste para acessar o campo no layout
                val email = binding.editEmail.text.toString() // Obtém o email do campo
                val db = FirebaseFirestore.getInstance() // Cria uma instância do Firestore

                // Cria um mapa para armazenar os dados do usuário
                val usuarios = hashMapOf(
                    "nome" to nome, // Armazena o nome do usuário
                    "email" to email, // Armazena o email do usuário
                    "foto" to foto // Armazena a URL da foto do usuário
                )

                // Obtém o ID do usuário autenticado no Firebase Authentication
                val usuarioId = FirebaseAuth.getInstance().currentUser?.uid // Obtém o ID do usuário

                // Verifica se o ID do usuário é válido
                usuarioId?.let {
                    // Cria uma referência ao documento do usuário no Firestore
                    val documentReference = db.collection("Usuarios").document(it)

                    // Salva os dados do usuário no Firestore
                    documentReference.set(usuarios).addOnSuccessListener {
                        Log.i("db", "Sucesso ao salvar os dados!") // Log de sucesso
                    }.addOnFailureListener { e -> // Listener para tratamento de falha
                        Log.e("db_error", "Erro ao salvar os dados! ${e.message}") // Log de erro
                    }
                }
            }.addOnFailureListener { e -> // Listener para tratamento de falha ao obter a URL
                Log.e("storage_error", "Erro ao obter URL da imagem: ${e.message}") // Log de erro
            }
        }.addOnFailureListener { e -> // Listener para tratamento de falha ao realizar o upload
            Log.e("upload_error", "Erro ao fazer upload da imagem: ${e.message}") // Log de erro
        }
    }
}
