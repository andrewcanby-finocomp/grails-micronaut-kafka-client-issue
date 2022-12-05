package kafka.example

import groovy.util.logging.Slf4j
import jakarta.inject.Singleton

//@Singleton
@Slf4j
class WeatherService {

    private final UnitConversionService unitConversionService

    WeatherService(UnitConversionService unitConversionService) {
        this.unitConversionService = unitConversionService
    }

    def logTemperature(float tempInC) {
        float tempInF = unitConversionService.celsiusToFahrenheit(tempInC)
        log.info("The temperature is {} degrees c and {} in f", tempInC, tempInF)
    }
}
