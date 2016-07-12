# Overview #

This repo contains the exercises that accompany QualiMente's [Building High-Quality Software](https://www.qualimente.com/training/building-high-quality-software-workshop/) workshop for Java.

[![Build Status](https://travis-ci.org/qualimente/quality-exercises-java.svg?branch=master)](https://travis-ci.org/qualimente/quality-exercises-java)

# Exercises #

The following exercises are intended to help the student practice quality assurance methods such as testing and inspection.

Complete each exercise using the recommended quality assurance methods:

* automated testing via either a Test-Driven Development or Test Now approach
* inspection via code review or pair programming
* static analysis by executing and reviewing reports bundled with the project

`mvn clean verify` should always succeed when an exercise has been completed.

## Setting-up ##

The exercises project requires:

* [Maven 3.3](http://maven.apache.org/index.html)
* [Java 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

or [Docker](https://www.docker.com/products/overview)

## Testing New Code ##

Use Test Driven Development and pair programming to add the following features:

1. Add a `name` field to the Address domain model.  Questions:
    * Should name be modeled as one field, two, many?
    * What determines whether a name is valid?
2. Add an `email` field to the Address domain model.  Questions:
    * Should there be a single email field or a Map of email addresses keyed by Home, Work, etc
    * What determines whether an email address is valid?
    * How far should validation be taken in this application?

## Testing Existing Code ##

Use 'Test Now' development and code review via a partner to add the following features:

1. Use LocationDataValidator to validate the country prior to storing an Address.  Questions:
    * Should Address' country field be renamed to countryCode?
    * When should the validation be done: in domain model, controller, DAO, other?
2. Use LocationDataValidator to validate postal codes prior to storing an Address.  Questions:
    * Since postal codes schemes are country-specific, should modification of country and postalCode be tied-together?
    * When should the validation be done: in domain model, controller, DAO, other?
3. Add support for updating an existing address.  Questions:
    * What will happen if/when concurrent requests to update an Address occur?

## Refactoring Troubled Code ##

1. Refactor LocationDataValidator so that it:
    * follows the Single Responsibility Principle
    * does not hinder unit-testing of classes trying to use it, e.g. Address or Controller
    * has > 90% unit test coverage by line
    * validates [ISO 3166 alpha-2 country codes](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) correctly
    * validates country-specific postal codes are (or may be for certain countries) correct


## Using Quality Reports ##

This project has a small, but very-useful, set of quality reporting configured:

* [FindBugs](http://findbugs.sourceforge.net/): use static analysis to find bugs in the code
* [JaCoCo](http://eclemma.org/jacoco/): analyze coverage of unit and integration tests

The reporting is created as part of the standard Maven 'site' generation facility:

`mvn clean verify site`

and the generated site is available at `target/site/index.html`

## Building with Docker ##

This project may also be built with Docker and Docker Compose:

* build project: `docker-compose run build`
* generate reports: `docker-compose run reports`  
* run the server: `docker-compose run --service-ports server`

# Thanks #

Thank you to [GeoNames](http://www.geonames.org/) for providing the great location reference data used within these exercises.  Location data is licensed under the Creative Commons Attributions License.
