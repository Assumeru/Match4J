CREATE TABLE lists (
	id INT AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(255) NOT NULL,
	first VARCHAR(255) NOT NULL,
	second VARCHAR(255) NOT NULL,
	password VARCHAR(255)
);

CREATE TABLE words (
	id INT AUTO_INCREMENT PRIMARY KEY,
	list INT REFERENCES lists(id),
	type SMALLINT NOT NULL,
	word VARCHAR(255) NOT NULL
);

CREATE TABLE matches (
	id INT REFERENCES lists(id),
	first INT REFERENCES words(id),
	second INT REFERENCES words(id),
	PRIMARY KEY(id, first, second)
);