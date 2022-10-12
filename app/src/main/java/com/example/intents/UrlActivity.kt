package com.example.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.intents.databinding.ActivityUrlBinding

class UrlActivity : AppCompatActivity() {

    private val aub: ActivityUrlBinding by lazy{
        ActivityUrlBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(aub.root)

        supportActionBar?.subtitle = "UrlActivity"

        val urlAnterior = intent.getStringExtra("URL") ?: ""

        if (urlAnterior.isNotEmpty()){
            aub.urlEt.setText(urlAnterior)
        }

        Toast.makeText(this, "Estudo Intents",Toast.LENGTH_SHORT).show()

        aub.entrarUrlBt.setOnClickListener {
            val retornoIntent = Intent()

            retornoIntent.putExtra("URL", aub.urlEt.text.toString())

            setResult(RESULT_OK, retornoIntent)
            finish()
        }

    }
}