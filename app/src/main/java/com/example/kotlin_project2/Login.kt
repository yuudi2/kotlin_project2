package com.example.kotlin_project2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        val login = findViewById<Button>(R.id.login)
        val signup = findViewById<Button>(R.id.signup)


        //회원가입 창 인텐트
        signup.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        //로그인
        login.setOnClickListener {
            if(email.text.isEmpty() && password.text.isEmpty()){
                Toast.makeText(this, "아이디와 비밀번호를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else{
                signIn(email.text.toString(), password.text.toString())
            }
        }
    }

    private fun signIn(email: String, password: String) {

        val intent = Intent(this, Main_activity::class.java)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                    finish()
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "정확한 아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?){

    }


    //자동 로그인
    public override fun onStart() {
        super.onStart()

        autologin(auth?.currentUser)

    }

    //현재 로그인한 사용자 가져오기
    private fun autologin(user: FirebaseUser?) {
        if(user != null){
            startActivity(Intent(this,Main_activity::class.java))
            finish()
        }
    }
}
