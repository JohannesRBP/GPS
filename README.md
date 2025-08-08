# 📍 Real-Time GPS Tracker

[![MIT license](https://img.shields.io/badge/license-MIT-brightgreen.svg)](http://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/language-Java-orange.svg)](https://www.oracle.com/java/)



## 🚀 Features

- Real-time location tracking via serial-connected GPS receiver
- Custom parser for **GPA/NMEA 0183** sentences
- High-precision extraction of **latitude** and **longitude**
- Cross-platform compatibility: Linux (USB device) and Windows (COM port)
- Modular Java codebase for easy extension and integration

---

## 🛠️ Technologies Used

- **Java 8+**
- **jSerialComm** (or RXTX) for serial communication
- **NMEA 0183** GPS data protocol
- Geospatial concepts: coordinate systems, datum transformations, precision handling

---

## 📦 Requirements

- A GPS receiver that outputs **NMEA 0183** sentences via USB or serial
- Java Development Kit (JDK) version 8 or higher
- [jSerialComm library](https://fazecast.github.io/jSerialComm/) (included or downloadable)
- Operating System:
  - **Linux**: GPS device typically appears as `/dev/ttyUSB0`
  - **Windows**: Select the appropriate `COM` port (e.g., `COM3`)

---

## ▶️ Getting Started

1. **Connect your GPS receiver** via USB
2. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/gps-tracker.git
   cd gps-tracker
