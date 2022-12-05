package kafka.example

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import org.springframework.stereotype.Component

@Component
@KafkaClient
interface WeatherProducer {

    @Topic("weather")
    void produce(@KafkaKey String key, String value)
}
