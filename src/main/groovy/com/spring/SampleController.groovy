package com.spring

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CompileStatic
class SampleController {

    @Autowired
    OrientGraphFactory graphFactory

    @RequestMapping('/persons')
    def persons() {
        return graphFactory.withTransaction { graph ->
            return personsToJSON(Person.graphQuery('select from Person'))
        }
    }

    @RequestMapping('/cities')
    def cities() {
        return graphFactory.withTransaction { graph ->
            return citiesToJSON(City.graphQuery('select from City'))
        }
    }

    static List personsToJSON(def persons) {
        persons.collect { Person person ->
            [rid          : person.id.toString(),
             firstName    : person.firstName,
             lastName     : person.lastName,
             city         : person.profile?.livesIn?.title,
             visited      : person.vertex.pipe().in('Visited').count()]
        }
    }

    static List citiesToJSON(def cities) {
        cities.collect { City city ->
            [rid         : city.id.toString(),
             title       : city.title,
             totalVisited: city.vertex.pipe().out('Visited').count()]
        }
    }
}
