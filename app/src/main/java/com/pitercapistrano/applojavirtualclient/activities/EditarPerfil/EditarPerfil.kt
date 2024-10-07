package com.pitercapistrano.applojavirtualclient.activities.EditarPerfil

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

    private lateinit var binding: ActivityEditarPerfilBinding
    private var mSelecionarUri: Uri? = null
    private var usuarioId: String? = null

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                mSelecionarUri = result.data?.data
                if (mSelecionarUri != null) {
                    binding.imgPerfil.setImageURI(mSelecionarUri) // Exibe a imagem selecionada
                } else {
                    Log.e("Erro", "Erro ao selecionar a imagem. URI nulo.")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = Color.parseColor("#000000")
        supportActionBar!!.hide()

        binding.btAtualizarFoto.setOnClickListener {
            selecionarFotoGaleria()
        }

        binding.btAtualizarDados.setOnClickListener {
            atualizarDadosPerfil(it)
            finish()
        }
    }

    private fun selecionarFotoGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityResultLauncher.launch(intent)
    }

    private fun atualizarDadosPerfil(view: View) {
        if (mSelecionarUri == null) {
            Log.e("Erro", "Nenhuma imagem selecionada")
            Snackbar.make(view, "Selecione uma imagem para continuar", Snackbar.LENGTH_SHORT).show()
            return
        }

        val nomeArquivo = UUID.randomUUID().toString()
        val reference: StorageReference = FirebaseStorage.getInstance().getReference("/imagens/$nomeArquivo")

        reference.putFile(mSelecionarUri!!).addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
            reference.downloadUrl.addOnSuccessListener { uri: Uri ->
                Log.i("url_img", uri.toString())
                val foto = uri.toString()

                val nome = binding.editNome.text.toString()
                if (nome.isEmpty() || foto.isEmpty()) {
                    Snackbar.make(view, "Selecione uma foto e digite o nome para atualizar os dados!", Snackbar.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val db = FirebaseFirestore.getInstance()
                val usuarios = hashMapOf<String, Any>(
                    "nome" to nome,
                    "foto" to foto
                )

                usuarioId = FirebaseAuth.getInstance().currentUser?.uid
                if (usuarioId != null) {
                    db.collection("Usuarios").document(usuarioId!!)
                        .update(usuarios)
                        .addOnCompleteListener { task ->
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
                            Log.e("Firestore Error", "Erro ao atualizar os dados: $e")
                            Snackbar.make(view, "Erro ao atualizar os dados!", Snackbar.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("Auth Error", "Usuário não autenticado")
                    Snackbar.make(view, "Erro ao obter o ID do usuário!", Snackbar.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e: Exception ->
                Log.e("storage_error", "Erro ao obter URL da imagem: $e")
                Snackbar.make(view, "Erro ao obter URL da imagem!", Snackbar.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e: Exception ->
            Log.e("upload_error", "Erro ao fazer upload da imagem: $e")
            Snackbar.make(view, "Erro ao fazer upload da imagem!", Snackbar.LENGTH_SHORT).show()
        }
    }
}
