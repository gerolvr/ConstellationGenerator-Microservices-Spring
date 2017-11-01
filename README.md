**Table of Contents**

# Table of Content
* [Eureka service discovery](#eureka-service-discovery)
* [Tracing with Zipkin](#tracing-with-zipkin)
  * [Microservices dependencies](#microservices-dependencies)
  * [Checking the behavior of the Ribbon client-side load balancing](#checking-the-behavior-of-the-ribbon-client-side-load-balancing)
* [Hystrix](#hystrix)
  * [Hystrix Dashboard](#hystrix-dashboard)
  
  
## Eureka service discovery

The following microservices instances are up and running:
![Eureka](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/eureka.png "Eureka")

## Tracing with Zipkin

### Microservices dependencies

Go to http://localhost:9411/zipkin/dependency
It shows the dependencies between microservices. In this demo it is simple but in larger projects it can be quite usefull to understand how all things work together.
![Dependies](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/dependencies1.png "Dependies")
![Dependies2](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/dependencies2.png "Dependies2")

### Checking the behavior of the Ribbon client-side load balancing

Ribbon is used by the [RestTemplate in the Constellation Gemerator microservice](https://github.com/gerolvr/ConstellationGenerator-Microservices-Spring/blob/7c6e99f06d89dfe5a060ac16545ba0895011ea7b/ConstellationClientServiceHystrixBreaker/src/main/java/com/gerolivo/constellationgenerator/services/StarsServiceImpl.java#L43 "RestTemplate") which is annotated with [@LoadBalanced](https://github.com/gerolvr/ConstellationGenerator-Microservices-Spring/blob/7c6e99f06d89dfe5a060ac16545ba0895011ea7b/ConstellationClientServiceHystrixBreaker/src/main/java/com/gerolivo/constellationgenerator/services/StarsServiceImpl.java#L16 "@LoadBalanced")


In our case, the load balancing is supposed to happen between the constellation generator microservice and the stars microservices. As shown in Eureka, there are two instances of the alpha-stars service so the calls should be balanced between these two instances. Let’s check that.
1. Hit http://localhost:9000/constellation-service-hystrix/ 4 times.

2. Go to the Zipkin dashboard: http://localhost:9411/zipkin
![Zipkin](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/zipkin1.png "Zipkin")

3. Select zuul-proxy, then Sort by Newest First and click on Find Traces. Zipkin will return the last traces:
![Zipkin](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/zipkin2.png "Zipkin")

4. Open each of the last 4 traces in a different tabs

5. Click on the alpha-stars-service trace
![Zipkin](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/zipkin3.png "Zipkin")

6. Notice the first request went on the alpha-stars-service instance 192.168.2.100:**9300**:
![Zipkin](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/zipkin4.png "Zipkin")
> The second request went on the instance 192.168.2.100:**9301**
![Zipkin](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/zipkin5.png "Zipkin")
> The third request went on the instance 192.168.2.100:**9300**
![Zipkin](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/zipkin6.png "Zipkin")
> The fourth request went on the instance 192.168.2.100:**9301**
![Zipkin](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/zipkin7.png "Zipkin")

So Ribbon alternated the requests on each available instance of alpha-stars-service: 192.168.2.100:**9300**, 192.168.2.100:**9301**, 192.168.2.100:**9300**, 192.168.2.100:**9301**

It is a round robin rule but other rules are available:
https://github.com/Netflix/ribbon/wiki/Working-with-load-balancers#common-rules


## Hystrix

### Hystrix Dashboard

Go to http://localhost:9201/hystrix/ or directly to http://localhost:9201/hystrix/monitor?stream=http%3A%2F%2Flocalhost%3A9201%2Fhystrix.stream

Check the Hystrix dashboard:

![Hystrix](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/hystrix1.png "Hystrix")

Both getStar and getDeltaStarFeign circuits are closed.
The circuits come from the methods annotated with [@HystrixCommand](https://github.com/gerolvr/ConstellationGenerator-Microservices-Spring/blob/7c6e99f06d89dfe5a060ac16545ba0895011ea7b/ConstellationClientServiceHystrixBreaker/src/main/java/com/gerolivo/constellationgenerator/services/StarsServiceImpl.java#L35 "@HystrixCommand").

If you hit http://localhost:9000/constellation-service-hystrix/ several time the graph will be updated in realtime:

![Hystrix](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/hystrix2.png "Hystrix")

Now make a request on http://localhost:9000/constellation-service-hystrix/forceHystrixOpen. It simulates a failure from the alpha-stars-service and Hystrix will use the [fallback method declared in the constellation-service](https://github.com/gerolvr/ConstellationGenerator-Microservices-Spring/blob/7c6e99f06d89dfe5a060ac16545ba0895011ea7b/ConstellationClientServiceHystrixBreaker/src/main/java/com/gerolivo/constellationgenerator/services/StarsServiceImpl.java#L62 "fallback method declared in the constellation-service"), which returns a “blackhole”. The delta star service is unaffected and returns a valid star:

![Hystrix](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/hystrix3.png "Hystrix")

This causes Hystrix to open the circuit.
The Hystrix dashboard has been updated. The getStar circuit is now in a Open state

![Hystrix](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/hystrix4.png "Hystrix")

Make a request on http://localhost:9000/constellation-service-hystrix. The request will go to the alpha-stars-service which will return a valid star:

![Hystrix](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/hystrix5.png "Hystrix")

The Hystrix dashboard has been updated and the getStar circuit is now in a Closed state again.

![Hystrix](https://raw.githubusercontent.com/gerolvr/ConstellationGenerator-Microservices-Spring/master/pictures/hystrix6.png "Hystrix")

Note that I overriden the default Hystrix settings. One single failure in a 5 seconds window triggers a closure of the circuit.
With the default settings the circuit closes only if there are 20 failures within a 30 seconds window.

The settings in the [application.properties](https://github.com/gerolvr/ConstellationGenerator-Microservices-Spring/blob/7c6e99f06d89dfe5a060ac16545ba0895011ea7b/ConstellationClientServiceHystrixBreaker/src/main/resources/application.properties#L19 "application.properties") are
hystrix.command.default.metrics.rollingStats.timeInMilliseconds=5000
hystrix.command.default.circuitBreaker.requestVolumeThreshold=1
