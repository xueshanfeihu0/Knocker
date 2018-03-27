package com.aquarids.knocker

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class KnockerFragment : Fragment() {

    private var mListener: KnockerFragment.FragmentInteractionListener? = null

    abstract fun getLayoutRes(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutRes(), container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is FragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement FragmentInteractionListener")
        }
    }

    interface FragmentInteractionListener {
        fun turnOffLocker()
    }
}
