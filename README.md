````
  ____  ____ _____   ____                 _                                  _   
 / ___||  _ \_   _| |  _ \  _____   _____| | ___  _ __  _ __ ___   ___ _ __ | |_ 
 \___ \| |_) || |   | | | |/ _ \ \ / / _ \ |/ _ \| '_ \| '_ ` _ \ / _ \ '_ \| __|
  ___) |  __/ | |   | |_| |  __/\ V /  __/ | (_) | |_) | | | | | |  __/ | | | |_ 
 |____/|_|    |_|   |____/ \___| \_/ \___|_|\___/| .__/|_| |_| |_|\___|_| |_|\__|
                                                 |_|                                           
 demo----------------------------------------------------------------------------
````

[![build_status](https://github.com/spt-development/spt-development-micrometer-tracing-demo/actions/workflows/build.yml/badge.svg)](https://github.com/spt-development/spt-development-micrometer-tracing-demo/actions)

A simple demo project demonstrating how to integrate the following open source projects into a Spring Boot application,
through the use of the corresponding Spring Boot starters, and with Micrometer tracing.

* [spt-development/spt-development-audit-spring](https://github.com/spt-development/spt-development-audit-spring)
* [spt-development/spt-development-logging-spring](https://github.com/spt-development/spt-development-logging-spring)

The project provides a simple 'books' REST API backed by an in-memory H2 database, that shows how the use of these 
projects can be used to quickly and easily add production grade logging to your Spring Boot projects. The project also 
integrates [spt-development/spt-development-audit-spring](https://github.com/spt-development/spt-development-audit-spring)
demonstrating how to use simple annotations to capture audit information. The auditing is configured to use the 
recommended approach of writing the audit records to a JMS queue and processing them asynchronously.

Building locally
================

To build the project and run the integration tests, run the following Maven command:

```shell
$ mvn clean install
```

Running the demo
================

The best way to understand how things are working is to run and debug the integration tests. However, to run the 
demo project from the command line, the easiest way is to use the Spring Boot plugin (the project currently requires
JDK 17 or above).

```shell
$ ./mvnw spring-boot:run
```

The REST API can then be exercised with cURL as follows:

```shell
$ curl -v -u bob:password123! --header "Content-Type: application/json" \
    --request POST \
    --data '{"title":"My Book","blurb":"My blurb","author":"Me","rrp":1000}' \
    http://localhost:8080/api/v1.0/books

$ curl -v -u bob:password123! --header "Content-Type: application/json" \
    --request PUT \
    --data '{"id":44, "title":"My Book - updated","blurb":"My blurb - updated","author":"Me","rrp":1000}' \
    http://localhost:8080/api/v1.0/books/4

$ curl -v -u bob:password123! http://localhost:8080/api/v1.0/books
$ curl -v -u bob:password123! http://localhost:8080/api/v1.0/books/4
$ curl -v -u bob:password123! -X DELETE http://localhost:8080/api/v1.0/books/4
```