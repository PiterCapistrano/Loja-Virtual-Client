package com.pitercapistrano.applojavirtualclient.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.pitercapistrano.applojavirtualclient.activities.EditarPerfil.EditarPerfil
import com.pitercapistrano.applojavirtualclient.activities.FormLogin.FormLogin
import com.pitercapistrano.applojavirtualclient.databinding.DialogPerfilBinding
import com.pitercapistrano.applojavirtualclient.model.DB

class DialogPerfil(private val activity: Activity) {

    lateinit var dialog: AlertDialog
    lateinit var binding: DialogPerfilBinding

    fun iniciarPerfil(){
        val builder = AlertDialog.Builder(activity)
        binding = DialogPerfilBinding.inflate(activity.layoutInflater)
        builder.setView(binding.root)
        builder.setCancelable(true)
        dialog = builder.create()
        dialog.show()
    }

    fun recuperarDadosPerfil() {
        val imgPerfil = binding.imgPerfil
        val nomePerfil = binding.txtNome
        val emailPerfil = binding.txtEmail

        // Recupera os dados do Firestore usando a classe DB
        val db = DB()
        db.recuperarDadosPerfil(nomePerfil, emailPerfil)

        // Recupera e exibe a foto de perfil usando Glide
        val usuarioID = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        if (usuarioID != null) {
            val documentReference = firestore.collection("Usuarios").document(usuarioID)
            documentReference.addSnapshotListener { documentSnapshot, error ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Carrega a foto de perfil
                    val fotoUrl = documentSnapshot.getString("foto")
                    Glide.with(activity).load(fotoUrl).into(imgPerfil)
                }
            }
        }

        // Função de logout
        binding.btSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, FormLogin::class.java)
            activity.startActivity(intent)
            activity.finish()
            Toast.makeText(activity, "Usuário Deslogado com Sucesso!", Toast.LENGTH_SHORT).show()
        }
    }
}
