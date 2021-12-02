package com.serhattanus.vocablestore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.search_recycler_row.view.*
import kotlinx.android.synthetic.main.word_recycler_row.view.*

class StoreRecyclerAdapter(var wordList:ArrayList<StoreWord>): RecyclerView.Adapter<StoreRecyclerAdapter.StoreViewHolder>() {

    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var viewGroup: ViewGroup

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.search_recycler_row,parent,false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        holder.itemView.textView_IngilizceKelime_search.text = wordList[position].ingilizceKelime
        holder.itemView.textView_TurkceAnlami_search.text = wordList[position].turkceAnlami

        holder.itemView.imageView_ekle_search.setOnClickListener {
            val guncelKullanici = auth.currentUser!!.email.toString()
            val ingilizceKelime = wordList[position].ingilizceKelime
            val turkceAnlami = wordList[position].turkceAnlami
            val durum = "ezberlenmedi"

            val wordHashMap = hashMapOf<String,String>()
            wordHashMap.put("kullanici",guncelKullanici)
            wordHashMap.put("ingilizceKelime",ingilizceKelime)
            wordHashMap.put("turkceAnlami",turkceAnlami)
            wordHashMap.put("durum",durum)

            database.collection("Kelimeler").add(wordHashMap).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(viewGroup.context,"Kelime Başarıyla eklendi", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { ex ->
                Toast.makeText(viewGroup.context,ex.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    fun filterList(filteredList:ArrayList<StoreWord>){
        wordList = filteredList
        notifyDataSetChanged()
    }
}