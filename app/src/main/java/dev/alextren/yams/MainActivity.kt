package dev.alextren.yams

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dice_area.*
import kotlinx.android.synthetic.main.score_card.*

class MainActivity :
    AppCompatActivity(),
    DicesViewAdapter.DiceInteractionListener,
    ScoresViewAdapter.ScoreInteractionListener {

    private var currentThrow = 3
    private val scoreboard = Scoreboard { ScoresViewAdapter.makeScore(it) }
    private val diceGroup = DiceGroup((1 until 6).map { DicesViewAdapter.makeDice() })

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
        if (diceGroup.hasBeenThrown() && !item.locked) {
            updateScore(scoreboard.lockScore(item))
            diceGroup.deselectAll()
            updateThrowCount(3)
            updateScores(scoreboard.getScores())
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
                updateThrowCount((currentThrow - 1) % 4)
                updateScores(scoreboard.updatedScores(this))
            }
        }
    }

    fun updateScores(scores: List<Score>) {
        (score_list.adapter as ScoresViewAdapter).updateScores(scores)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        MutableLiveData<String>().apply {
            postValue("eee")
        }.observe(this, Observer<String> {

        })

        score_list.let {
            it.layoutManager = LinearLayoutManager(it.context)
            it.adapter = ScoresViewAdapter(mutableListOf(), this)
        }

        dice_list.let {
            it.layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = DicesViewAdapter(diceGroup.dices, this)
        }

        updateScores(scoreboard.getScores())

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
}
