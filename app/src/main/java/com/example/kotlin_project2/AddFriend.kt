package com.example.kotlin_project2

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlin_project2.Data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

private var imageUri: Uri? = null
private val fireStorage = FirebaseStorage.getInstance().reference
private val fireDatabase = FirebaseDatabase.getInstance().reference
private val user = Firebase.auth.currentUser


class AddFriend : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)


        val friend_email = findViewById<TextView>(R.id.addfriend_email)
        val friend_search = findViewById<Button>(R.id.find_friend)

        val friend_photo = findViewById<ImageView>(R.id.addfriend_img)
        val friend_name = findViewById<TextView>(R.id.addfriend_name)
        val addfriend = findViewById<Button>(R.id.addfriend_btn)

        val friend_layout = findViewById<LinearLayout>(R.id.friend_layout)
        val no_search = findViewById<TextView>(R.id.no_search)

        val insert_email = friend_email.text



        //친구찾기
        friend_search.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
//                user.clear()
                        for (data in snapshot.children) {
                            val item = data.getValue<User>()
                            if (item?.email.equals(insert_email.toString())) {
                                friend_layout.visibility = View.VISIBLE
                                no_search.visibility = View.GONE
                                friend_name.text = item?.name
                                Glide.with(getApplicationContext()).load(item?.profileImageUrl)
                                .apply(RequestOptions().circleCrop())
                                .into(friend_photo)
                                break
                            } else {
                                friend_layout.visibility = View.GONE
                                no_search.visibility = View.VISIBLE
//                                Toast.makeText(getApplicationContext(),"검색 결과가 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
        }
    }

}