package com.pitercapistrano.applojavirtualclient.model

import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class DB {

    fun salvarDadosUsuario(nome: String, email: String){

        val usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()

        val usuarios = hashMapOf(
            "nome" to nome,
            "email" to email
        )

        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        documentReference.set(usuarios).addOnSuccessListener {
            Log.d("DB", "Sucesso ao salvar os dados")
        }.addOnFailureListener {
            Log.d("DB_ERROR", "Erro ao salvar os dados! ${it.printStackTrace()}")
        }
    }

    fun recuperarDadosPerfil(nomePerfil: TextView, emailPerfil: TextView){
        val usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        val email = FirebaseAuth.getInstance().currentUser!!.email
        val db = FirebaseFirestore.getInstance()

        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        documentReference.addSnapshotListener { documento, error ->
            if (documento != null){
                nomePerfil.text = documento.getString("nome")
                emailPerfil.text = email
            }
        }
    }
}