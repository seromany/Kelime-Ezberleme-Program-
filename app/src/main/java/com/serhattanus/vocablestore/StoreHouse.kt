package com.serhattanus.vocablestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_store_house.*

class StoreHouse : AppCompatActivity() {

    private lateinit var database: FirebaseFirestore

    private lateinit var recyclerViewAdapter: StoreRecyclerAdapter

    var wordList = ArrayList<StoreWord>()
    var newWordList = ArrayList<StoreWord>()

    private lateinit var aranmaSecenegi : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_house)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Hazinede Arama"

        database = FirebaseFirestore.getInstance()

        aranmaSecenegi = "İngilizce"

        database.collection("Store").addSnapshotListener{snapshot, exception ->
            if (exception != null){
                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if (snapshot != null){
                    if (!snapshot.isEmpty){
                        val documents = snapshot.documents
                        wordList.clear()
                        for (document in documents){
                            val ingilizceKelime = document.get("ingilizceKelime") as String
                            val turkceAnlami = document.get("turkceAnlami") as String
                            val kullaniciAdi = document.get("kullaniciAdi") as String

                            val indirilenKelime = StoreWord(ingilizceKelime, turkceAnlami,kullaniciAdi)
                            wordList.add(indirilenKelime)
                        }
                    }
                }
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

        recyclerViewAdapter = StoreRecyclerAdapter(wordList)
        search_recyclerview.layoutManager = LinearLayoutManager(this)
        search_recyclerview.adapter = recyclerViewAdapter

        editText_store_search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        
        searchable_ingilizceKelime.setOnClickListener {
            if (searchable_ingilizceKelime.isChecked){
                aranmaSecenegi = "İngilizce"
            }
        }

        searchable_turkceKelime.setOnClickListener {
            if (searchable_turkceKelime.isChecked){
                aranmaSecenegi = "Türkçe"
            }
        }

        searchable_kullaniciAdi.setOnClickListener {
            if (searchable_kullaniciAdi.isChecked){
                aranmaSecenegi = "Kullanıcı"
            }
        }

    }

    fun filter(text:String){
        println(aranmaSecenegi)
        newWordList.clear()
        for (word in wordList){
            if (aranmaSecenegi=="İngilizce"){
                if (word.ingilizceKelime.lowercase().contains(text.lowercase())){
                    newWordList.add(word)
                }
            }else if (aranmaSecenegi=="Türkçe"){
                if (word.turkceAnlami.lowercase().contains(text.lowercase())){
                    newWordList.add(word)
                }
            }else{
                if (word.kullaniciAdi.lowercase().contains(text.lowercase())){
                    newWordList.add(word)
                }
            }
        }
        recyclerViewAdapter.filterList(newWordList)
    }
}