package dev.alextren.yams

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dice_area.*
import kotlinx.android.synthetic.main.score_card.*

class MainActivity :
    AppCompatActivity(),
    DicesViewAdapter.DiceInteractionListener,
    ScoresViewAdapter.ScoreInteractionListener {

    private lateinit var scoreBoard: ScoreBoard
    private lateinit var remainingRolls: RemainingRolls
    private val diceGroup = DiceGroup((1 until 6).map { DicesViewAdapter.makeDice() })

    override fun onScoreClicked(item: Score) {
        if (diceGroup.hasBeenThrown() && !item.locked) {
            scoreBoard.lockScore(item)
            diceGroup.deselectAll()
            remainingRolls.restore()
            fab.show()
            scoreBoard.refreshScores()
        }
    }

    override fun onDiceClicked(item: Dice) {
        window.decorView.rootView.run {
            Toast.makeText(context, if (item.isSelected()) "Dice locked" else "Unlocked", Toast.LENGTH_SHORT).show()
        }
    }

    fun onThrowDicesClicked() {
        diceGroup.run {
            rollDices {
                remainingRolls.decrement()
                scoreBoard.scoresForDices(this)
                if (remainingRolls.canRoll()) fab.show() else fab.hide()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        remainingRolls = RemainingRollsImpl(throw_textview)

        score_list.let {
            it.layoutManager = LinearLayoutManager(it.context)
            it.adapter = ScoresViewAdapter(mutableListOf(), this).also {
                scoreBoard = ScoreBoardImpl(score_total, it)
                scoreBoard.refreshScores()
            }
        }

        dice_list.let {
            it.layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = DicesViewAdapter(diceGroup.dices, this)
        }

        fab.setOnClickListener {
            onThrowDicesClicked()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    class ScoreBoardImpl(val scoreView: TextView, val scoresViewAdapter: ScoresViewAdapter) : ScoreBoard (ScoresViewAdapter.Companion::makeScore) {

        override fun setScores(newScores: List<Score>) {
            scoresViewAdapter
                .apply {
                    scores.clear()
                    scores.addAll(newScores)
                }
                .notifyDataSetChanged()
        }

        override fun setTotalScore(total: Int) {
            scoreView.text = total.toString()
        }

    }

    class RemainingRollsImpl(val textView: TextView) : RemainingRolls {

        var counter: Int = 3

        override fun decrement() {
            counter = (counter - 1) % 4
            textView.text = counter.toString()
        }

        override fun restore() {
            counter = 3
            textView.text = counter.toString()
        }

        override fun canRoll() = counter > 0

    }
}
