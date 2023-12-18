DROP DATABASE IF EXISTS F1racer;
CREATE DATABASE F1racer;
USE F1racer;

-- Create the Accounts table
CREATE TABLE Accounts (
  UserName VARCHAR(50) PRIMARY KEY NOT NULL,
  UserPassword VARCHAR(50) NOT NULL, -- Added field for password
  gamesPlayed INT,
  gamesWon INT,
  totalCharactersTyped INT,
  totalWordsTyped INT,
  bestWordsPerMinute INT
);

-- Create the Games table
CREATE TABLE Games (
  ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  winnerUserName VARCHAR(50) NOT NULL,
  FOREIGN KEY (winnerUserName) REFERENCES Accounts(UserName)
);

-- Insert a record into the Accounts table
INSERT INTO Accounts (UserName,UserPassword, gamesPlayed, gamesWon, totalCharactersTyped, totalWordsTyped, bestWordsPerMinute) VALUES ('or','1234',10, 0, 150, 25, 6);
INSERT INTO Accounts (UserName,UserPassword, gamesPlayed, gamesWon, totalCharactersTyped, totalWordsTyped, bestWordsPerMinute) VALUES ('mj','1234', 10, 10, 200, 50, 99);

-- Insert a record into the Games table
INSERT INTO Games (name, winnerUserName) VALUES ('Monkey Palace', 'Wolfy');
