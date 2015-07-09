package com.spring

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextRefreshedEvent

@CompileStatic
@EnableAutoConfiguration
class BootApp extends SpringBootServletInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    OrientGraphFactory graphFactory

    /**
        *  Create entities on startup
        * @param event
        */
    @Override
    void onApplicationEvent(ContextRefreshedEvent event) {
        graphFactory.withTransaction {
            def cities = [new City(title: 'Amsterdam'),
                          new City(title: 'New York'),
                          new City(title: 'London')]
            1000.times {
                def livesIn = cities[new Random().nextInt(3)]
                def profile = new Profile(livesIn: livesIn)
                def person = new Person(firstName: "Test Person $it", lastName: "Last Name $it", profile: profile)
                Visited visit = person.addToVisitedCities(livesIn)
                visit.on = new Date()
            }
        }
    }



    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.sources BootApp
    }

    static void main(String[] args) {
        SpringApplication.run BootApp, args
    }

    @Bean
    public OrientGraphFactory databaseFactory() {
        // change to 'remote:host/dbname' if persistent storage needed
        def factory = new OrientGraphFactory("remote:/test").setupPool(1, 20)
        factory
    }

    @Bean
    SampleController sampleController() {
        new SampleController(graphFactory: graphFactory)
    }
}
