package com.spring

import com.groovy.orient.graph.Edge
import com.groovy.orient.graph.Vertex
import com.orientechnologies.orient.core.metadata.schema.OType
import groovy.transform.CompileStatic

@Vertex
@CompileStatic
class Person {
    String firstName
    String lastName
    Profile profile
    List<City> visitedCities
    List<City> notVisitedCities

    static mapping = {
        profile(type: OType.LINK)
        visitedCities(edge: Visited)
        notVisitedCities(formula: "select from City where @rid not in (select in('Visited') from ?)", params: this.id)
    }
}

@Vertex
@CompileStatic
class City {
    String title
    List<Person> visitedPersons

    static mapping = {
        visitedPersons(edge: Visited)
    }
}

@Vertex
@CompileStatic
class Profile {
    Boolean isPublic
    List<String> phones
    City livesIn

    static mapping = {
        livesIn(type: OType.LINK)
    }
}

@Edge(from = Person, to = City)
@CompileStatic
class Visited {
    Date on
}
