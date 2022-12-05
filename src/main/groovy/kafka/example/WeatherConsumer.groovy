package kafka.example

import groovy.util.logging.Slf4j
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic

@Slf4j
@KafkaListener(groupId = "weather-group", offsetReset = OffsetReset.LATEST)
class WeatherConsumer {

    private final WeatherService weatherService

    WeatherConsumer(WeatherService weatherService) {
        this.weatherService = weatherService
    }

    @Topic("weather")
    void consumeWeather(@KafkaKey String key, String value) {
        float temp = value.toFloat()
        weatherService.logTemperature(temp)
    }
}
