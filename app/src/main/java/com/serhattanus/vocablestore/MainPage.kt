package com.serhattanus.vocablestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main_page.*
import java.text.SimpleDateFormat
import java.util.*

class MainPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        val wordHashMap = hashMapOf<String,Any>()
        wordHashMap.put("kullaniciEmail",currentUser!!.email.toString())
        wordHashMap.put("tarih",currentDateAndTime)
        wordHashMap.put("kullaniciAdi","ahmet")
        wordHashMap.put("ezberlenecekKelime",0)
        wordHashMap.put("ezberlenenKelime",0)

        database.collection("Profil")
            .whereEqualTo("kullaniciEmail",currentUser!!.email.toString())
            .addSnapshotListener{snapshot, exception ->
            if (exception != null){
                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }else if (snapshot?.isEmpty==true){
                database.collection("Profil").add(wordHashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(applicationContext,"Bilgileriniz veritabanına başarıtla kaydedildi", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { ex ->
                    Toast.makeText(applicationContext,ex.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        supportActionBar?.title = "Anasayfa"

        ezberlenecek_button.setOnClickListener {
            val intent = Intent(applicationContext,Words::class.java)
            intent.putExtra("wordState","ezberlenmedi")
            startActivity(intent)
        }

        profile_button.setOnClickListener {
            val intent = Intent(applicationContext,Profile::class.java)
            startActivity(intent)
        }

        ezberlenen_button.setOnClickListener {
            val intent = Intent(applicationContext,Words::class.java)
            intent.putExtra("wordState","ezberlendi")
            startActivity(intent)
        }

        kelimeEkle_button.setOnClickListener {
            val intent = Intent(applicationContext,WordsIU::class.java)
            intent.putExtra("page","insert")
            startActivity(intent)
        }

        storeHouse_button.setOnClickListener {
            val intent = Intent(applicationContext,StoreHouse::class.java)
            startActivity(intent)
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