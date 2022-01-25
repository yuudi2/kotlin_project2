package com.example.kotlin_project2

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlin_project2.Data.Friend
import com.example.kotlin_project2.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import fragment.HomeFragment
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import java.io.File


private lateinit var auth: FirebaseAuth
lateinit var databases: DatabaseReference


private val fireStorage = FirebaseStorage.getInstance().reference
private val fireDatabase = FirebaseDatabase.getInstance().reference
private val user = Firebase.auth.currentUser
private val uid = user?.uid.toString()


@Suppress("DEPRECATION")
class AddFriend : AppCompatActivity() {
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        auth = FirebaseAuth.getInstance()
        databases = Firebase.database.reference


        val friend_email = findViewById<TextView>(R.id.addfriend_email)
        val friend_search = findViewById<Button>(R.id.find_friend)

        val friend_photo = findViewById<ImageView>(R.id.addfriend_img)
        val friend_name = findViewById<TextView>(R.id.addfriend_name)
        val addfriend = findViewById<Button>(R.id.addfriend_btn)

        val friend_layout = findViewById<LinearLayout>(R.id.friend_layout)
        val no_search = findViewById<TextView>(R.id.no_search)

        val insert_email = friend_email.text

        val intent = Intent(this, Main_activity::class.java)

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

                                imageUri = Uri.parse(item?.profileImageUrl)


                                //friends database에 저장
                                addfriend.setOnClickListener {

                                    val friends = Friend(
                                        item?.email.toString(),
                                        item?.name.toString(),
                                        imageUri.toString(),
                                        item?.uid
                                    )
                                    databases.child("users").child(uid).child("friends").child(item?.uid.toString())
                                        .setValue(friends)
                                    startActivity(intent)
                                }

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