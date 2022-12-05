package kafka.example

import groovy.util.logging.Slf4j
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic

@Slf4j
@KafkaListener(groupId = "weather-group", offsetReset = OffsetReset.LATEST)
class WeatherConsumer {

    @Topic("weather")
    void consumeWeather(@KafkaKey String key, String value) {
        float temp = value.toFloat()
        log.info("received temp {}/{}", key, temp)
    }
}
