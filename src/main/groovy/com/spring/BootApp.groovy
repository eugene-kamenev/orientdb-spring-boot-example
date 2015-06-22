package com.spring

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
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
        withTransaction(graphFactory) {
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
        withTransaction(graphFactory) {
            personsToJSON((List<Person>) Person.graphQuery('select from Person'))
        }
    }

    @RequestMapping('/cities')
    def cities() {
        withTransaction(graphFactory) {
            citiesToJSON((List<City>) City.graphQuery('select from City'))
        }
    }

    static List personsToJSON(List<Person> persons) {
        persons.collect { person ->
            [rid          : person.id.toString(),
             firstName    : person.firstName,
             lastName     : person.lastName,
             city         : person.profile?.livesIn?.title,
             visited      : person.vertex.pipe().in('Visited').count(),
             notYetVisited: person.notVisitedCities.size()]
        }
    }

    static List citiesToJSON(List<City> cities) {
        cities.collect { city ->
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

    /**
        * I didnt checked, but read this: http://orientdb.com/docs/last/Transaction-propagation.html
        *  Nested transactions should work in OrientDB 2.1
        * @param dbf
        * @param closure
        * @return
        */
    static <T> T withTransaction(OrientGraphFactory dbf,
                                 @ClosureParams(value = FromString, options = 'com.tinkerpop.blueprints.impls.orient.OrientGraph')
                                         Closure<T> closure) {
        def orientGraph = (OrientGraph) OrientGraph.activeGraph
        if (!orientGraph) {
            orientGraph = dbf.getTx()
        }
        try {
            orientGraph.begin()
            def result = closure.call(orientGraph)
            orientGraph.commit()
            return result
        } catch (Exception e) {
            log.error('EXCEPTION IN TRANSACTION', e)
            orientGraph.rollback()
        } finally {
            orientGraph.shutdown(false)
        }
        return null
    }

    @Bean
    public OrientGraphFactory databaseFactory() {
        // change to 'remote:host/dbname' if persistent storage needed
        def factory = new OrientGraphFactory("plocal:/tmp/test")
        factory.database.drop()
        factory
    }
}
