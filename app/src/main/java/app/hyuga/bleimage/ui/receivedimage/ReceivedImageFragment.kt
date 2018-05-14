package app.hyuga.bleimage.ui.receivedimage

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import app.hyuga.bleimage.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_URI = "param1"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ReceivedImageFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ReceivedImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReceivedImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var imageUri: Uri? = null
//    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUri = it.getParcelable(ARG_URI)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_received_image, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        listener = null
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
