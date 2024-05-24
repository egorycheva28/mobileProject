package com.example.mobileproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
class RotateFragment : Fragment() {
    interface OnSeekBarChangeListener1 {
        fun onSeekBarValueChange(value: Int)
    }

    private var listener: OnSeekBarChangeListener1? = null

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
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        val seekBarValue = view.findViewById<TextView>(R.id.textView2)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                listener?.onSeekBarValueChange(progress)
                seekBarValue.text = "$progress"
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

        return inflater.inflate(R.layout.fragment_rotate, container, false)
    }
}