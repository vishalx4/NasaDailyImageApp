package com.example.nasapictureoftheday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.nasapictureoftheday.databinding.ActivityMainBinding
import com.example.nasapictureoftheday.helper.hide
import com.example.nasapictureoftheday.helper.show
import com.example.nasapictureoftheday.viewmodels.NasaViewModel
import com.example.nasapictureoftheday.viewmodels.NasaViewModelProvider
import com.example.nasapictureoftheday.viewmodels.State
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this, NasaViewModelProvider(this))[NasaViewModel::class.java]
        setObservers(viewModel)
        setClickListeners(viewModel)
    }

    private fun setClickListeners(viewModel: NasaViewModel) {
        binding.date.setOnClickListener {
            showDate(viewModel)
        }

        binding.retryLayout.setOnClickListener {
            showDate(viewModel)
        }
    }

    private fun setObservers(viewModel: NasaViewModel) {
        viewModel.nasaModel.observe(this) {
            it?.let { model ->
                binding.date.text = getString(R.string.str_date, model.date)
                Glide.with(this)
                    .load(model.url)
                    .centerCrop()
                    .into(binding.nasaImage)
                binding.description.text = model.explanation
                binding.nasaTitle.text = model.title
            }
        }

        viewModel.state.observe(this) {
            it?.let {  state ->
                when (state) {
                    State.IN_PROGRESS -> {
                        binding.errorText.hide()
                        binding.retryLayout.hide()
                        binding.nasaLayout.hide()
                        binding.nasaProgressAnimation.show()
                    }
                    State.FAILED -> {
                        binding.nasaLayout.hide()
                        binding.nasaProgressAnimation.hide()

                        binding.errorText.text = viewModel.errorMessage.value.toString()
                        binding.errorText.show()
                        binding.retryLayout.show()
                    }
                    State.SUCCESS -> {
                        binding.nasaProgressAnimation.hide()
                        binding.nasaLayout.show()
                    }
                }
            }
        }
    }

    private fun showDate(viewModel: NasaViewModel) {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())
                    .build()
            )
            .setTitleText(getString(R.string.str_select_the_date))
            .build()

        datePicker
            .addOnPositiveButtonClickListener {
                viewModel.sendRequestForTheDate(it)
            }
        datePicker.show(supportFragmentManager, "tag")
    }
}