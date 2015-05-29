package com.spring

import com.groovy.orient.document.OrientDocument
import com.orientechnologies.orient.core.metadata.schema.OType
import groovy.transform.CompileStatic

@OrientDocument
@CompileStatic
class Person {
    String id
    String firstName
    String lastName
    City city

    static mapping = {
        id(field: '@rid')
        city(type: OType.LINK)
    }
}

@OrientDocument
@CompileStatic
class City {
    String id
    String title

    static mapping = {
        id(field: '@rid')
    }
}

