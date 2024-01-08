package com.yashkumartech.groupgab.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.yashkumartech.groupgab.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Singleton


@IgnoreExtraProperties
data class Message(
    val message: String = "",
    val sender: String = "",
    val time: String = ""
)

@Singleton
class MessagesRepository {
    private val db = FirebaseDatabase.getInstance().reference

    fun getMessages(groupId: String, callback: (List<Message>) -> Unit) {
        db.child(groupId)
            .limitToLast(100)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = mutableListOf<Message>()
                    snapshot.children.forEach { child ->
                        val message = child.getValue(Message::class.java)
                        message?.let { messages.add(it) }
                    }
                    callback(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun sendMessage(username: String, messageText: String, groupId: String) {
        Log.d("SENDMESSAGE", "IN REPO")
        val message = Message(
            message = messageText,
            sender = username,
            time = System.currentTimeMillis().toString()
        )
        db.child(groupId).push().setValue(message)
    }
}