package com.spring

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import org.ops4j.orient.spring.tx.OrientDocumentDatabaseFactory
import org.ops4j.orient.spring.tx.OrientTransactionManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@RestController
class BootApp extends SpringBootServletInitializer {
    @Autowired
    OrientService service

    @RequestMapping(name = '/create', method = RequestMethod.POST)
    def create(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String cityTitle) {
        def person = service.createPerson(firstName, lastName, cityTitle)
        // person.toJSON()
        return transform(person)
    }

    @RequestMapping(name = '/show', method = RequestMethod.GET)
    def show(@RequestParam String cityTitle) {
        service.findByCity(cityTitle).collect {
            transform(it)
        }
    }

    Map transform(Person person) {
        [rid: person.id, firstName: person.firstName, lastName: person.lastName, city: person.profile?.city?.title]
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.sources BootApp
    }

    static void main(String[] args) {
        SpringApplication.run BootApp, args
    }

    private static final String URL = 'memory:test'
    private static final String USER = 'admin'
    private static final String PASSWORD = 'admin'

    @Bean(name = 'orient')
    public OrientTransactionManager transactionManager() {
        new OrientTransactionManager(databaseManager: databaseFactory())
    }

    @Bean
    public OrientDocumentDatabaseFactory databaseFactory() {
        ODatabaseDocumentTx databaseTx = new ODatabaseDocumentTx(URL).create()
        databaseTx.getMetadata().getSchema().createClass(Person)
        databaseTx.getMetadata().getSchema().createClass(City)
        databaseTx.getMetadata().getSchema().createClass(Profile)
        databaseTx.close()
        def manager = new OrientDocumentDatabaseFactory()
        manager.setUrl(URL)
        manager.setUsername(USER)
        manager.setPassword(PASSWORD)
        manager
    }

    @Bean
    public OrientService orientService() {
        new OrientService()
    }

    @Service
    static class OrientService {
        @Autowired
        OrientDocumentDatabaseFactory dbf

        @Transactional(value = 'orient')
        Person createPerson(String firstName, String lastName, String cityTitle) {
            def profile = new Profile(city: new City(title: cityTitle), isPublic: true,
                    phones: ['+9999999999', '+888888888', '+777777777'])
            def person = new Person(firstName: firstName, lastName: lastName, profile: profile)
            person.save()
            person
        }

        @Transactional('orient')
        List<Person> findByCity(String title) {
            Person.executeQuery('select from Person where profile[city][title]=?', title)
        }
    }
}
