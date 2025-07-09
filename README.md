# Java Weather App ☀️☁️🌧️

A Swing-based Java GUI that fetches real-time weather data using the OpenWeatherMap API.

---

## 🚀 Features

- 🔎 Live weather info (temperature, humidity, description)
- 🖼️ Weather icon support
- 🌡️ Toggle between °C and °F
- 📅 3-day forecast
- 🎨 Simple, clean UI using Java Swing

---

## 📦 Folder Structure

WeatherApp/
├── src/ # Java source files
│ └── WeatherApp.java
├── lib/ # Dependency (JSON JAR)
│ └── json-20210307.jar
├── bin/ # Compiled class files
├── .vscode/ # (Optional) VS Code settings
├── README.md
├── LICENSE
├── .gitignore

---

## 🔧 How to Run

### ✅ Compile:
```
bash
javac -cp ".;lib/json-20210307.jar" -d bin src/WeatherApp.java
