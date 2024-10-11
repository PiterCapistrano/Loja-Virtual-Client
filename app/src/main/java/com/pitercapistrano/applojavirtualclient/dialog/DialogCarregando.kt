// Pacote da aplicação que contém o diálogo de carregamento
package com.pitercapistrano.applojavirtualclient.dialog

// Importações necessárias
import android.app.Activity // Para manipulação da atividade atual
import android.app.AlertDialog // Para criar diálogos de alerta
import com.pitercapistrano.applojavirtualclient.R // Para acessar os recursos da aplicação

// Classe DialogCarregando que recebe uma atividade como parâmetro
class DialogCarregando(private val activity: Activity) {

    // Declaração da variável dialog do tipo AlertDialog
    lateinit var dialog: AlertDialog

    // Método para iniciar o diálogo de carregamento
    fun iniciarCarregamentoAlertDialog() {
        // Cria um builder para o AlertDialog utilizando a atividade atual
        val builder = AlertDialog.Builder(activity)
        // Obtém o LayoutInflater da atividade para inflar o layout do diálogo
        val layoutInflater = activity.layoutInflater
        // Define a view do diálogo utilizando um layout específico
        builder.setView(layoutInflater.inflate(R.layout.activity_dialog_carregando, null))
        // Define que o diálogo não pode ser cancelado pelo usuário
        builder.setCancelable(false)
        // Cria o AlertDialog a partir do builder
        dialog = builder.create()
        // Exibe o diálogo
        dialog.show()
    }

    // Método para liberar (fechar) o diálogo de carregamento
    fun liberarAlertDialog() {
        // Dismiss (fecha) o diálogo se ele estiver visível
        dialog.dismiss()
    }
}
