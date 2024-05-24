package com.example.mobileproject

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView


class MaskingFragment : Fragment() {

    interface OnSeekBarChangeListener1 {
        fun onSeekBarValueChange2(progress1: Int, progress2: Int, progress3: Int)
    }

    private var listener: OnSeekBarChangeListener1? = null
    private var progress1 = 0
    private var progress2 = 0
    private var progress3 = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSeekBarChangeListener1) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSeekBarChangeListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seekBar1 = view.findViewById<SeekBar>(R.id.seekBar3)
        val seekBar2 = view.findViewById<SeekBar>(R.id.seekBar4)
        val seekBar3 = view.findViewById<SeekBar>(R.id.seekBar5)
        val seekBarValue1 = view.findViewById<TextView>(R.id.textView5)
        val seekBarValue2 = view.findViewById<TextView>(R.id.textView4)
        val seekBarValue3 = view.findViewById<TextView>(R.id.textView3)

        seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progress1 = progress
                listener?.onSeekBarValueChange2(progress1, progress2, progress3)
                seekBarValue1.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progress2 = progress
                listener?.onSeekBarValueChange2(progress1, progress2, progress3)
                seekBarValue2.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progress3 = progress
                listener?.onSeekBarValueChange2(progress1, progress2, progress3)
                seekBarValue3.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first2, container, false)
    }
}