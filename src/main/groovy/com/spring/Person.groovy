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
    Profile profile

    static mapping = {
        id(field: '@rid')
        profile(type: OType.EMBEDDED)
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

@CompileStatic
@OrientDocument
class Profile {
    Boolean isPublic
    List<String> phones
    City city

    static mapping = {
        city(type: OType.LINK, fetch: 'eager')
    }
}