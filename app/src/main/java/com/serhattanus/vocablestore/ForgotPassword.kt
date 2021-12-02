package com.serhattanus.vocablestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        forgotPassword_button.setOnClickListener {
            val email = editText_forgotPassword_email.text.toString()
            if (email.isEmpty()){
                forgotPassword_email.error = "Email alanı boş geçilemez"
            }else{
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(this,"Emailinizi kontrol ediniz!",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this,"Bir sorun yaşandı. Lütfen tekrar deneyiniz!",Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}