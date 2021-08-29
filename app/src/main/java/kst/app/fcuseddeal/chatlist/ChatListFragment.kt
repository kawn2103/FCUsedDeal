package kst.app.fcuseddeal.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kst.app.fcuseddeal.DBKey.Companion.CHILD_CHAT
import kst.app.fcuseddeal.DBKey.Companion.DB_USERS
import kst.app.fcuseddeal.R
import kst.app.fcuseddeal.chatdetail.ChatRoomActivity
import kst.app.fcuseddeal.databinding.FragmentChatListBinding
import kst.app.fcuseddeal.home.ArticleAdapter

class ChatListFragment:Fragment(R.layout.fragment_chat_list) {
    private var binding: FragmentChatListBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private lateinit var chatDB: DatabaseReference
    private val chatRoomList = mutableListOf<ChatListItem>()
    private lateinit var chatListAdapter: ChatListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatListBinding = FragmentChatListBinding.bind(view)
        binding = fragmentChatListBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { chatroom ->
            //채팅방 이동
            context?.let {
                val intnet = Intent(it,ChatRoomActivity::class.java)
                intnet.putExtra("chatKey",chatroom.key)
                startActivity(intnet)
            }


        })

        chatRoomList.clear()

        fragmentChatListBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatListBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        if (auth.currentUser == null){
            return
        }

        chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid).child(CHILD_CHAT)
        chatDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }
                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }
}