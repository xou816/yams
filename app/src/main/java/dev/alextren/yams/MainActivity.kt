package dev.alextren.yams

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.score_card.*

class MainActivity :
    AppCompatActivity(),
    DicesFragment.DiceInteractionListener,
    ScoresFragment.ScoreInteractionListener {

    private var currentThrow = 3
    private val scoreboard = Scoreboard { ScoresViewAdapter.makeScore(it) }

    fun updateThrowCount(newThrow: Int) {
        currentThrow = newThrow
        throw_textview.text = newThrow.toString()
        if (newThrow == 0) {
            fab.hide()
        } else {
            fab.show()
        }
    }

    fun updateScore(score: Int) {
        score_total.text = score.toString()
    }

    override fun onScoreClicked(item: Score) {
        if (getDicesFragment().diceGroup.hasBeenThrown() && !item.locked) {
            updateScore(scoreboard.lockScore(item))
            getDicesFragment().diceGroup.deselectAll()
            updateThrowCount(3)
            getPossibleScoresFragment()
                .updateScores(scoreboard.getScores())
        }
    }

    override fun onDiceClicked(item: Dice) {
        window.decorView.rootView.run {
            Toast.makeText(context, if (item.isSelected()) "Dice locked" else "Unlocked", Toast.LENGTH_SHORT).show()
        }
    }

    fun onThrowDicesClicked() {
        getDicesFragment().diceGroup.run {
            rollDices {
                updateThrowCount((currentThrow - 1) % 4)
                getPossibleScoresFragment()
                    .updateScores(scoreboard.updatedScores(this))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        getPossibleScoresFragment()
            .updateScores(scoreboard.getScores())

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

    private fun getDicesFragment() = fragment_dices as DicesFragment
    private fun getPossibleScoresFragment() = fragment_possible_scores as ScoresFragment
}
