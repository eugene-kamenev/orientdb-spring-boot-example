# orientdb-spring-boot-example

OrientDB usage example with spring boot ang groovy and [orientdb-groovy](https://github.com/eugene-kamenev/orientdb-groovy/) lib

###Graph Schema Implemented:
Person->Visited<-City<-Profile[livesIn]<-Person

### Run

```bash
username>gradle bootRun
```
Open browser and access localhost:8080/persons or localhost:8080/cities to see results


### Contribution
Feel free to contribute