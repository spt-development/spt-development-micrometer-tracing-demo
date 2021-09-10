Feature: SPT Development Demo - Books API: demonstrates and tests the spt-development-* libraries

  @HappyPath @BooksApi @Create
  Scenario: Creates a new book
    When a new book is POSTed to the books REST API
    Then the server will respond with a HTTP status of '201'
    And the response will have a correlationId header
    And the response body will contain the new book details
    And the new book will be added to the database
    And a successful login audit event will eventually be created
    And a new book audit event will eventually be created
    And the book creation is logged at all tiers
    And the successful login audit event processing is logged at all tiers
    And the new book audit event processing is logged at all tiers

  @HappyPath @BooksApi @Read
  Scenario:  Reads an existing book specifying the correlation ID in the request header
    Given a book exists in the database
    When the last created book is read with a GET request to the books REST API and the correlation ID is set in the request header
    Then the server will respond with a HTTP status of '200'
    And the response will have the correlationID header sent in the request
    And the response will contain the last created book details
    And a successful login audit event will eventually be created
    And the book read is logged at all tiers
    And the successful login audit event processing is logged at all tiers