package com.serhattanus.vocablestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_words.*
import java.util.*
import kotlin.collections.ArrayList

class Words : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var recyclerViewAdapter: WordRecyclerAdapter

    var wordList = ArrayList<Word>()
    var newWordList = ArrayList<Word>()
    private lateinit var wordState : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        val intent = intent
        if (intent.getStringExtra("wordState") != null) wordState = intent.getStringExtra("wordState")!!
        else wordState = ""

        if (wordState == "ezberlenmedi") supportActionBar?.title = "Ezberlenecek Kelimeler"
        else supportActionBar?.title = "Ezberlenen Kelimeler"

        recyclerViewAdapter = WordRecyclerAdapter(newWordList,wordState,this)
        words_recyclerview.layoutManager = LinearLayoutManager(this)
        words_recyclerview.adapter = recyclerViewAdapter

        if (!wordState.isEmpty()) getData()
    }

    fun getData(){
        val guncelKullanici = auth.currentUser!!.email.toString()
        database.collection("Kelimeler").whereEqualTo("kullanici",guncelKullanici).addSnapshotListener{snapshot, exception ->
            if (exception != null){
                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if (snapshot != null){
                    if (!snapshot.isEmpty){
                        val documents = snapshot.documents
                        newWordList.clear()
                        wordList.clear()
                        for (document in documents){
                            val kullanici = document.get("kullanici") as String
                            val ingilizceKelime = document.get("ingilizceKelime") as String
                            val turkceAnlami = document.get("turkceAnlami") as String
                            val documentID = document.id
                            val durum = document.get("durum") as String

                            if (wordState == durum){
                                val indirilenKelime = Word(documentID, kullanici, ingilizceKelime, turkceAnlami, durum)
                                wordList.add(indirilenKelime)
                            }
                        }
                    }
                }
                newWordList.addAll(wordList)
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu, menu)

        val menuItem = menu!!.findItem(R.id.action_search)
        if (menuItem != null){
            val searcView = menuItem.actionView as SearchView
            searcView.queryHint = "İngilizce kelimeyi yazınız"
            searcView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()){
                        newWordList.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        wordList.forEach {
                            if (it.ingilizceKelime.lowercase(Locale.getDefault()).contains(search)){
                                newWordList.add(it)
                            }
                        }
                        recyclerViewAdapter.notifyDataSetChanged()
                    }else{
                        newWordList.clear()
                        newWordList.addAll(wordList)
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }

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