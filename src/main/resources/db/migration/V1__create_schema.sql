### Creating the tables needed for the DVD Companion application ###
CREATE TABLE IF NOT EXISTS dlc (
	dlc_id INT PRIMARY KEY,
	dlc_name VARCHAR(45) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS user(
	user_id INT PRIMARY KEY,
	user_login VARCHAR(20) UNIQUE NOT NULL,
	user_password VARCHAR(72) NOT NULL
);

CREATE TABLE IF NOT EXISTS zone(
	zone_id INT PRIMARY KEY,
	zone_name VARCHAR(45) UNIQUE NOT NULL,
	zone_depth_min INT,
	zone_depth_max INT,
	dlc_id INT, 
	FOREIGN KEY (dlc_id) REFERENCES dlc(dlc_id),
	-- Accepts when both depths are null, or accepts when min depth and max is null, or accepts when both exist and min < max
	CONSTRAINT chk_zone_depths CHECK ((zone_depth_max IS NULL AND zone_depth_min IS NULL) OR (zone_depth_min IS NOT NULL AND( zone_depth_max IS NULL OR zone_depth_max>zone_depth_min)))
);

CREATE TABLE IF NOT EXISTS fish(
	fish_id INT PRIMARY KEY,
	fish_name VARCHAR(45) UNIQUE NOT NULL,
	fish_rank INT NOT NULL,
	active_time VARCHAR(10) NOT NULL,
	always_three_stars BOOLEAN NOT NULL,
	zone_id INT NOT NULL,
	dlc_id INT,
	FOREIGN KEY (zone_id) REFERENCES zone(zone_id),
	FOREIGN KEY (dlc_id) REFERENCES dlc(dlc_id)
);

CREATE TABLE IF NOT EXISTS no_star_fish(
	nsf_id INT PRIMARY KEY,
	nsf_name VARCHAR(45) UNIQUE NOT NULL,
	nsf_rank INT NOT NULL,
	zone_id INT NOT NULL,
	FOREIGN KEY (zone_id) REFERENCES zone(zone_id)
);

CREATE TABLE IF	NOT EXISTS catch(
	user_id INT,
	fish_id INT,
	best_nb_stars INT CHECK (best_nb_stars BETWEEN 1 AND 3) ,
	PRIMARY KEY(user_id, fish_id),
	FOREIGN KEY (user_id) REFERENCES user(user_id),
	FOREIGN KEY (fish_id) REFERENCES fish(fish_id)
);

CREATE TABLE IF NOT EXISTS no_star_catch(
	user_id INT,
	nsf_id INT,
	PRIMARY KEY(user_id, nsf_id),
	FOREIGN KEY(user_id) REFERENCES user(user_id),
	FOREIGN KEY(nsf_id) REFERENCES no_star_fish(nsf_id)
);

CREATE TABLE IF NOT EXISTS roe(
	user_id INT,
	fish_id INT,
	nb_roe INT CHECK(nb_roe BETWEEN 1 AND 2),
	PRIMARY KEY(user_id, fish_id),
	FOREIGN KEY(user_id) REFERENCES user(user_id),
	FOREIGN KEY(fish_id) REFERENCES fish(fish_id)
);