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

  @BooksApi @Read @UnknownBook
  Scenario: Attempts to read an unknown book
    When a book with an unknown ID is read with a GET request
    Then the server will respond with a HTTP status of '404'
    And the response will have a correlationId header
    And the response body will be empty
    And a successful login audit event will eventually be created
    And the unsuccessful book read is logged at all tiers
    And the successful login audit event processing is logged at all tiers

  @HappyPath @BooksApi @ReadAll
  Scenario:  Reads all existing books
    Given a book exists in the database
    When all books are read with a GET request
    Then the server will respond with a HTTP status of '200'
    And the response will have a correlationId header
    And the response will contain all books
    And a successful login audit event will eventually be created
    And the book read all is logged at all tiers
    And the successful login audit event processing is logged at all tiers

  @HappyPath @BooksApi @Update
  Scenario: Updates an existing book
    Given a book exists in the database
    When the last created book is updated with a PUT request to the books REST API
    Then the server will respond with a HTTP status of '200'
    And the response will have a correlationId header
    And the response will contain the updated book details
    And the last created book will be updated in the database
    And a successful login audit event will eventually be created
    And the book update is logged at all tiers
    And the successful login audit event processing is logged at all tiers
    And the update book audit event processing is logged at all tiers

  @BooksApi @Update @UnknownBook
  Scenario: Attempts to update an existing book
    When a book with an unknown ID is updated with a PUT request to the books REST API
    Then the server will respond with a HTTP status of '404'
    And the response will have a correlationId header
    And the response body will be empty
    And a successful login audit event will eventually be created
    And the unsuccessful book update is logged at all tiers
    And the successful login audit event processing is logged at all tiers

  @HappyPath @BooksApi @Delete
  Scenario: Deletes an existing book
    Given a book exists in the database
    When the last created book is deleted with a DELETE request to the books REST API
    Then the server will respond with a HTTP status of '204'
    And the response will have a correlationId header
    And the response body will be empty
    And the last created book will be deleted from the database
    And the book delete is logged at all tiers
    And a successful login audit event will eventually be created
    And the delete book audit event processing is logged at all tiers