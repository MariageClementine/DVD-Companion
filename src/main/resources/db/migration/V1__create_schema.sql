### Creating the tables needed for the DVD Companion application ###
CREATE TABLE IF NOT EXISTS dlc (
	dlc_id INT PRIMARY KEY,
	dlc_name VARCHAR(45) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS user(
	user_id INT PRIMARY KEY AUTO_INCREMENT,
	user_login VARCHAR(20) UNIQUE NOT NULL,
	user_password_hash VARCHAR(72) NOT NULL
);

CREATE TABLE IF NOT EXISTS zone(
	zone_id INT PRIMARY KEY,
	zone_name VARCHAR(45) UNIQUE NOT NULL,
	zone_depth_min INT,
	zone_depth_max INT,
	dlc_id INT, 
	CONSTRAINT fk_zone_dlc FOREIGN KEY (dlc_id) REFERENCES dlc(dlc_id),
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
	CONSTRAINT fk_fish_zone FOREIGN KEY (zone_id) REFERENCES zone(zone_id),
	CONSTRAINT fk_fish_dlc FOREIGN KEY (dlc_id) REFERENCES dlc(dlc_id),
	CONSTRAINT chk_fish_active_time CHECK (active_time IN('BOTH','DAY','NIGHT'))
);

CREATE TABLE IF NOT EXISTS no_star_fish(
	nsf_id INT PRIMARY KEY,
	nsf_name VARCHAR(45) UNIQUE NOT NULL,
	nsf_rank INT NOT NULL,
	zone_id INT NOT NULL,
	CONSTRAINT fk_nsf_zone FOREIGN KEY (zone_id) REFERENCES zone(zone_id)
);

CREATE TABLE IF	NOT EXISTS catch(
	catch_id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	fish_id INT NOT NULL,
	best_nb_stars INT  NOT NULL CHECK (best_nb_stars BETWEEN 1 AND 3) ,
	CONSTRAINT fk_catch_user FOREIGN KEY (user_id) REFERENCES user(user_id),
	CONSTRAINT fk_catch_fish FOREIGN KEY (fish_id) REFERENCES fish(fish_id),
	CONSTRAINT uq_catch_user_fish UNIQUE (user_id, fish_id)
);

CREATE TABLE IF NOT EXISTS no_star_catch(
	nscatch_id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	nsf_id INT NOT NULL,
	CONSTRAINT fk_nscatch_user FOREIGN KEY(user_id) REFERENCES user(user_id),
	CONSTRAINT fk_nscatch_nsf FOREIGN KEY(nsf_id) REFERENCES no_star_fish(nsf_id),
	CONSTRAINT uq_nscatch_user_nsf UNIQUE (user_id, nsf_id)
);

CREATE TABLE IF NOT EXISTS roe(
	roe_id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	fish_id INT NOT NULL,
	nb_roe INT NOT NULL CHECK(nb_roe BETWEEN 1 AND 2),
	CONSTRAINT fk_roe_user FOREIGN KEY(user_id) REFERENCES user(user_id),
	CONSTRAINT fk_roe_fish FOREIGN KEY(fish_id) REFERENCES fish(fish_id),
	CONSTRAINT uq_roe_user_fish UNIQUE (user_id, fish_id)
);