// Pacote da aplicação que contém o diálogo de perfil
package com.pitercapistrano.applojavirtualclient.dialog

// Importações necessárias para funcionalidades da atividade, diálogos, Toast, Firebase e Glide
import android.app.Activity // Para manipulação da atividade atual
import android.app.AlertDialog // Para criar diálogos de alerta
import android.content.Intent // Para iniciar novas atividades
import android.widget.Toast // Para exibir mensagens temporárias na tela
import com.bumptech.glide.Glide // Para carregar imagens de maneira eficiente
import com.google.firebase.auth.FirebaseAuth // Para autenticação com Firebase
import com.pitercapistrano.applojavirtualclient.activities.EditarPerfil.EditarPerfil // Atividade para editar perfil
import com.pitercapistrano.applojavirtualclient.activities.FormLogin.FormLogin // Atividade de login
import com.pitercapistrano.applojavirtualclient.databinding.DialogPerfilBinding // Para vinculação de layout
import com.pitercapistrano.applojavirtualclient.model.DB // Classe para manipulação do banco de dados

// Classe DialogPerfil que recebe uma atividade como parâmetro
class DialogPerfil(private val activity: Activity) {

    // Declarações de variáveis para o diálogo e vinculação do layout
    lateinit var dialog: AlertDialog
    lateinit var binding: DialogPerfilBinding

    // Método para iniciar o diálogo de perfil
    fun iniciarPerfil() {
        // Cria um builder para o AlertDialog utilizando a atividade atual
        val builder = AlertDialog.Builder(activity)
        // Infla o layout do diálogo utilizando o layoutInflater da atividade
        binding = DialogPerfilBinding.inflate(activity.layoutInflater)
        // Define a view do diálogo como o layout inflado
        builder.setView(binding.root)
        // Define que o diálogo pode ser cancelado pelo usuário
        builder.setCancelable(true)
        // Cria o AlertDialog a partir do builder
        dialog = builder.create()
        // Exibe o diálogo
        dialog.show()
    }

    // Método para recuperar os dados do perfil
    fun recuperarDadosPerfil() {
        // Declarações de variáveis para os componentes do layout
        val imgPerfil = binding.imgPerfil // Imagem do perfil
        val nomePerfil = binding.txtNome // Nome do perfil
        val emailPerfil = binding.txtEmail // Email do perfil

        // Recupera os dados do Firestore usando a classe DB
        val db = DB()
        db.recuperarDadosPerfil(nomePerfil, emailPerfil) // Chama método para recuperar dados

        // Obtém o ID do usuário logado no Firebase
        val usuarioID = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance() // Instância do Firestore

        // Verifica se o usuárioID não é nulo
        if (usuarioID != null) {
            // Referência ao documento do usuário no Firestore
            val documentReference = firestore.collection("Usuarios").document(usuarioID)
            // Adiciona um listener para ouvir mudanças no documento
            documentReference.addSnapshotListener { documentSnapshot, error ->
                // Verifica se o snapshot do documento existe
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Carrega a foto de perfil do Firestore
                    val fotoUrl = documentSnapshot.getString("foto")
                    Glide.with(activity).load(fotoUrl).into(imgPerfil) // Carrega a imagem usando Glide
                }
            }
        }

        // Configura o botão de atualizar dados
        binding.btAtualizarDados.setOnClickListener {
            val intent = Intent(activity, EditarPerfil::class.java) // Cria intenção para editar perfil
            activity.startActivity(intent) // Inicia a atividade de edição de perfil
        }

        // Configura a função de logout
        binding.btSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Desloga o usuário do Firebase
            val intent = Intent(activity, FormLogin::class.java) // Cria intenção para a tela de login
            activity.startActivity(intent) // Inicia a atividade de login
            activity.finish() // Finaliza a atividade atual
            // Exibe mensagem informando que o usuário foi deslogado
            Toast.makeText(activity, "Usuário Deslogado com Sucesso!", Toast.LENGTH_SHORT).show()
        }
    }
}
