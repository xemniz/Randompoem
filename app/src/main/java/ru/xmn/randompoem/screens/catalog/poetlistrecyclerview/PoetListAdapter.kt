package ru.xmn.randompoem.screens.catalog.poetlistrecyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_poet.view.*
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.extensions.inflate
import ru.xmn.randompoem.common.extensions.isChecked
import ru.xmn.randompoem.common.ui.adapter.AutoUpdatableAdapter
import ru.xmn.randompoem.poetcommonbuisnesslogic.isEyeCrossed
import ru.xmn.randompoem.screens.catalog.SelectablePoet
import kotlin.properties.Delegates


class PoetListAdapter(val unignorePoet: (String) -> Unit, val ignorePoet: (String) -> Unit) : RecyclerView.Adapter<PoetListAdapter.ViewHolder>(), AutoUpdatableAdapter {
    var poets by Delegates.observable<List<SelectablePoet>>(emptyList())
    { property, oldValue, newValue ->
        autoNotify(
                oldValue,
                newValue,
                { a, b -> a.poet == b.poet }
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.create(parent)

    override fun getItemCount(): Int = poets.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(poets[position], unignorePoet, ignorePoet)


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun create(parent: ViewGroup) = ViewHolder(parent.inflate(R.layout.item_poet))
        }

        fun bind(selectablePoet: SelectablePoet, unignorePoet: (String) -> Unit, ignorePoet: (String) -> Unit) {
            itemView.poetName.text = selectablePoet.poet.name
            itemView.eye.isEyeCrossed = selectablePoet.ignored
            itemView.setOnClickListener {
                if (selectablePoet.ignored) unignorePoet(selectablePoet.poet.id) else ignorePoet(selectablePoet.poet.id)
            }

        }
    }

}
