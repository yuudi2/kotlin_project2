package fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlin_project2.Chatting
import com.example.kotlin_project2.Data.ChatData
import com.example.kotlin_project2.Data.Friend
import com.example.kotlin_project2.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class ChatFragment : Fragment() {
    companion object{
        fun newInstance() : ChatFragment{
            return ChatFragment()
        }
    }

    private val fireDatabase = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>(){

        private val chatModel = ArrayList<ChatData>()
        private var uid : String? = null
        private val destinationUsers : ArrayList<String> = arrayListOf()

        init {
            uid = Firebase.auth.currentUser?.uid.toString()

            fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModel.clear()
                    for (data in snapshot.children){
                        chatModel.add(data.getValue<ChatData>()!!)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val chat_photo : ImageView = itemView.findViewById(R.id.room_photo)
            val chat_name : TextView = itemView.findViewById(R.id.room_name)
            val chat_message : TextView = itemView.findViewById(R.id.room_message)

        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerViewAdapter.CustomViewHolder {
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.chatlist, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerViewAdapter.CustomViewHolder, position: Int) {
            var destinationUid : String?= null

            for (user in chatModel[position].users.keys){
                if (!user.equals(uid)){
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }

            fireDatabase.child("users").child("$destinationUid").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friend = snapshot.getValue<Friend>()
                    Glide.with(holder.itemView.context).load(friend?.profileImageUrl)
                        .apply(RequestOptions().circleCrop())
                        .into(holder.chat_photo)

                    holder.chat_name.text = friend?.name
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            //마지막 메세지
            val comment = TreeMap<String, ChatData.Comment>(reverseOrder())
            comment.putAll(chatModel[position].comments)
            val lastMessage = comment.keys.toTypedArray()[0]
            holder.chat_message.text = chatModel[position].comments[lastMessage]?.message


            holder.itemView.setOnClickListener{
                val intent = Intent(context, Chatting::class.java)
                intent.putExtra("frienduid", destinationUsers[position])
                context?.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return chatModel.size
        }
    }
}