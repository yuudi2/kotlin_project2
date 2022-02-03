package com.example.kotlin_project2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlin_project2.Data.ChatData
import com.example.kotlin_project2.Data.Friend
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Chatting : AppCompatActivity() {

    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomUid: String? = null
    private var friendUid: String? = null
    private var uid: String? = null
    private var recyclerView: RecyclerView? = null


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        //메세지를 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        friendUid = intent.getStringExtra("frienduid")
        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = findViewById(R.id.message_recyclerview)

        val send_message = findViewById<ImageView>(R.id.send_message)
        val text = findViewById<TextView>(R.id.text)

        send_message.setOnClickListener {
            val chatData = ChatData()
            chatData.users.put(uid.toString(), true)
            chatData.users.put(friendUid!!, true)

            val comment = ChatData.Comment(uid, text.text.toString(), curTime)
            if (chatRoomUid == null) {
                send_message.isEnabled = false
                fireDatabase.child("chatrooms").push().setValue(chatData).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler().postDelayed({
                        fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                        text.text = null
                    }, 1000L)
                }
            } else {
                fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                text.text = null
            }
        }
        checkChatRoom()
    }

    private fun checkChatRoom() {
        fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        val chatData = item.getValue<ChatData>()
                        if (chatData?.users!!.containsKey(friendUid)) {
                            chatRoomUid = item.key
                            val send_message = findViewById<ImageView>(R.id.send_message)
                            send_message.isEnabled = true
                            recyclerView?.layoutManager = LinearLayoutManager(this@Chatting)
                            recyclerView?.adapter = RecyclerViewAdapter()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ChattingViewHolder>() {
        private val comments = ArrayList<ChatData.Comment>()
        private var friend : Friend? = null

        init {
            fireDatabase.child("users").child(friendUid.toString()).addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friend = snapshot.getValue<Friend>()
                    val name_friend = findViewById<TextView>(R.id.name_friend)
                    name_friend.text = friend?.name
                    getMessageList()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): ChattingViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false)

            return ChattingViewHolder(view)
        }

        fun getMessageList(){
            fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for (data in snapshot.children){
                        val item = data.getValue<ChatData.Comment>()
                        comments.add(item!!)
                    }
                    notifyDataSetChanged()
                    //메세지를 보낼 시 화면 맨 밑으로
                    recyclerView?.scrollToPosition(comments.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(
            holder: ChattingViewHolder, position: Int) {

            holder.message.textSize = 18F
            holder.message.text = comments[position].message
            holder.message_time.text = comments[position].time

            if (comments[position].uid.equals(uid)){ // -> 본인 채팅일때
                holder.message.setBackgroundResource(R.drawable.chat2)
                holder.message_name.visibility = View.INVISIBLE
                holder.message_main.gravity = Gravity.RIGHT
                holder.message_destination.visibility = View.INVISIBLE
            } else {
                Glide.with(holder.itemView.context)
                    .load(friend?.profileImageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.message_photo)
                holder.message_name.text = friend?.name
                holder.message_destination.visibility = View.VISIBLE
                holder.message_name.visibility = View.VISIBLE
                holder.message.setBackgroundResource(R.drawable.chat)
                holder.message_main.gravity = Gravity.LEFT
            }

        }

        inner class ChattingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val message : TextView = view.findViewById(R.id.message)
            val message_name : TextView = view.findViewById(R.id.message_name)
            val message_time : TextView = view.findViewById(R.id.message_time)
            val message_photo : ImageView = view.findViewById(R.id.message_photo)
            val message_destination : LinearLayout = view.findViewById(R.id.message_destination)
            val message_main : LinearLayout = view.findViewById(R.id.message_main)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }

}