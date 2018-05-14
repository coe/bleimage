package app.hyuga.bleimage.ui.receivedimage

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import app.hyuga.bleimage.R
import app.hyuga.bleimage.databinding.FragmentReceivedImageBinding
import app.hyuga.bleimage.ui.ImageObservable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_URI = "param1"

class ReceivedImageFragment : Fragment() {
    private var imageUri: Uri? = null
    private var imageObservable = ImageObservable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUri = it.getParcelable(ARG_URI)
            imageObservable.imageUri = imageUri
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentReceivedImageBinding>(inflater,R.layout.fragment_received_image, container, false)
        binding.imageobservable = imageObservable
        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(uri:Uri) =
                ReceivedImageFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_URI,uri)
                    }
                }
    }
}
