package kafka.example

import jakarta.inject.Singleton

//@Singleton
class UnitConversionService {

    float celsiusToFahrenheit(float celsuis) {
        return (celsuis * 1.8) + 32
    }
}
