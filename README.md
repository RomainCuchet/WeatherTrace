
# WeatherTrace

**"Hotter than it used to be, or just your imagination?"**

**WeatherTrace** has the answer.

Compare today's weather of a given location with the one of the past 45 years. The app helps to raise awareness about the climate change.

## Features
This project is a native Android application written in Kotlin.  We developed the following functionalities :
- Display historical weather data of a given location up to 45 years.
- Renders markdown for an elegant ReadMe section up to date with the one on Github
- If available uses localisation to display your current position weather data
- The language is set based on your phone's settings. Supports English, French and Spanish
- Save and update your favorite cities to be one click away from their daily data.

## Stack
The app fetches data from  :
- [OpenWeather API](https://openweathermap.org/): historical weather data up to 45 years; query by latitude & longitude; includes a free tier with limited requests per day.
- [Nominatim](https://nominatim.openstreetmap.org/): provides cities with latitude & longitude; query by city name.

We use the following librairies :
- [Vico Chart](https://github.com/patrykandpatrick/vico) to render interactive charts.
- [markdown-renderer](https://github.com/mikepenz/multiplatform-markdown-renderer) to automatically render and up to date markdown that is mounted during the build.
- Android Datastore and Kotlinx Json Serialization to save our favorite cities.

## Architecture

The app follows the Model View ViewModel (MVVM) design pattern to separate business logic to user interface.
```
C:.
├───data
│   ├───local
│   └───remote
│       ├───geo
│       │   ├───api
│       │   ├───dto
│       │   └───mapper
│       └───weather
│           ├───api
│           ├───dto
│           └───mapper
├───domain
│   ├───model
│   └───repository
├───ui
│   ├───components
│   ├───screens
│   └───theme
└───viewModel

```
The data layer is separated within local and remote sources to enhance modularity.

## TEAM
Romain CUCHET CCC1 romain.cuchet@edu.devinci.fr
Camille ESPIEUX CCC1 camille.espieux@edu.devinci.fr
Léo GUERIN CCC2 leo.guerin@edu.devinci.fr  
