package dev.alextren.yams

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import dev.alextren.yams.DicesFragment.DiceInteractionListener
import kotlinx.android.synthetic.main.fragment_single_dice.view.*
import kotlin.random.Random

class DicesViewAdapter(
    private val dices: List<Dice>,
    private val listener: DiceInteractionListener?
) : RecyclerView.Adapter<DicesViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_single_dice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dices[position]
        if (item is DiceImpl) {
            item.viewHolder = holder
        }

        holder.itemView.setOnClickListener {
            if (item.getValue() != 0) {
                item.toggleSelection()
                listener?.onDiceClicked(item)
            }
        }
    }

    override fun getItemCount(): Int = dices.size

    companion object {
        fun makeDice(): Dice = DiceImpl()
    }

    class DiceImpl(private var value: Int = 0, private var selected: Boolean = false) : Dice {

        var viewHolder: ViewHolder? = null
            set(viewHolder) {
                field = viewHolder
                updateView()
            }

        override fun getValue() = value

        override fun isSelected() = selected

        override fun toggleSelection() {
            selected = !selected
            updateView()
        }

        override fun resetDice() {
            value = 0
            selected = false
            updateView()
        }

        override fun rollDice(callback: () -> Unit) {
            viewHolder?.let {
                it.rollDice({
                    value = 0
                    updateView()
                }, {
                    value = Random.nextInt(1, 7)
                    updateView()
                    callback()
                })
            }
        }

        private fun updateView() {
            viewHolder?.setImage(value, selected)
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun diceTheme(selected: Boolean) = when {
                selected -> R.style.DiceTheme_Selected
                else -> R.style.DiceTheme
            }

        fun setImage(value: Int, selected: Boolean) = itemView.imageView.apply {
            val resId = resources.getIdentifier(
                if (value > 0) "dice_$value" else "dice_unknown",
                "drawable",
                context.packageName
            )
            val theme = ContextThemeWrapper(context, diceTheme(selected)).theme
            val drawable = ResourcesCompat.getDrawable(resources, resId, theme)
            setImageDrawable(drawable)
        }

        fun rollDice(onAnimationStart: () -> Unit, onAnimationEnd: () -> Unit) = itemView.imageView.apply {
            val animation = AnimationUtils.loadAnimation(context, R.anim.dice_roll)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(anim: Animation?) {}
                override fun onAnimationStart(anim: Animation?) { onAnimationStart() }
                override fun onAnimationEnd(anim: Animation?) { onAnimationEnd() }

            })
            startAnimation(animation)
        }
    }
}
