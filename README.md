# Grails/Micronaut Dependency Injection issue/confusion?

Note: this is an oversimplified version of the real problem domain, trimmed for clarity on actual issue

## Background

We have existing Grails applications that have 100s of services in `grails-app/services` and also `src/main/groovy`.

Depending on the project, bean definition (for things outside of `grails-app`) is usually done via `resources.groovy` per the Grails documentation. 
Newer applications use a combination of `@ComponentScan` and `@Component/@Service/@Controller` etc, however my understanding is this is materially the same thing.

A project is adding Kafka functionality. By using the Micronaut Kafka client, it seems that we can reuse all of our service logic, leveraging the client
to simply proxy the values through to the existing service.

In concrete terms for this project, we are looking to use our existing [`WeatherService`](grails-app/services/kafka/example/WeatherService.groovy) 
in a Consumer (see [`WeatherConsumer`](src/main/groovy/kafka/example/WeatherConsumer.groovy). 
The `WeatherService` is already a functioning piece of our  application, and is invoked from Controllers & other services without issue.

## The issue

When we try to inject the service to the Consumer, we are unable to effect dependency injection in a way where the consumer is aware of the service that is required.

The micronaut-spring documentation describes various scenarios where this _should_ work but the only way I can 'make' this work is by annotating the service with
`@Singleton`. 

Using `@Singleton` _seems_ to solve the problem, however upon further testing any dependencies managed/injected by Spring for the `resources.groovy` then
fail to resolve. Of course, the logical suggestion here is to add `@Singleton` or equivalent to each service... but as there are hundreds of services like this in
our projects (plural!) it seems like a very invasive change.

### Reproducing

1. Simply start the application
   ```bash
   ./gradlew bootRun
   ```
2. Observe the following log:
   ```
   2022-12-05 13:08:01.683 ERROR --- [  restartedMain] o.s.boot.SpringApplication               : Application run failed

   io.micronaut.context.exceptions.DependencyInjectionException: Failed to inject value for parameter [weatherService] of class: kafka.example.WeatherConsumer
   
   Message: No bean of type [kafka.example.WeatherService] exists. Make sure the bean is not disabled by bean requirements (enable trace logging for 'io.micronaut.context.condition' to check) and if the bean is enabled then ensure the class is declared a bean and annotation processing is enabled (for Java and Kotlin the 'micronaut-inject-java' dependency should be configured as an annotation processor).
   Path Taken: new WeatherConsumer(WeatherService weatherService) --> new WeatherConsumer([WeatherService weatherService])
   at io.micronaut.context.AbstractInitializableBeanDefinition.resolveBean(AbstractInitializableBeanDefinition.java:2091)
   at io.micronaut.context.AbstractInitializableBeanDefinition.getBeanForConstructorArgument(AbstractInitializableBeanDefinition.java:1299)
   at kafka.example.$WeatherConsumer$Definition.build(Unknown Source)
   at io.micronaut.context.DefaultBeanContext.resolveByBeanFactory(DefaultBeanContext.java:2333)
   at io.micronaut.context.DefaultBeanContext.doCreateBean(DefaultBeanContext.java:2284)
   at io.micronaut.context.DefaultBeanContext.doCreateBean(DefaultBeanContext.java:2230)
   at io.micronaut.context.DefaultBeanContext.createRegistration(DefaultBeanContext.java:2995)
   at io.micronaut.context.SingletonScope.getOrCreate(SingletonScope.java:80)
   at io.micronaut.context.DefaultBeanContext.findOrCreateSingletonBeanRegistration(DefaultBeanContext.java:2897)
   at io.micronaut.context.DefaultBeanContext.resolveBeanRegistration(DefaultBeanContext.java:2858)
   at io.micronaut.context.DefaultBeanContext.resolveBeanRegistration(DefaultBeanContext.java:2779)
   at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:1596)
   at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:865)
   at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:857)
   at io.micronaut.configuration.kafka.processor.KafkaConsumerProcessor.submitConsumerThread(KafkaConsumerProcessor.java:417)
   at io.micronaut.configuration.kafka.processor.KafkaConsumerProcessor.submitConsumerThreads(KafkaConsumerProcessor.java:404)
   at io.micronaut.configuration.kafka.processor.KafkaConsumerProcessor.process(KafkaConsumerProcessor.java:309)
   at io.micronaut.context.DefaultBeanContext.lambda$initializeContext$35(DefaultBeanContext.java:1975)
   at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
   at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1655)
   at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
   at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
   at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
   at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
   at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:497)
   at io.micronaut.context.DefaultBeanContext.lambda$initializeContext$36(DefaultBeanContext.java:1948)
   at java.base/java.util.HashMap.forEach(HashMap.java:1337)
   at io.micronaut.context.DefaultBeanContext.initializeContext(DefaultBeanContext.java:1946)
   at io.micronaut.context.DefaultApplicationContext.initializeContext(DefaultApplicationContext.java:245)
   at io.micronaut.context.DefaultBeanContext.readAllBeanDefinitionClasses(DefaultBeanContext.java:3305)
   at io.micronaut.context.DefaultBeanContext.finalizeConfiguration(DefaultBeanContext.java:3663)
   at io.micronaut.context.DefaultBeanContext.start(DefaultBeanContext.java:339)
   at io.micronaut.context.DefaultApplicationContext.start(DefaultApplicationContext.java:190)
   at grails.boot.GrailsApp.createApplicationContext(GrailsApp.groovy:184)
   at org.springframework.boot.SpringApplication.run(SpringApplication.java:305)
   at grails.boot.GrailsApp.run(GrailsApp.groovy:99)
   at grails.boot.GrailsApp.run(GrailsApp.groovy:485)
   at grails.boot.GrailsApp.run(GrailsApp.groovy:472)
   at kafka.example.Application.main(Application.groovy:14)
   at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
   at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
   at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
   at java.base/java.lang.reflect.Method.invoke(Method.java:566)
   at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:49)
   Caused by: io.micronaut.context.exceptions.NoSuchBeanException: No bean of type [kafka.example.WeatherService] exists. Make sure the bean is not disabled by bean requirements (enable trace logging for 'io.micronaut.context.condition' to check) and if the bean is enabled then ensure the class is declared a bean and annotation processing is enabled (for Java and Kotlin the 'micronaut-inject-java' dependency should be configured as an annotation processor).
   at io.micronaut.context.DefaultBeanContext.resolveBeanRegistration(DefaultBeanContext.java:2784)
   at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:1596)
   at io.micronaut.context.AbstractBeanResolutionContext.getBean(AbstractBeanResolutionContext.java:66)
   at io.micronaut.context.AbstractInitializableBeanDefinition.resolveBean(AbstractInitializableBeanDefinition.java:2069)
   ... 45 common frames omitted
   ```

## Expected

Dependency Injection works and beans are resolved, ideally without having to bulk modify 100s of existing services