package app.hyuga.bleimage.ui.main

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import app.hyuga.bleimage.R
import app.hyuga.bleimage.databinding.FragmentMainBinding
import app.hyuga.bleimage.ui.ImageObservable

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MainFragment : Fragment(),MainHandler {

    private lateinit var imageObservable:ImageObservable
    private lateinit var binding:FragmentMainBinding
    override fun onClickScan() {
        listener?.onClickScan()
    }

    override fun onClickSend() {
        imageObservable.imageBitmap?.let {
            listener?.onClickSend(it)
        }
    }

    override fun onClickCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            RESULT_OK -> {
                when(requestCode) {
                    REQUEST_IMAGE_CAPTURE -> {

                        val extras = data?.getExtras()
                        val imageBitmap = extras?.get("data") as Bitmap
                        imageObservable.imageBitmap = imageBitmap
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageObservable = ImageObservable()
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater,R.layout.fragment_main, container, false)
        binding.handler = this
        binding.imageObservable = imageObservable

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onClickScan()
        fun onClickSend(bitmap:Bitmap)
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1

        @JvmStatic
        fun newInstance() =
                MainFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
