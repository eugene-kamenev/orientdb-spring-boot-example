package com.spring

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@EnableAutoConfiguration
@Slf4j
@RestController
@CompileStatic
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
            50.times {
                def livesIn = cities[new Random().nextInt(3)]
                def profile = new Profile(livesIn: livesIn)
                def person = new Person(firstName: "Test Person $it", lastName: "Last Name $it", profile: profile)
                Visited visit = person.addToVisitedCities(livesIn)
                visit.on = new Date()
            }
        }
    }

    @RequestMapping('/persons')
    def persons() {
        graphFactory.withTransaction { graph ->
            personsToJSON(Person.graphQuery('select from Person'))
        }
    }

    @RequestMapping('/cities')
    def cities() {
        graphFactory.withTransaction { graph ->
            citiesToJSON(City.graphQuery('select from City'))
        }
    }

    static List personsToJSON(def persons) {
        persons.collect { Person person ->
            [rid          : person.id.toString(),
             firstName    : person.firstName,
             lastName     : person.lastName,
             city         : person.profile?.livesIn?.title,
             visited      : person.vertex.pipe().in('Visited').count(),
             notYetVisited: person.notVisitedCities.size()]
        }
    }

    static List citiesToJSON(def cities) {
        cities.collect { City city ->
            [rid         : city.id.toString(),
             title       : city.title,
             totalVisited: city.vertex.pipe().out('Visited').count()]
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
        def factory = new OrientGraphFactory("memory:test")
        factory
    }
}
