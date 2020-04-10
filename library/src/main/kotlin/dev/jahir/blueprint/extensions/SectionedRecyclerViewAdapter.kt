package dev.jahir.blueprint.extensions

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter

fun SectionedRecyclerViewAdapter<*>.safeNotifySectionChanged(section: Int) {
    try {
        notifySectionChanged(section)
    } catch (e: Exception) {
        notifyDataSetChanged()
    }
}