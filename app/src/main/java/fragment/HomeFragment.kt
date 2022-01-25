package fragment

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlin_project2.AddFriend
import com.example.kotlin_project2.Data.Friend
import com.example.kotlin_project2.Data.User
import com.example.kotlin_project2.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class HomeFragment : Fragment() {
    companion object {
        private var imageUri: Uri? = null
        private val fireStorage = FirebaseStorage.getInstance().reference
        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private val user = Firebase.auth.currentUser
        private val uid = user?.uid.toString()

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private lateinit var database: DatabaseReference
    private var friend: ArrayList<Friend> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val my_photo = view.findViewById<ImageView>(R.id.my_photo)
        val my_name = view.findViewById<TextView>(R.id.my_name)
        val addfriend = view.findViewById<ImageButton>(R.id.addfriend)

        val friend_recyclerview = view.findViewById<RecyclerView>(R.id.friend_recyclerview)
        database = Firebase.database.reference
        friend_recyclerview.layoutManager = LinearLayoutManager(requireContext())
        friend_recyclerview.adapter = RecyclerViewAdapter()


        addfriend.setOnClickListener {
            val intent = Intent(context, AddFriend::class.java)
            context?.startActivity(intent)
        }


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
                        .into(my_photo)

                    my_name.text = userProfile?.name
                }
            })

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("friends")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        friend.clear()
                        for (data in snapshot.children) {
                            val item = data.getValue<Friend>()
                            friend.add(item!!)
                        }
                        notifyDataSetChanged()
                    }
                })
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerViewAdapter.CustomViewHolder {
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.myfriend, parent, false))
        }

        inner class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
            val friends_name : TextView = itemView.findViewById(R.id.friends_name)
            val friends_photo : ImageView = itemView.findViewById(R.id.friends_photo)
        }

        override fun onBindViewHolder(holder: RecyclerViewAdapter.CustomViewHolder, position: Int) {
            holder.friends_name.text = friend[position].name

            Glide.with(holder.itemView.context).load(friend[position].profileImageUrl)
                .apply(RequestOptions().circleCrop())
                .into(holder.friends_photo)
        }

        override fun getItemCount(): Int {
            return friend.size
        }

    }

}