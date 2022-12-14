package com.example.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var urlArl: ActivityResultLauncher<Intent>
    private lateinit var pegarImagemArl: ActivityResultLauncher<Intent>
    private lateinit var permissaoChamadaArl: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        supportActionBar?.subtitle = "MainActivity"

        //trata o retorno da segunda tela com a string do TextView
        urlArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {resultado: ActivityResult ->
            if(resultado.resultCode == RESULT_OK){
                val urlRetornada = resultado.data?.getStringExtra("URL") ?: ""
                amb.urlTv.text = urlRetornada
            }
        }

        //trata a Arl da ACTION_PICK para selecionar uma imagem e mostrar na tela
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

        //verifica se a permiss??o foi concedida pelo usu??rio
        permissaoChamadaArl = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            object: ActivityResultCallback<Boolean>{
                override fun onActivityResult(concedida: Boolean?) {
                    if(concedida!!){
                        chamarNumero(true)
                    }else{
                        Toast.makeText(this@MainActivity, "?? necess??rio permiss??o para executar", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        )

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

    //trata o item do menu que foi selecionado
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            //abre navegador com a Url especificada
            R.id.viewMi -> {
                val url = Uri.parse(amb.urlTv.text.toString())
                val navegadorIntent = Intent(ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }

            //abre o discador com o n??mero passado
            R.id.dialMi -> {
                chamarNumero(false)
                true
            }

            R.id.callMi -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        chamarNumero(true)
                    }else{
                        permissaoChamadaArl.launch(CALL_PHONE)
                    }
                }
                true
            }

            //seleciona arquivo de diret??rio especificado
            R.id.pickMi -> {
                val pegarImagemIntent = Intent(ACTION_PICK)

                val diretorioImagens = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path

                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens), "image/*")
                pegarImagemArl.launch(pegarImagemIntent)
                true
            }

            //escolher entre dois ou mais app's para executar
            R.id.chooserMi -> {
                val escolherAppIntent = Intent(ACTION_CHOOSER)
                val informacoesIntent = Intent(ACTION_VIEW, Uri.parse(amb.urlTv.text.toString()))

                escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador")
                escolherAppIntent.putExtra(EXTRA_INTENT, informacoesIntent)
                startActivity(escolherAppIntent)
                true
            }

            else -> {false}
        }
    }

    private fun chamarNumero(chamar: Boolean) {
        val uri = Uri.parse("tel: 99768-0077")
        val intent = Intent(if(chamar) ACTION_CALL else ACTION_DIAL, uri)
        startActivity(intent)
    }
}