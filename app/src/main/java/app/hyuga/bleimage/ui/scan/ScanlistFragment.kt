package app.hyuga.bleimage.ui.scan

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.hyuga.bleimage.R

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ScanlistFragment.OnListFragmentInteractionListener] interface.
 */
class ScanlistFragment : Fragment(),ScanlistHandler {
    override fun onClickScanList(scanList: Parcelable) {
    }

    // TODO: Customize parameters
    private var columnCount = 1

    private lateinit var myItemRecyclerViewAdapter:MyItemRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")

        super.onCreate(savedInstanceState)
        val model = ViewModelProviders.of(this).get(ScanlistViewModel::class.java)
        model.getData().observe(this, Observer {
            it?.let {
                Log.d("hyuu","データ受信")
                val list:List<Parcelable> = it.map {
                    it.value
                }
                myItemRecyclerViewAdapter.updateList(list)
            }
        })

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d(TAG,"onCreateView")
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        val self = this
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                myItemRecyclerViewAdapter = MyItemRecyclerViewAdapter(self)
                adapter = myItemRecyclerViewAdapter
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val TAG = "ScanlistFragment"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance() =
                ScanlistFragment().apply {
                    arguments = Bundle().apply {
                        //                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
