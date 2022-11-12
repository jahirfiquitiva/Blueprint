package dev.jahir.blueprint.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.extensions.sendEmail
import dev.jahir.blueprint.ui.adapters.HelpAdapter
import dev.jahir.blueprint.ui.decorations.GridDividerItemDecoration
import dev.jahir.frames.data.Preferences
import dev.jahir.frames.extensions.context.findView
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.context.stringArray
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.extensions.views.tint
import dev.jahir.frames.ui.activities.base.BaseSearchableActivity

@Suppress("RemoveExplicitTypeArguments")
class HelpActivity : BaseSearchableActivity<Preferences>() {

    private val adapter: HelpAdapter by lazy { HelpAdapter() }

    private val helpItems: ArrayList<Pair<String, String>> by lazy {
        val questions = stringArray(R.array.questions)
        val answers = stringArray(R.array.answers)
        try {
            ArrayList<Pair<String, String>>(questions.mapIndexed { i, s -> Pair(s, answers[i]) })
        } catch (e: Exception) {
            ArrayList<Pair<String, String>>()
        }
    }

    override val preferences: Preferences by lazy { Preferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icons_category)

        val toolbar: Toolbar? by findView(R.id.toolbar)
        setSupportActionBar(toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        toolbar?.title = string(R.string.help)
        toolbar?.tint()

        val recyclerView: FastScrollRecyclerView? by findView(R.id.recycler_view)
        recyclerView?.tint()
        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView?.addItemDecoration(
            GridDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )
        recyclerView?.adapter = adapter.apply { submitList(helpItems) }
        recyclerView?.setHasFixedSize(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        else if (item.itemId == R.id.contact) {
            val email = string(R.string.email)
            if (email.hasContent()) sendEmail(email, "${getAppName()} Support")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getMenuRes(): Int = R.menu.help_menu

    override fun internalDoSearch(filter: String, closed: Boolean) {
        super.internalDoSearch(filter, closed)
        if (filter.hasContent() && !closed) {
            adapter.submitList(ArrayList(ArrayList(helpItems).filter {
                it.first.lower().contains(filter.lower()) || it.second.lower()
                    .contains(filter.lower())
            }))
        } else {
            adapter.submitList(helpItems)
        }
    }
}