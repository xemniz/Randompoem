package ru.xmn.randompoem.screens

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ru.xmn.filmfilmfilm.common.ui.adapter.AutoUpdatableAdapter
import ru.xmn.randompoem.R
import kotlinx.android.synthetic.main.item_poem.view.itemPoemText
import ru.xmn.randompoem.common.extensions.inflate
import ru.xmn.randompoem.model.Poem
import kotlin.properties.Delegates

class AdapterRandomPoems : RecyclerView.Adapter<AdapterRandomPoems.ViewHolder>(), AutoUpdatableAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_poem))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    var items: List<Poem> by Delegates.observable(emptyList()) {
        _, old, new ->
        autoNotify(old, new) { o, n -> o == n }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(poem: Poem) {
            itemView.itemPoemText.text = poem.text
        }
    }

}