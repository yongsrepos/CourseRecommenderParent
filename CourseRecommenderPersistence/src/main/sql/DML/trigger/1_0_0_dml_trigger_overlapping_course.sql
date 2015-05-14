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
--Uncomment out the DELIMITER when executing from command line, e.g source ***.sql
--Put ; at line start for mvn sql plugin 

DELIMITER ;
DROP TRIGGER IF EXISTS trigger_insert_overlapping_course_id
; 

DELIMITER $$
CREATE TRIGGER trigger_insert_overlapping_course_id 
BEFORE INSERT 
ON highly_overlapping_course_set 
FOR EACH ROW 
BEGIN 
    IF NEW.course_code NOT IN (SELECT DISTINCT(code) FROM course) 
    THEN
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'No matching course for input course code.';
    END IF;
END
;
$$

DELIMITER;
DROP TRIGGER IF EXISTS trigger_update_overlapping_course_id
;

DELIMITER $$
CREATE TRIGGER trigger_update_overlapping_course_id 
BEFORE UPDATE 
ON highly_overlapping_course_set 
FOR EACH ROW 
BEGIN 
    IF NEW.course_code NOT IN (SELECT DISTINCT(code) FROM course) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No matching course for input course code.';
    END IF;    
END
;
$$

--DELIMITER;
