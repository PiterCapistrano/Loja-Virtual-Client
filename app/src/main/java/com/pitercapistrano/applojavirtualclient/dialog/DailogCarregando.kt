package com.pitercapistrano.applojavirtualclient.dialog

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pitercapistrano.applojavirtualclient.R

class DailogCarregando(private val activity: Activity){

    lateinit var dialog: AlertDialog

    fun iniciarCarregamentoAlertDialog(){
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity.layoutInflater
        builder.setView(layoutInflater.inflate(R.layout.activity_dailog_carregando, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun liberarAlertDialog(){
        dialog.dismiss()
    }
}