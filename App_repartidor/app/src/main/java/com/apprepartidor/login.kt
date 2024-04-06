package com.apprepartidor

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprepartidor.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.facebook.FacebookSdk;


@Suppress("DEPRECATION")
class login : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val callbackManager = CallbackManager.Factory.create()
    private val GOOGLE_SING_IN = 100
    private lateinit var auth: FirebaseAuth
    private val binding : ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        firebaseAuth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botonabrir = findViewById<TextView>(R.id.textView7)

        botonabrir.setOnClickListener{
            val intent = Intent(this, registrarse::class.java)
            startActivity(intent)
        }

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Integración de FireBase completa")
        analytics.logEvent("InitScreen", bundle)

        //setup
        setup()
        session()
    }


    override fun onStart() {
        super.onStart()

    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if(email != null && provider != null){

            showHome(email, ProviderType.valueOf(provider))
        }

    }

    private fun setup(){

        title = "Autenticacion"

        val botonabrir2 = findViewById<TextView>(R.id.button3)
        val emailText = findViewById<TextView>(R.id.emailtext)
        val passwordText = findViewById<TextView>(R.id.passwordtext)

        botonabrir2.setOnClickListener{

            if (emailText.text.isNotEmpty() && passwordText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailText.text.toString(),
                        passwordText.text.toString()).addOnCompleteListener {
                            if(it.isSuccessful){
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            }else{
                                showAlert()
                            }
                        }
            }

        }
        val googlebtn = findViewById<TextView>(R.id.buttonG)
        googlebtn.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("207123066809-lf8emgknefeb1b8ncanccrrs7kdddo43.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SING_IN)
        }

        val facebookbtn = findViewById<TextView>(R.id.buttonF)
        facebookbtn.setOnClickListener{

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
              object : FacebookCallback<LoginResult> {

                  override fun onSuccess(result: LoginResult) {
                      result?.let {
                          val token = it.accessToken
                          val credential = FacebookAuthProvider.getCredential(token.token)
                          FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                              if (it.isSuccessful) {
                                  showHome(it.result?.user?.email ?: "", ProviderType.FACEBOOK)
                              } else {
                                  showAlert()
                              }
                          }
                      }
                  }

                  override fun onCancel() {

                  }

                  override fun onError(error: FacebookException) {
                        showAlert()
                  }
              })

        }

    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){

        val intent = Intent(this, principal::class.java).apply {
           putExtra("email", email)
           putExtra("provider", provider.name)

        }
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SING_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        } else {
                            showAlert()
                        }
                    }
                }
            }catch (e: ApiException){
                    showAlert()
            }
        }
    }
}