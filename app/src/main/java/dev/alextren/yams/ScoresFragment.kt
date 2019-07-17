package dev.alextren.yams

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ScoresFragment.ScoreInteractionListener] interface.
 */
class ScoresFragment : Fragment() {

    val scores: MutableList<Score> = mutableListOf()

    fun updateScores(newScores: List<Score>) {
        scores.clear()
        scores.addAll(newScores)
        (view as RecyclerView).adapter?.notifyDataSetChanged()
    }

    private var listener: ScoreInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_possible_scores, container, false) as RecyclerView
        return view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ScoresViewAdapter(scores, listener)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ScoreInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ScoreInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface ScoreInteractionListener {
        fun onScoreClicked(item: Score)
    }
}
