CREATE TABLE users (
    user_id VARCHAR (50) NOT NULL,
    user_password VARCHAR (50) NOT NULL,
    PRIMARY KEY (user_id)
);

INSERT INTO users("user", password)
    VALUES ('root', root);