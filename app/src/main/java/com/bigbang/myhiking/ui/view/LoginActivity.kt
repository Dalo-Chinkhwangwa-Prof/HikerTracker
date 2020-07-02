package com.bigbang.myhiking.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bigbang.myhiking.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if ((FirebaseAuth.getInstance().currentUser != null) && (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true)) {
            //LoggedIn and verified!
            openHomeActivity()
        } else {
            Toast.makeText(this, "User must log in!!!!!!", Toast.LENGTH_SHORT).show()
        }



        login.setOnClickListener {

            val email = username.text.toString().trim()
            val password = password.text.toString().trim()

            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true)
                            openHomeActivity()
                        else
                            verificaitonToast()

                    } else {
                        if (task.exception?.message?.contains("no user record") == true) {
                            //user does not exist create account
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                                email, password
                            ).addOnCompleteListener { signUpTask ->
                                if (signUpTask.isSuccessful) {
                                    //first time user - send a verification email and let the user know
                                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                    verificaitonToast()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "${signUpTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }


                            }


                        } else {
//                            wrong password o some other error
                            Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT)
                                .show()

                        }


                    }

                }

        }
    }

    private fun verificaitonToast() {
        Toast.makeText(this, "Please verify email - link sent", Toast.LENGTH_SHORT).show()
    }

    private fun openHomeActivity() {
        startActivity(Intent(this, MainActivity::class.java).also {
            it.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
    }
}