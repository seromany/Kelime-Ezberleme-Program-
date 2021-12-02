package com.serhattanus.vocablestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var name : String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var againPassword : String

    private lateinit var PASSWORD_PATTERN : Pattern

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,18}$")

        editText_register_password.doOnTextChanged { text, start, before, count ->
            if (text!!.length<6 || text!!.length>18){
                textInputLayout_register_password.error = "Şifreniz 6 ile 18 karakter arasında olmalıdır"
            }else{
                textInputLayout_register_password.error = null
            }
        }

    }

    fun register(view: View){
        name = editText_register_userName.text.toString()
        email = editText_register_email.text.toString()
        againPassword = editText_againPassword.text.toString()
        password = editText_register_password.text.toString()
        if (name.isEmpty() || email.isEmpty() ||  password.isEmpty() || againPassword.isEmpty()) {
            Toast.makeText(this, "Tüm alanların doldurulması zorunludur", Toast.LENGTH_LONG).show()
        }
        else{
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                textInputLayout_register_email.error = "Lütfen geçerli bir email adresi giriniz"
            }else{
                textInputLayout_register_email.error = null
                if (password.length in 6..18){
                    if (!PASSWORD_PATTERN.matcher(password).matches()){
                        textInputLayout_register_password.error = "Şifreniz bir sayı, bir büyük harf, bir küçük harf ve bir özel karakter(@#\$%^&+=) içermelidir!"
                    }else{
                        if (password == againPassword){
                            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    val currentUser = auth.currentUser
                                    if (currentUser != null){
                                        currentUser.sendEmailVerification().addOnCompleteListener { t ->
                                            if (t.isSuccessful){
                                                Toast.makeText(applicationContext,"Email adresinize bir doğrulama mesajı gitmiştir. Lütfen emailinizi kontol ediniz.",Toast.LENGTH_LONG).show()
                                                finish()
                                            }
                                        }.addOnFailureListener{ex->
                                            Toast.makeText(applicationContext,ex.localizedMessage,Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                            }
                        }else {
                            Toast.makeText(
                                this,
                                "Şifre ve Tekrar şifre alanları uyuşmuyor!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

}