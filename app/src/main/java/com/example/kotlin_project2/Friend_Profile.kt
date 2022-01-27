package com.example.kotlin_project2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlin_project2.Data.ChatData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Friend_Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        val friend_photo = findViewById<ImageView>(R.id.friend_photo)
        val friend_name = findViewById<TextView>(R.id.friend_name)
        val chatting = findViewById<Button>(R.id.friend_chatting)

        val back = findViewById<ImageButton>(R.id.back)

        Glide.with(getApplicationContext()).load(intent.getStringExtra("img"))
            .apply(RequestOptions().circleCrop())
            .into(friend_photo)

        friend_name.text = intent.getStringExtra("name")
        val frienduid : String = intent.getStringExtra("frienduid").toString()

        val intent = Intent(applicationContext, Chatting::class.java)
        chatting.setOnClickListener {

            intent.putExtra("frienduid", frienduid)
            startActivity(intent)
            finish()
        }

        back.setOnClickListener {
            finish()
        }
    }
}