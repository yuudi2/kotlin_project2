package com.example.kotlin_project2.Data

import org.w3c.dom.Comment

class ChatData (
    val users: HashMap<String, Boolean> = HashMap(),
    val comment: HashMap<String, Comment> = HashMap()){
    class Comment(val uid: String? = null, val message: String? = null, val time: String? = null)
}