package com.taetae98.coronawidget.fragment

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.taetae98.coronawidget.R
import com.taetae98.coronawidget.databinding.BindingFragment
import com.taetae98.coronawidget.databinding.FragmentKoreaCoronaWidgetBinding
import com.taetae98.coronawidget.dialog.ColorPickerDialog
import com.taetae98.coronawidget.dto.KoreaCoronaWidgetInformation
import com.taetae98.coronawidget.repository.WidgetRepository
import com.taetae98.coronawidget.viewmodel.KoreaCoronaWidgetViewModel
import com.taetae98.coronawidget.widget.KoreaCoronaWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KoreaCoronaWidgetFragment : BindingFragment<FragmentKoreaCoronaWidgetBinding>(R.layout.fragment_korea_corona_widget) {
    private val viewModel by viewModels<KoreaCoronaWidgetViewModel>()
    private val widgetId by lazy { requireActivity().intent.extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) }

    @Inject
    lateinit var widgetRepository: WidgetRepository

    private val onPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            viewModel.wallpaper.value = WallpaperManager.getInstance(requireContext()).drawable
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        onCreateSupportActionBar()
        onCreateOnTextColor()
        onCreateOnBackgroundColorClick()
        onCreateOnFinish()

        onPermissionRequest.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        return binding.root
    }

    override fun onCreateViewDataBinding() {
        super.onCreateViewDataBinding()
        binding.viewModel = viewModel
    }

    private fun onCreateSupportActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateOnTextColor() {
        binding.setOnTextColor {
            ColorPickerDialog(viewModel.textColor.value!!).apply {
                onColorPick = {
                    viewModel.textColor.value = it
                }
            }.show(parentFragmentManager, null)
        }
    }

    private fun onCreateOnBackgroundColorClick() {
        binding.setOnBackgroundColor {
            ColorPickerDialog(viewModel.backgroundColor.value!!).apply {
                onColorPick = {
                    viewModel.backgroundColor.value = it
                }
            }.show(parentFragmentManager, null)
        }
    }

    private fun onCreateOnFinish() {
        binding.setOnFinish {
            CoroutineScope(Dispatchers.IO).launch {
                widgetRepository.insert(
                    KoreaCoronaWidgetInformation(widgetId, viewModel.textColor.value!!, viewModel.backgroundColor.value!!)
                )
                requireContext().sendBroadcast(Intent(requireContext(), KoreaCoronaWidget::class.java).apply {
                    action = KoreaCoronaWidget.WIDGET_UPDATE
                })
                setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                finish()
            }
        }
    }
}