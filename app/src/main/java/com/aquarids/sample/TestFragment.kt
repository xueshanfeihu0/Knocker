package com.aquarids.sample

import com.aquarids.knocker.KnockerFragment

class TestFragment : KnockerFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.frg_test
    }

    companion object {

        fun newInstance(): TestFragment {
            return TestFragment()
        }
    }
}
