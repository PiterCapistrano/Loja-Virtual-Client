package com.pitercapistrano.applojavirtualclient.dialog

import android.app.Activity
import android.app.AlertDialog
import com.pitercapistrano.applojavirtualclient.R

class DialogCarregando(private val activity: Activity){

    lateinit var dialog: AlertDialog

    fun iniciarCarregamentoAlertDialog(){
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity.layoutInflater
        builder.setView(layoutInflater.inflate(R.layout.activity_dialog_carregando, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun liberarAlertDialog(){
        dialog.dismiss()
    }
}