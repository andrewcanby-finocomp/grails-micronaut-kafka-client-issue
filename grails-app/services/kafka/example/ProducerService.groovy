package kafka.example

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Slf4j
@Service
class ProducerService {

    // needed for grails services + scheduled to work
    static lazyInit = false

    private final WeatherProducer weatherProducer
    private final Random random

    @Autowired
    ProducerService(WeatherProducer weatherProducer) {
        this.weatherProducer = weatherProducer
        random = new Random()
    }

    @Scheduled(fixedDelay = 2000l)
    void serviceMethod() {
        String key = RandomStringUtils.randomAlphabetic(5)
        String value = (random.nextFloat() * 22f).toString()
        log.info("producing {}/{}", key, value)
        weatherProducer.produce(key, value)
    }
}
