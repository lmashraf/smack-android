package org.uhworks.smack.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.uhworks.smack.Model.Message
import org.uhworks.smack.R
import org.uhworks.smack.Services.UserDataService

class MessageAdapter(val context: Context, val messages: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder?.bindMessage(context, messages[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val userImage: ImageView = itemView?.findViewById(R.id.messageUserImage)
        private val timeStamp: TextView = itemView?.findViewById(R.id.messageTimestampLbl)
        private val userName: TextView = itemView?.findViewById(R.id.messageUserNameLbl)
        private val messageBody: TextView = itemView?.findViewById(R.id.messageBodyLbl)

        fun bindMessage(context: Context, message: Message) {

            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))

            userName?.text = message.userName
            timeStamp?.text = message.timeStamp
            messageBody?.text = message.message
        }
    }
}