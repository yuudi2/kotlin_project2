package com.example.kotlin_project2

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.kotlin_project2.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

private lateinit var auth: FirebaseAuth
lateinit var database: DatabaseReference


@Suppress("DEPRECATION")
class Register : AppCompatActivity() {
    private var imageUri : Uri? = null
    //이미지 등록
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == RESULT_OK) {
                val photo = findViewById<ImageView>(R.id.register_photo)
                imageUri = result.data?.data //이미지 경로 원본
                photo.setImageURI(imageUri) //이미지 뷰를 바꿈
                Log.d("이미지", "성공")
                Log.d("이미지 URL", imageUri.toString())
                } else {
                    Log.d("이미지", "실패")
                }
            }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference


        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        val photo = findViewById<ImageView>(R.id.register_photo)

        val name = findViewById<EditText>(R.id.register_name).text
        val email = findViewById<EditText>(R.id.register_email).text
        val password = findViewById<EditText>(R.id.register_password).text

        val signup = findViewById<Button>(R.id.register)

        var profileCheck = false


        photo.setOnClickListener{
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
            profileCheck = true
        }

        val intent = Intent(this, MainActivity::class.java)

        signup.setOnClickListener {
            if(email.isEmpty() || password.isEmpty() || name.isEmpty()){
                Toast.makeText(this, "정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                Log.d("Email", "$email, $password")
            }
            else{
                if(!profileCheck){
                    Toast.makeText(this,"프로필사진을 등록해주세요.", Toast.LENGTH_SHORT).show()
                }else{
                    auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                        .addOnCompleteListener(this) { task ->

                            if (task.isSuccessful) {
                                val user = Firebase.auth.currentUser
                                val userId = user?.uid
                                val userIdSt = userId.toString()

                                FirebaseStorage.getInstance()
                                    .reference.child("userImages").child("$userIdSt/photo").putFile(imageUri!!).addOnSuccessListener {

                                        var userProfile: Uri? = null
                                        FirebaseStorage.getInstance().reference.child("userImages").child("$userIdSt/photo").downloadUrl .addOnSuccessListener {
                                            userProfile = it
                                            Log.d("이미지 URL", "$userProfile")
                                            val user = User(email.toString(), name.toString(), userProfile.toString(), userIdSt)
                                            database.child("users").child(userId.toString()).setValue(user)
                                        }
                                    }
                                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                Log.e(TAG, "$userId")
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }

                        }
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun reload() {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }

}
