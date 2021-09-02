CREATE SCHEMA IF NOT EXISTS demo;

CREATE TABLE demo.book (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title   TEXT NOT NULL,
    blurb   TEXT NOT NULL,
    author  TEXT NOT NULL,
    rrp     INT NOT NULL
);