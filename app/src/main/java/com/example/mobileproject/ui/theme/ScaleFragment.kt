package com.example.mobileproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class ScaleFragment : Fragment() {

    interface OnSeekBarChangeListener2 {
        fun onSeekBarValueChangeScale(value: Float)
    }

    private var listener: OnSeekBarChangeListener2? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSeekBarChangeListener2) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSeekBarChangeListener2")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seekBar = view.findViewById<SeekBar>(R.id.seekBarScale)
        val seekBarValue = view.findViewById<TextView>(R.id.scaleText)

        val maxProgress = 300

        seekBar.max = maxProgress
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val progressFloat = progress / 100.0f
                listener?.onSeekBarValueChangeScale(progressFloat)
                seekBarValue.text = "$progressFloat"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scale, container, false)
    }
}
