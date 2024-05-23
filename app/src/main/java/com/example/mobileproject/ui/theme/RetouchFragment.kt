//package com.example.mobileproject
//
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.SeekBar
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//
//class RetouchFragment : Fragment() {
//    private var nBitmap: Bitmap? = null
//    private var nBitmapOriginal: Bitmap? = null
//    private lateinit var retouchImageView: RetouchImageView
//    private lateinit var retouchButtonFragment: ImageButton
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            nBitmap = it.getParcelable("imageBitmap")
//            nBitmapOriginal = nBitmap
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_retouch, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val buttonBlackAndWhite: ImageButton = view.findViewById(R.id.bw_button)
//        buttonBlackAndWhite.setOnClickListener {
//            nBitmap?.let { bitmap ->
//                val bwBitmap = convertToBlackAndWhite(bitmap)
//                (activity as? Activity1)?.let {
//                    Log.d("FilterFragment", "Updating image in Activity1")
//                    it.imageView.setImageBitmap(bwBitmap)
//                    it.nBitmap = bwBitmap
//                }
//            } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT).show()
//        }
//
//
////        retouchImageView =
//        retouchButtonFragment = view.findViewById(R.id.retouch_fragment_button)
//
//        nBitmap?.let { bitmap ->
//            val bwBitmap = convertToBlackAndWhite(bitmap)
//            (activity as? Activity1)?.let {
//                Log.d("FilterFragment", "Updating image in Activity1")
//                it.imageView.setImageBitmap(bwBitmap)
//                it.nBitmap = bwBitmap
//            }
//        } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT).show()
//
//        retouchButtonFragment.setOnClickListener {
//            val isRetouchEnabled = retouchImageView.isRetouch
//            retouchImageView.isRetouch = !isRetouchEnabled
//            Toast.makeText(
//                activity,
//                if (!isRetouchEnabled) "Retouch mode enabled" else "Retouch mode disabled",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//}

