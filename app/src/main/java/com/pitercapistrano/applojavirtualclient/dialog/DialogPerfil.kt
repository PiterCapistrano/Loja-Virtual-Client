package com.pitercapistrano.applojavirtualclient.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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

    fun recuperarDadosPerfil(){
        val imgPerfil = binding.imgPerfil
        val nomePerfil = binding.txtNome
        val emailPerfil = binding.txtEmail

        val db = DB()
        db.recuperarDadosPerfil(nomePerfil, emailPerfil)

        binding.btSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, FormLogin::class.java)
            activity.startActivity(intent)
            activity.finish()
            Toast.makeText(activity, "Usuário Deslogado com Sucesso!", Toast.LENGTH_SHORT).show()
        }
    }
}