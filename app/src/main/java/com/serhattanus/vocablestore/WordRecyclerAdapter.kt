package com.serhattanus.vocablestore

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.word_recycler_row.view.*

class WordRecyclerAdapter(var wordList:ArrayList<Word>, var wordState:String, var activity: Activity):RecyclerView.Adapter<WordRecyclerAdapter.WordViewHolder>() {

    private lateinit var database: FirebaseFirestore
    private lateinit var viewGroup: ViewGroup

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.word_recycler_row,parent,false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        database = FirebaseFirestore.getInstance()
        holder.itemView.textView_IngilizceKelime.text = wordList[position].ingilizceKelime
        holder.itemView.textView_TurkceAnlami.text = wordList[position].turkceAnlami
        val documentID = wordList[position].documentID
        holder.itemView.setOnClickListener {
            val intent = Intent(viewGroup.context,WordsIU::class.java)
            intent.putExtra("page","update")
            intent.putExtra("documentID",documentID)
            intent.putExtra("ingilizceKelime",wordList[position].ingilizceKelime)
            intent.putExtra("turkceAnlami",wordList[position].turkceAnlami)
            viewGroup.context.startActivity(intent)
            activity.finish()
        }
        holder.itemView.imageView_delete.setOnClickListener {
            database.collection("Kelimeler").document(documentID).delete()
            wordList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, wordList.size)
            holder.itemView.visibility = View.GONE
        }
        if (wordState == "ezberlenmedi"){
            holder.itemView.imageView_remove.isVisible = false
            holder.itemView.imageView_update.setOnClickListener {
                database.collection("Kelimeler").document(documentID).update("durum","ezberlendi")
                wordList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, wordList.size)
                holder.itemView.visibility = View.GONE
            }
        }else {
            holder.itemView.imageView_update.isVisible = false
            holder.itemView.imageView_remove.isVisible = true
            holder.itemView.imageView_remove.setOnClickListener {
                database.collection("Kelimeler").document(documentID).update("durum","ezberlenmedi")
                wordList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, wordList.size)
                holder.itemView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return  wordList.size
    }
}