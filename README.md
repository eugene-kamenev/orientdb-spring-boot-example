# orientdb-spring-boot-example

OrientDB usage example with spring boot ang groovy and [orientdb-groovy](https://github.com/eugene-kamenev/orientdb-groovy/) lib
Full method highlight in IntelliJ IDEA provided by orientdb-groovy lib.

###What you can learn from this project
1. Use OrientDB graph & document native apis but still stay with entity style.
2. You can learn about groovy AST transformations and groovy extension methods
3. OrientDB graph database gremlin usage and native query usage
4. You can take a look at *.gdsl script provided by orientdb-groovy to make your own IDE-help script.
5. Create fast prototype of spring-boot app with OrientDB

###Graph Schema Implemented:
Person->Visited<-City<-Profile[livesIn]<-Person

### Run
Database will be created automatically, no need of OrientDB download.
Just
```bash
gradle bootRun
```

Open browser and access localhost:8080/persons or localhost:8080/cities to see results

### Contribution
Feel free to contribute