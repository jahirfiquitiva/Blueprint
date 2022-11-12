package dev.jahir.blueprint.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.frames.extensions.views.findView

class HelpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val questionView: TextView? by itemView.findView(R.id.help_question)
    private val answerView: TextView? by itemView.findView(R.id.help_answer)

    fun bind(helpItem: Pair<String, String>) {
        questionView?.text = helpItem.first
        answerView?.text = helpItem.second
    }
}