package com.example.weatherapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeatherAdapter(
    private val context: Context,
    private var weatherList: List<Weather>,
    private val clickListener: (Weather) -> Unit
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

private val TAG = "WeatherAdapter"

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.date_text)
        private val weatherDescription: TextView = itemView.findViewById(R.id.weather_description)

        fun bind(weather: Weather) {
            dateText.text = weather.date
            weatherDescription.text = weather.description
            itemView.setOnClickListener { clickListener(weather) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false)
        Log.d(TAG, "onCreateViewHolder:$view ")
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    override fun getItemCount(): Int = weatherList.size
}
