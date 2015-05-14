---
-- #%L
-- CourseRecommenderPersistence
-- %%
-- Copyright (C) 2015 Yong Huang
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
DROP DATABASE uu_cs_course_recommender;

CREATE DATABASE IF NOT EXISTS uu_cs_course_recommender;

USE uu_cs_course_recommender;

CREATE TABLE IF NOT EXISTS supported_course_credit(
    credit DOUBLE(3,1) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS supported_course_level(
    level VARCHAR(15) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS course(
    /* int id for the purpose of constraint programming*/
    auto_gen_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50),
    name VARCHAR(150),
    credit DOUBLE(3,1),
    level VARCHAR(15),
    taught_year SMALLINT,
    start_period SMALLINT,
    end_period SMALLINT,
    
    FOREIGN KEY(credit) REFERENCES supported_course_credit(credit),
    FOREIGN KEY(level) REFERENCES supported_course_level(level),
    
    CONSTRAINT chk_course_start_period CHECK (start_period in (1,2,3,4,5,6,7,8)),
    CONSTRAINT chk_course_end_period CHECK (end_period in (1,2,3,4,5,6,7,8)),

    UNIQUE (code, taught_year, start_period)
);

CREATE TABLE IF NOT EXISTS course_normalization(
    from_earlier_code VARCHAR(50),
    to_later_code VARCHAR(50),

    PRIMARY KEY(from_earlier_code, to_later_code)
);

CREATE TABLE IF NOT EXISTS course_selection_original(
	student_id INT,
	course_id INT,

	PRIMARY KEY(student_id, course_id),

	FOREIGN KEY(course_id) REFERENCES course(auto_gen_id)
);

CREATE TABLE IF NOT EXISTS course_selection_normalized(
	student_id INT,
	normalized_course_id INT,

	PRIMARY KEY(student_id, normalized_course_id),

	FOREIGN KEY(normalized_course_id) REFERENCES course(auto_gen_id)
);

CREATE TABLE IF NOT EXISTS computing_domain(
	id VARCHAR(100) PRIMARY KEY,
	name VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS course_domain_relevance(
	course_id INT, 
	domain_id VARCHAR(100),
	relevance_level SMALLINT,

	PRIMARY KEY(course_id, domain_id),

	FOREIGN KEY(course_id) REFERENCES course(auto_gen_id),
        FOREIGN KEY(domain_id) REFERENCES computing_domain(id)
);
