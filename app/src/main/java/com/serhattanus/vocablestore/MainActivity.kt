package com.serhattanus.vocablestore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 120
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleWith_button.setOnClickListener {
            signIn()
        }

        supportActionBar?.hide()

        main_forgotPassword.setOnClickListener {
            val intent = Intent(applicationContext,ForgotPassword::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()

        val guncelKullanici = auth.currentUser
        if (guncelKullanici != null && guncelKullanici.isEmailVerified){
            val intent = Intent(applicationContext,MainPage::class.java)
            startActivity(intent)
            finish()
        }

        editText_email.onFocusChangeListener  = View.OnFocusChangeListener { view, b ->
            if (b){
                textInputLayout_email.hint = "Email"
            }else if(editText_email.text.toString()==""){
                textInputLayout_email.hint = "example@gmail.com"
            }
        }
        editText_password.doOnTextChanged { text, start, before, count ->
            if (text!!.length<6 || text!!.length>18){
                textInputLayout_password.error = "Şifreniz 6 ile 18 karakter arasında olmalıdır"
            }else{
                textInputLayout_password.error = null
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("MainPage", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("MainPage", "Google sign in failed", e)
                }
            }else{
                Log.w("MainPage",exception.toString())
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainPage", "signInWithCredential:success")
                    val intent = Intent(applicationContext,MainPage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainPage", "signInWithCredential:failure", task.exception)
                }
            }
    }

    fun girisYap(view: View){
        if (editText_password.text!!.isEmpty() || editText_email.text!!.isEmpty()){
            Toast.makeText(applicationContext,"Kullanıcı adı ve/veya şifre boş geçilemez",Toast.LENGTH_LONG).show()
        }else{
            auth.signInWithEmailAndPassword(editText_email.text.toString(),editText_password.text.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val guncelKullanici = auth.currentUser
                    if (guncelKullanici != null && guncelKullanici.isEmailVerified){
                        Toast.makeText(this,"Hoşgeldin ${guncelKullanici.email.toString()}",Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext,MainPage::class.java)
                        startActivity(intent)
                        finish()
                    }else if(guncelKullanici != null){
                        guncelKullanici.sendEmailVerification()
                        Toast.makeText(
                            this,
                            "Giriş yapabilmeniz için mailinizi doğrulamanız gerekmektedir. Lütfen mailinizi kontrol ediniz.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun goToRegisterPage(view: View){
        val intent = Intent(applicationContext,Register::class.java)
        startActivity(intent)
    }
}