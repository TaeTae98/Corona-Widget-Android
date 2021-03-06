package com.taetae98.coronawidget.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    protected fun setSupportActionBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    protected fun setResult(resultCode: Int, intent: Intent? = null) {
        requireActivity().setResult(resultCode, null)
    }

    protected fun finish() {
        requireActivity().finish()
    }
}