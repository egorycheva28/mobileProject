package com.example.mobileproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FirstFragment : Fragment() {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myButton=findViewById(R.id.button2)as Button
        myButton.setOnClickListener{

        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        /*val view = inflater.inflate(R.layout.fragment_first2, container, false)
        val myButton = view.findViewById<View>(R.id.photo) as Button
        myButton.setOnClickListener{
            val activity = activity as MainActivity?
            activity?.doSomething()
        }
        return view*/
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

}