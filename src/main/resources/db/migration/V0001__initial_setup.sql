CREATE SCHEMA IF NOT EXISTS demo;

CREATE TABLE demo.book (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title   TEXT NOT NULL,
    blurb   TEXT NOT NULL,
    author  TEXT NOT NULL,
    rrp     INT NOT NULL
);

/* INSERT some books as this is for demo purposes */
INSERT INTO demo.book (title, blurb, author, rrp) VALUES ('1984', 'Hidden away in the Record Department of the sprawling Ministry of Truth, Winston Smith skilfully rewrites the past to suit the needs to the Party.', 'George Orwell', 899);
INSERT INTO demo.book (title, blurb, author, rrp) VALUES ('Fahrenheit 451', 'Guy Montag is a fireman. His job is to destroy the most illegal of commodities, the source of all discord an unhappiness: the printed book.', 'Ray Bradbury', 899);
INSERT INTO demo.book (title, blurb, author, rrp) VALUES ('The Hitchhikers Guide to the Galaxy', 'One Thursday lunchtime the Earth gets unexpectedly demolished to make way for a new hyperspace bypass.', 'Douglas Adams', 699);
