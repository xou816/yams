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
 * [DicesFragment.DiceInteractionListener] interface.
 */
class DicesFragment : Fragment() {

    var diceGroup = DiceGroup((1 until 6).map { DicesViewAdapter.makeDice() })

    private var listener: DiceInteractionListener? = null
    private var view: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_dices, container, false) as RecyclerView
        return view?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = DicesViewAdapter(diceGroup.dices, listener)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DiceInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement DiceInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface DiceInteractionListener {
        fun onDiceClicked(item: Dice)
    }
}
