package com.example.googleapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginScreen : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    var firebaseAuth = FirebaseAuth.getInstance()
    lateinit var Signin:CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)


        // Configure Google Sign In inside onCreate mentod
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        Signin=findViewById(R.id.Signin)
        Signin.setOnClickListener{ view: View? ->
            signInGoogle()
        }

    }
        private  fun signInGoogle(){

            val signInIntent: Intent =mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent,Req_Code)
        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Req_Code){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }
    // handleResult() function -  this is where we update the UI after Google signin takes place
    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }
    }
    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    private fun UpdateUI(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                SavedPreference.setEmail(this,account.email.toString())
                SavedPreference.setUsername(this,account.displayName.toString())
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
// if you do not add this check, then you would have to login everytime you start your application on your phone.
}