package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_list, container, false)
        recyclerView = view.findViewById(R.id.weather_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = WeatherAdapter(requireContext(), listOf()) { weather ->
            val fragment = WeatherDetailFragment.newInstance(weather)
            (activity as MainActivity).openFragment(fragment)
        }
        recyclerView.adapter = adapter
        Toast.makeText(context, "Fragment Created", Toast.LENGTH_SHORT).show()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fetchLocationAndWeather()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndWeather()
            }
        }
    }

    private fun fetchLocationAndWeather() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        println(location)
        location?.let {
            fetchWeatherData(it.latitude, it.longitude)
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        RetrofitInstance.api.getWeather(latitude, longitude).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                response.body()?.let { weatherResponse ->
                    val weatherList = weatherResponse.daily.time.mapIndexed { index, date ->
                        Weather(
                            date = date,
                            description = "Max: ${weatherResponse.daily.temperature_2m_max[index]}°C, Min: ${weatherResponse.daily.temperature_2m_min[index]}°C"
                        )
                    }
                    saveWeatherData(weatherList)
                    adapter = WeatherAdapter(requireContext(), weatherList) { weather ->
                        val fragment = WeatherDetailFragment.newInstance(weather)
                        (activity as MainActivity).openFragment(fragment)
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
                loadSavedWeatherData().let {
                    if (it.isNotEmpty()) {
                        adapter = WeatherAdapter(requireContext(), it) { weather ->
                            val fragment = WeatherDetailFragment.newInstance(weather)
                            (activity as MainActivity).openFragment(fragment)
                        }
                        recyclerView.adapter = adapter
                    }
                }
            }
        })
    }

    private fun saveWeatherData(weatherList: List<Weather>) {
        val json = Gson().toJson(weatherList)
        PreferencesHelper.saveWeatherData(requireContext(), json)
    }

    private fun loadSavedWeatherData(): List<Weather> {
        val json = PreferencesHelper.getWeatherData(requireContext())
        val type = object : TypeToken<List<Weather>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
}
