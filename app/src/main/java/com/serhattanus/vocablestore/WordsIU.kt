package com.serhattanus.vocablestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_words_iu.*

class WordsIU : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words_iu)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()


        /*
        editText_englishWord.onFocusChangeListener = View.OnFocusChangeListener{view, b ->
            if (b){
                textInputLayout_englishWord.hint = "İngilizce Kelime"
            }else if(editText_email.text.toString()==""){
                textInputLayout_englishWord.hint = "İngilizce Kelimeyi Giriniz"
            }
        }

        editText_turkishWord.onFocusChangeListener = View.OnFocusChangeListener{view, b ->
            if (b){
                textInputLayout_turkishWord.hint = "Türkçe Kelime"
            }else if(editText_email.text.toString()==""){
                textInputLayout_turkishWord.hint = "Türkçe Kelimeyi Giriniz"
            }
        }*/

        val intent = intent
        val page = intent.getStringExtra("page")
        val ingilizceKelime = intent.getStringExtra("ingilizceKelime")
        val turkceAnlami = intent.getStringExtra("turkceAnlami")
        val documentID = intent.getStringExtra("documentID")

        if (page == "insert"){
            supportActionBar?.title = "Kelime Ekleme"
            iu_button.setText("EKLE")
            textView_iu.setText("Kayıt İşlemi")
        }else if(page == "update"){
            supportActionBar?.title = "Kelime Güncelleme"
            iu_button.setText("GÜNCELLE")
            editText_englishWord.setText(ingilizceKelime)
            editText_turkishWord.setText(turkceAnlami)
            textView_iu.setText("Güncelleme İşlemi")
        }

        iu_button.setOnClickListener {
            if (page == "insert"){
                val guncelKullanici = auth.currentUser!!.email.toString()
                val ingilizceKelime = editText_englishWord.text.toString()
                val turkceAnlami = editText_turkishWord.text.toString()
                val durum = "ezberlenmedi"

                val wordHashMap = hashMapOf<String,String>()
                wordHashMap.put("kullanici",guncelKullanici)
                wordHashMap.put("ingilizceKelime",ingilizceKelime)
                wordHashMap.put("turkceAnlami",turkceAnlami)
                wordHashMap.put("durum",durum)

                database.collection("Kelimeler").add(wordHashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        finish()
                    }
                }.addOnFailureListener { ex ->
                    Toast.makeText(applicationContext,ex.localizedMessage, Toast.LENGTH_LONG).show()
                }

                val storeWordHashMap = hashMapOf<String,String>()
                storeWordHashMap.put("ingilizceKelime",ingilizceKelime)
                storeWordHashMap.put("turkceAnlami",turkceAnlami)
                database.collection("Store").add(storeWordHashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        finish()
                    }
                }.addOnFailureListener { ex ->
                    Toast.makeText(applicationContext,ex.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }else if (page == "update"){
                val yeniIngilizceKelime = editText_englishWord.text.toString()
                val yeniTurkceAnlami = editText_turkishWord.text.toString()
                documentID?.let { it1 ->
                    database.collection("Kelimeler").document(it1).update("ingilizceKelime",yeniIngilizceKelime)
                    database.collection("Kelimeler").document(it1).update("turkceAnlami",yeniTurkceAnlami)
                }
                val intent = Intent(applicationContext,MainPage::class.java)
                startActivity(intent)
                finish()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu, menu)

        val menuItem = menu!!.findItem(R.id.action_search)
        menuItem.isVisible = false

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exit){
            auth.signOut()
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}