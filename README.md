# WeatherTrace

**"Hotter than it used to be, or just your imagination?"**

**WeatherTrace** has the answer.

Compare today's weather with 45 years of data. One click away.

## Features
This project is a native Android application written in Kotlin to raise awareness about the climate change.
- Display historical weather data of a given location up to 45 years.
- Renders markdown for an elegant ReadMe section up to date with the one on Github
- If available uses localisation to display your current position weather data
- The language is set based on your phone's settings. Supports English, French and Spanish

## Stack
The app fetches data from
- [OpenWeather API](https://openweathermap.org/): historical weather data up to 45 years; query by latitude & longitude; includes a free tier with limited requests per day.
- [Nominatim](https://nominatim.openstreetmap.org/): provides cities with latitude & longitude; query by city name.


The app follows the Model View ViewModel (MVVM) design pattern to separate business logic to user interface.

