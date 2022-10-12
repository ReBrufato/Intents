package com.example.intents

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var urlArl: ActivityResultLauncher<Intent>
    private lateinit var pegarImagemArl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        supportActionBar?.subtitle = "MainActivity"

        urlArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {resultado: ActivityResult ->
            if(resultado.resultCode == RESULT_OK){
                val urlRetornada = resultado.data?.getStringExtra("URL") ?: ""
                amb.urlTv.text = urlRetornada
            }
        }

        pegarImagemArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){resultado: ActivityResult ->
            if(resultado.resultCode == RESULT_OK){

                var imagemUri = resultado.data?.data

                imagemUri?.let{
                    amb.urlTv.text = it.toString()
                }

                val visualizadorImagemIntent = Intent(ACTION_VIEW, imagemUri)

                startActivity(visualizadorImagemIntent)
            }
        }

        amb.entrarUrlBt.setOnClickListener{
            val urlActivityIntent = Intent(this, UrlActivity::class.java)
            urlActivityIntent.putExtra("URL", amb.urlTv.text)
            urlArl.launch(urlActivityIntent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.viewMi -> {
                val url = Uri.parse(amb.urlTv.text.toString())
                val navegadorIntent = Intent(ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }

            R.id.dialMi -> {
                val url = Uri.parse("tel: +55 (16)99768-0070")
                val discadorIntent = Intent(ACTION_DIAL, url)
                startActivity(discadorIntent)
                true
            }

            R.id.callMi -> {
                Toast.makeText(this, "callMi", Toast.LENGTH_SHORT)
                true
            }

            R.id.pickMi -> {
                //'ACTION_PICK' diz ao SO que será feita uma ação de pegar algo, como arquivo, imagem(ns), etc..
                val pegarImagemIntent = Intent(ACTION_PICK)
                //obtém um diretório público externo que contenha imagens
                val diretorioImagens = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                //o método 'setDataAndType()' espera um 'data', que é uma Uri e o tipo de dado que eu quero recuperar (no caso é uma imagem, podendo ser de qualquer tipo),
                //nesse caso estou convertendo o path da imagem em uma Uri e especificando que a imagem pode ser de qualquer tipo
                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens), "image/*")
                pegarImagemArl.launch(pegarImagemIntent)
                true
            }

            R.id.chooserMi -> {
                Toast.makeText(this, "chooserMi", Toast.LENGTH_SHORT)
                true
            }

            else -> {false}
        }
    }
}