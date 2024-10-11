package com.pitercapistrano.applojavirtualclient.activities.EditarPerfil
// Define o pacote onde a classe EditarPerfil está localizada.

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.pitercapistrano.applojavirtualclient.R
import com.pitercapistrano.applojavirtualclient.databinding.ActivityEditarPerfilBinding
import java.util.UUID

class EditarPerfil : AppCompatActivity() {
    // Define a classe EditarPerfil, que será usada para editar os dados do perfil do usuário.

    private lateinit var binding: ActivityEditarPerfilBinding
    // Declara o binding para acessar os componentes da interface usando view binding.

    private var mSelecionarUri: Uri? = null
    // Variável para armazenar o URI da imagem selecionada pelo usuário.

    private var usuarioId: String? = null
    // Variável para armazenar o ID do usuário autenticado.

    // Configura o resultado da atividade para selecionar uma imagem da galeria.
    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                mSelecionarUri = result.data?.data
                // Obtém o URI da imagem selecionada.

                if (mSelecionarUri != null) {
                    binding.imgPerfil.setImageURI(mSelecionarUri)
                    // Exibe a imagem selecionada na ImageView de perfil.
                } else {
                    Log.e("Erro", "Erro ao selecionar a imagem. URI nulo.")
                    // Exibe uma mensagem de erro no log se o URI da imagem for nulo.
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Ativa a exibição em tela cheia, utilizando as barras de status e navegação.

        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        // Inicializa o binding com o layout da atividade.

        setContentView(binding.root)
        // Define o layout da atividade como o conteúdo principal da tela.

        // Adiciona margens ao layout para as barras de sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = Color.parseColor("#000000")
        // Define a cor da barra de status como preta.

        window.decorView.systemUiVisibility = 0
        // Define a visibilidade do sistema UI para o padrão.

        // Define a ação de clique no botão para atualizar a foto de perfil.
        binding.btAtualizarFoto.setOnClickListener {
            selecionarFotoGaleria()
            // Chama a função para abrir a galeria e selecionar uma foto.
        }

        // Define a ação de clique no botão para atualizar os dados do perfil.
        binding.btAtualizarDados.setOnClickListener {
            atualizarDadosPerfil(it)
            // Chama a função para atualizar os dados do perfil do usuário.

            finish()
            // Encerra a atividade após a atualização.
        }
    }

    // Função para abrir a galeria de fotos e permitir ao usuário selecionar uma imagem.
    private fun selecionarFotoGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        // Define a ação de seleção de conteúdo como "imagem".

        activityResultLauncher.launch(intent)
        // Lança a atividade para selecionar a imagem.
    }

    // Função para atualizar os dados do perfil no Firestore.
    private fun atualizarDadosPerfil(view: View) {
        if (mSelecionarUri == null) {
            // Se nenhuma imagem for selecionada, exibe uma mensagem de erro no log e um Snackbar.
            Log.e("Erro", "Nenhuma imagem selecionada")
            Snackbar.make(view, "Selecione uma imagem para continuar", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Gera um nome de arquivo único para a imagem usando UUID.
        val nomeArquivo = UUID.randomUUID().toString()

        // Cria uma referência ao Firebase Storage para o upload da imagem.
        val reference: StorageReference = FirebaseStorage.getInstance().getReference("/imagens/$nomeArquivo")

        // Faz o upload da imagem para o Firebase Storage.
        reference.putFile(mSelecionarUri!!).addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
            // Após o upload, obtém a URL da imagem armazenada.
            reference.downloadUrl.addOnSuccessListener { uri: Uri ->
                Log.i("url_img", uri.toString())
                val foto = uri.toString()
                // Obtém a URL da imagem.

                // Obtém o nome digitado pelo usuário.
                val nome = binding.editNome.text.toString()

                // Se o nome ou a foto estiverem vazios, exibe uma mensagem de erro.
                if (nome.isEmpty() || foto.isEmpty()) {
                    Snackbar.make(view, "Selecione uma foto e digite o nome para atualizar os dados!", Snackbar.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Cria uma instância do Firestore para salvar os dados do usuário.
                val db = FirebaseFirestore.getInstance()

                // Cria um mapa de dados com o nome e a foto do usuário.
                val usuarios = hashMapOf<String, Any>(
                    "nome" to nome,
                    "foto" to foto
                )

                // Obtém o ID do usuário autenticado.
                usuarioId = FirebaseAuth.getInstance().currentUser?.uid

                // Atualiza os dados no Firestore se o ID do usuário estiver disponível.
                if (usuarioId != null) {
                    db.collection("Usuarios").document(usuarioId!!)
                        .update(usuarios)
                        .addOnCompleteListener { task ->
                            // Exibe um Snackbar de sucesso ou erro com base na resposta do Firestore.
                            if (task.isSuccessful) {
                                Snackbar.make(view, "Sucesso ao atualizar os dados!", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK") { finish() }
                                    .show()
                            } else {
                                Log.e("Firestore Error", "Erro ao atualizar os dados")
                                Snackbar.make(view, "Erro ao atualizar os dados!", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            // Exibe um log e uma mensagem de erro se falhar ao atualizar os dados.
                            Log.e("Firestore Error", "Erro ao atualizar os dados: $e")
                            Snackbar.make(view, "Erro ao atualizar os dados!", Snackbar.LENGTH_SHORT).show()
                        }
                } else {
                    // Se o ID do usuário não for encontrado, exibe uma mensagem de erro.
                    Log.e("Auth Error", "Usuário não autenticado")
                    Snackbar.make(view, "Erro ao obter o ID do usuário!", Snackbar.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e: Exception ->
                // Se falhar ao obter a URL da imagem, exibe um log e uma mensagem de erro.
                Log.e("storage_error", "Erro ao obter URL da imagem: $e")
                Snackbar.make(view, "Erro ao obter URL da imagem!", Snackbar.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e: Exception ->
            // Se o upload da imagem falhar, exibe um log e uma mensagem de erro.
            Log.e("upload_error", "Erro ao fazer upload da imagem: $e")
            Snackbar.make(view, "Erro ao fazer upload da imagem!", Snackbar.LENGTH_SHORT).show()
        }
    }
}
