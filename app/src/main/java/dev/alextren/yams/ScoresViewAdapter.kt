package dev.alextren.yams

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.single_score.view.*

class ScoresViewAdapter(
    private val scores: MutableList<Score>,
    private val listener: ScoreInteractionListener?
) : RecyclerView.Adapter<ScoresViewAdapter.ViewHolder>() {

    fun updateScores(newScores: List<Score>) {
        scores.clear()
        scores.addAll(newScores)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = scores[position]
        if (item is ScoreImpl) {
            item.viewHolder = holder
        }
        holder.itemView.setOnClickListener {
            listener?.onScoreClicked(item)
        }
    }

    override fun getItemCount(): Int = scores.size

    companion object {
        fun makeScore(name: String): Score = ScoreImpl(name)
    }

    class ScoreImpl(override val name: String = "?") : Score {

        var viewHolder: ViewHolder? = null
            set(viewHolder) {
                field = viewHolder
                viewHolder!!.setScore(name, value, locked)
            }

        override var value: Int = 0
            set(value) {
                field = value
                viewHolder?.setScore(name, value, locked)
            }

        override var locked: Boolean = false
            set(locked) {
                field = locked
                viewHolder?.setScore(name, value, locked)
            }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val scoreName: TextView = itemView.score_name
        private val scoreValue: TextView = itemView.score_value

        fun setScore(name: String, value: Int, locked: Boolean) {
            scoreName.text = name
            scoreValue.text = value.toString()
            if (locked) disable() else enable()
        }

        fun enable() {
            scoreName.paintFlags = scoreName.paintFlags.and(Paint.STRIKE_THRU_TEXT_FLAG.inv())
            scoreName.isEnabled = true
        }

        fun disable() {
            scoreName.paintFlags = scoreName.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
            scoreName.isEnabled = false
        }
    }

    interface ScoreInteractionListener {
        fun onScoreClicked(item: Score)
    }
}
