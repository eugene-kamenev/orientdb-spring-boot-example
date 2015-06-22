# orientdb-spring-boot-example

OrientDB usage example with spring boot ang groovy and [orientdb-groovy](https://github.com/eugene-kamenev/orientdb-groovy/) lib
Full method highlight in IntelliJ IDEA provided by orientdb-groovy lib.

###What you can learn from this project
1. You can learn about groovy AST transformations and groovy extension methods
2. OrientDB graph database gremlin usage and native query usage
3. You can take a look at *.gdsl script provided by orientdb-groovy to make your own IDE-help script.
4. Use OrientDB graph & document native apis but still stay with entity style.

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