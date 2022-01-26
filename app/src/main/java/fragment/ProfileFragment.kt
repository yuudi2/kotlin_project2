package fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlin_project2.Data.User
import com.example.kotlin_project2.MainActivity
import com.example.kotlin_project2.Main_activity
import com.example.kotlin_project2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {
    private var imageUri: Uri? = null
    private val fireStorage = FirebaseStorage.getInstance().reference
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private val user = Firebase.auth.currentUser
    private val uid = user?.uid.toString()

    private var auth : FirebaseAuth? = null
    companion object{
        fun newInstance() : ProfileFragment{
            return ProfileFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = Firebase.auth


        val change_name = view.findViewById<TextView>(R.id.change_name)
        val change_photo = view.findViewById<ImageView>(R.id.change_photo)
        val email = view.findViewById<TextView>(R.id.my_email)

        val log = view.findViewById<Button>(R.id.change)

        val logout = view.findViewById<Button>(R.id.logout)


        //프로필 구현
        fireDatabase.child("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue<User>()
                    println(userProfile)
                    Glide.with(requireContext()).load(userProfile?.profileImageUrl)
                        .apply(RequestOptions().circleCrop())
                        .into(change_photo)

                    change_name.text = userProfile?.name
                    email.text = userProfile?.email
                }
            })

        logout.setOnClickListener{
            // 로그인 화면으로
            val intent = Intent(view.context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth?.signOut()
        }

        return view
    }

}