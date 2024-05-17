package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class WeatherDetailFragment : Fragment() {

    companion object {
        private const val ARG_WEATHER = "weather"

        fun newInstance(weather: Weather): WeatherDetailFragment {
            val fragment = WeatherDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_WEATHER, weather)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var dateTextView: TextView
    private lateinit var descriptionTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateTextView = view.findViewById(R.id.detail_date_text)
        descriptionTextView = view.findViewById(R.id.detail_weather_description)

        arguments?.let {
            val weather = it.getParcelable<Weather>(ARG_WEATHER)
            weather?.let { w ->
                dateTextView.text = w.date
                descriptionTextView.text = w.description
            }
        }
    }
}
