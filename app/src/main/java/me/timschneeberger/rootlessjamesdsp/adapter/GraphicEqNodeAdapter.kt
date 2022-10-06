package me.timschneeberger.rootlessjamesdsp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import me.timschneeberger.rootlessjamesdsp.R
import me.timschneeberger.rootlessjamesdsp.model.GraphicEqNode
import me.timschneeberger.rootlessjamesdsp.model.GraphicEqNodeList

class GraphicEqNodeAdapter(var nodes: GraphicEqNodeList) :
    RecyclerView.Adapter<GraphicEqNodeAdapter.ViewHolder>() {

    var onItemsChanged: ((GraphicEqNodeAdapter) -> Unit)? = null
    var onItemClicked: ((GraphicEqNode, Int) -> Unit)? = null

    private val callback = object : ObservableList.OnListChangedCallback<ObservableArrayList<GraphicEqNode>>() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onChanged(sender: ObservableArrayList<GraphicEqNode>?) {
            this@GraphicEqNodeAdapter.notifyDataSetChanged()
            onItemsChanged()
        }

        override fun onItemRangeChanged(
            sender: ObservableArrayList<GraphicEqNode>?,
            positionStart: Int,
            itemCount: Int,
        ) {
            this@GraphicEqNodeAdapter.notifyItemRangeChanged(positionStart, itemCount)
            onItemsChanged()
        }

        override fun onItemRangeInserted(
            sender: ObservableArrayList<GraphicEqNode>?,
            positionStart: Int,
            itemCount: Int,
        ) {
            this@GraphicEqNodeAdapter.notifyItemRangeInserted(positionStart, itemCount)
            onItemsChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onItemRangeMoved(
            sender: ObservableArrayList<GraphicEqNode>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int,
        ) {
            this@GraphicEqNodeAdapter.notifyDataSetChanged()
            onItemsChanged()
        }

        override fun onItemRangeRemoved(
            sender: ObservableArrayList<GraphicEqNode>?,
            positionStart: Int,
            itemCount: Int,
        ) {
            this@GraphicEqNodeAdapter.notifyItemRangeRemoved(positionStart, itemCount)
            onItemsChanged()
        }
    }

    private fun onItemsChanged() {
        this.onItemsChanged?.invoke(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val freq: TextView
        val gain: TextView
        val deleteButton: Button

        init {
            freq = view.findViewById(R.id.freq)
            gain = view.findViewById(R.id.gain)
            deleteButton = view.findViewById(R.id.delete)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        nodes.addOnListChangedCallback(callback)
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        nodes.removeOnListChangedCallback(callback)
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_geq_node_list, viewGroup, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Reset view
        viewHolder.deleteButton.isEnabled = true

        // Set content
        viewHolder.freq.text = "${String.format("%.1f", nodes[position].freq)}Hz"
        viewHolder.gain.text = "${String.format("%.2f", nodes[position].gain)}dB"

        // Set click listeners
        viewHolder.deleteButton.setOnClickListener {
            nodes.removeAt(viewHolder.adapterPosition)
            viewHolder.deleteButton.isEnabled = false
        }

        viewHolder.itemView.setOnClickListener {
            onItemClicked?.invoke(nodes[viewHolder.adapterPosition], viewHolder.adapterPosition)
        }
    }

    override fun getItemCount() = nodes.size
}
