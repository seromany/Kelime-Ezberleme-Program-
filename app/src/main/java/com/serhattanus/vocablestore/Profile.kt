package com.serhattanus.vocablestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*

class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser!!.email.toString()

        database.collection("Profil").whereEqualTo("kullaniciEmail",currentUser).get().addOnCompleteListener {
            if (it.isSuccessful){
                for (document in it.result!!){
                    val kullaniciAdi = document.data.getValue("kullaniciAdi")
                    profile_kullaniciAdi.setText(kullaniciAdi.toString())
                    val ezberlenecekK = document.data.getValue("ezberlenecekKelime")
                    profile_EzberlenecekKelime.setText(ezberlenecekK.toString())
                    val ezberlenenK = document.data.getValue("ezberlenenKelime")
                    profile_ezberlenenKelime.setText(ezberlenenK.toString())
                    profile_kullaniciEmail.setText(currentUser)
                    val tarih = document.data.getValue("tarih")
                    profile_kullaniciTarih.setText(tarih.toString())
                }
            }
        }

    }

}