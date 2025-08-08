# 📍 Real-Time GPS Tracker with JavaFX Visualization

[![MIT license](https://img.shields.io/badge/license-MIT-brightgreen.svg)](http://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/language-Java-orange.svg)](https://www.oracle.com/java/)



## 🚀 Key Features

- 📡 Real-time GPS data acquisition from NMEA 0183 GPA sentences  
- 🧠 Multi-threaded processing pipeline using `BlockingQueue`  
- 🗺️ JavaFX GUI displaying live **coordinates** and **speed**  
- ⚙️ Modular design with clear separation of concerns  
- 🧪 High-accuracy parsing and transformation of raw GPS data

---

## 🛠️ Technologies Used

- **Java 11+**
- **JavaFX 21** (via Maven)
- **Maven** for dependency management and execution
- **NMEA 0183** protocol for GPS data
- **Concurrent programming** with threads and queues

---

## 📦 Requirements

- A GPS receiver that outputs **NMEA 0183** data (e.g., `$GPGGA` sentences)
- Java Development Kit (JDK) version 11 or higher
- [JavaFX SDK](https://openjfx.io/) installed locally
- Maven configured with JavaFX dependencies

---

## ▶️ How It Works

The application launches multiple threads to handle different stages of data processing:

| Thread             | Function                                                                 |
|--------------------|--------------------------------------------------------------------------|
| `SerialLectorThread` | Reads raw GPS data from the serial port (`/dev/ttyACM0`)               |
| `ProcesadorThread`   | Filters and prepares raw strings for parsing                           |
| `TransformarThread`  | Parses GPA sentences into latitude and longitude                       |
| `VelocimetroThread`  | Calculates speed based on coordinate changes over time                 |
| `MapaApp`            | JavaFX application that displays coordinates and speed in real time    |

All threads communicate via `BlockingQueue` instances to ensure thread-safe data exchange.

---

## 🖥️ JavaFX Interface

The GUI is launched via:

```java
Application.launch(MapaApp.class);
