SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `expertfinder` ;
CREATE SCHEMA IF NOT EXISTS `expertfinder` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
SHOW WARNINGS;
USE `expertfinder`;

-- -----------------------------------------------------
-- Table `expertfinder`.`document`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`document` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`document` (
  `id` INT NOT NULL ,
  `title` VARCHAR(256) NOT NULL ,
  `redirects_to` INT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `document_title` (`title` ASC) ,
  INDEX `fk_document_document1` (`redirects_to` ASC) ,
  CONSTRAINT `fk_document_document1`
    FOREIGN KEY (`redirects_to` )
    REFERENCES `expertfinder`.`document` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`author`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`author` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`author` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(256) CHARACTER SET 'latin1' COLLATE 'latin1_general_ci' NOT NULL ,
  `location` VARCHAR(256) CHARACTER SET 'latin1' COLLATE 'latin1_general_ci' NULL ,
  `isBot` TINYINT(1) NULL ,
  `isAnoymous` TINYINT(1) NULL ,
  `affiliation` VARCHAR(767) NULL ,
  `realName` VARCHAR(256) NULL ,
  `position` VARCHAR(256) NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `author_name` (`name` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`revision`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`revision` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`revision` (
  `id` INT NOT NULL ,
  `id_document` INT NOT NULL ,
  `id_author` INT NOT NULL ,
  `timestamp` DATETIME NOT NULL ,
  `count` INT NOT NULL ,
  PRIMARY KEY (`id`, `id_document`, `id_author`) ,
  INDEX `fk_revision_document1` (`id_document` ASC) ,
  INDEX `fk_revision_author1` (`id_author` ASC) ,
  CONSTRAINT `fk_revision_document1`
    FOREIGN KEY (`id_document` )
    REFERENCES `expertfinder`.`document` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_revision_author1`
    FOREIGN KEY (`id_author` )
    REFERENCES `expertfinder`.`author` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`section`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`section` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`section` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `title` VARCHAR(1024) NOT NULL ,
  `level` INT NOT NULL ,
  `startPos` INT NOT NULL ,
  `endPos` INT NOT NULL ,
  `id_parent_section` INT NULL ,
  `id_revision_created` INT NULL ,
  `id_revision_deleted` INT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `section_title` (`title` ASC) ,
  INDEX `fk_section_section1` (`id_parent_section` ASC) ,
  INDEX `fk_section_revision1` (`id_revision_created` ASC) ,
  INDEX `fk_section_revision2` (`id_revision_deleted` ASC) ,
  CONSTRAINT `fk_section_section1`
    FOREIGN KEY (`id_parent_section` )
    REFERENCES `expertfinder`.`section` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_section_revision1`
    FOREIGN KEY (`id_revision_created` )
    REFERENCES `expertfinder`.`revision` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_section_revision2`
    FOREIGN KEY (`id_revision_deleted` )
    REFERENCES `expertfinder`.`revision` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci
COMMENT = 'Stores subsections (subtopics) of Wikipedia articles';

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`concept`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`concept` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`concept` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `uri` VARCHAR(256) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `concept_uri` (`uri` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`word`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`word` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`word` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `word` VARCHAR(256) NOT NULL ,
  `wordStem` VARCHAR(256) NULL ,
  `startPos` INT NOT NULL ,
  `endPos` INT NOT NULL ,
  `id_revision_created` INT NULL ,
  `id_revision_deleted` INT NULL ,
  `id_concept` INT NULL ,
  `id_section` INT NULL ,
  `isNoun` TINYINT(1) NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_word_revision1` (`id_revision_created` ASC) ,
  INDEX `fk_word_revision2` (`id_revision_deleted` ASC) ,
  INDEX `fk_word_concept1` (`id_concept` ASC) ,
  INDEX `fk_word_section1` (`id_section` ASC) ,
  INDEX `word_word` (`word` ASC) ,
  INDEX `word_wordstem` (`wordStem` ASC) ,
  CONSTRAINT `fk_word_revision1`
    FOREIGN KEY (`id_revision_created` )
    REFERENCES `expertfinder`.`revision` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_word_revision2`
    FOREIGN KEY (`id_revision_deleted` )
    REFERENCES `expertfinder`.`revision` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_word_concept1`
    FOREIGN KEY (`id_concept` )
    REFERENCES `expertfinder`.`concept` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_word_section1`
    FOREIGN KEY (`id_section` )
    REFERENCES `expertfinder`.`section` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`concept_similarity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`concept_similarity` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`concept_similarity` (
  `id_concept_1` INT NOT NULL ,
  `id_concept_2` INT NOT NULL ,
  `similarity` DOUBLE NOT NULL ,
  PRIMARY KEY (`id_concept_1`, `id_concept_2`) ,
  INDEX `fk_concept_similarity_concept` (`id_concept_1` ASC) ,
  INDEX `fk_concept_similarity_concept1` (`id_concept_2` ASC) ,
  CONSTRAINT `fk_concept_similarity_concept`
    FOREIGN KEY (`id_concept_1` )
    REFERENCES `expertfinder`.`concept` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_concept_similarity_concept1`
    FOREIGN KEY (`id_concept_2` )
    REFERENCES `expertfinder`.`concept` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`section_has_concept`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`section_has_concept` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`section_has_concept` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `id_section` INT NULL ,
  `id_concept` INT NULL ,
  `similarity` DOUBLE NULL ,
  INDEX `fk_section_has_concept_section1` (`id_section` ASC) ,
  INDEX `fk_section_has_concept_concept1` (`id_concept` ASC) ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_section_has_concept_section1`
    FOREIGN KEY (`id_section` )
    REFERENCES `expertfinder`.`section` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_section_has_concept_concept1`
    FOREIGN KEY (`id_concept` )
    REFERENCES `expertfinder`.`concept` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`document_has_concept`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`document_has_concept` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`document_has_concept` (
  `id_document` INT NOT NULL ,
  `id_concept` INT NOT NULL ,
  PRIMARY KEY (`id_document`, `id_concept`) ,
  INDEX `fk_document_has_concept_document1` (`id_document` ASC) ,
  INDEX `fk_document_has_concept_concept1` (`id_concept` ASC) ,
  CONSTRAINT `fk_document_has_concept_document1`
    FOREIGN KEY (`id_document` )
    REFERENCES `expertfinder`.`document` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_document_has_concept_concept1`
    FOREIGN KEY (`id_concept` )
    REFERENCES `expertfinder`.`concept` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`link`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`link` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`link` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `id_source_section` INT NULL ,
  `id_target_section` INT NULL ,
  `startPos` INT NOT NULL ,
  `endPos` INT NOT NULL ,
  `id_revision_created` INT NULL ,
  `id_revision_deleted` INT NULL ,
  `text` VARCHAR(256) CHARACTER SET 'latin1' COLLATE 'latin1_general_ci' NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_link_section1` (`id_source_section` ASC) ,
  INDEX `fk_link_section2` (`id_target_section` ASC) ,
  INDEX `fk_link_revision1` (`id_revision_created` ASC) ,
  INDEX `fk_link_revision2` (`id_revision_deleted` ASC) ,
  CONSTRAINT `fk_link_section1`
    FOREIGN KEY (`id_source_section` )
    REFERENCES `expertfinder`.`section` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_link_section2`
    FOREIGN KEY (`id_target_section` )
    REFERENCES `expertfinder`.`section` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_link_revision1`
    FOREIGN KEY (`id_revision_created` )
    REFERENCES `expertfinder`.`revision` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_link_revision2`
    FOREIGN KEY (`id_revision_deleted` )
    REFERENCES `expertfinder`.`revision` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`category` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`category` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(256) CHARACTER SET 'latin1' COLLATE 'latin1_general_ci' NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `category_name` (`name` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`document_has_category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`document_has_category` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`document_has_category` (
  `id_document` INT NOT NULL ,
  `id_category` INT NOT NULL ,
  `id_revision_created` INT NULL ,
  `id_revision_deleted` INT NULL ,
  `processed` TINYINT(1) NULL ,
  PRIMARY KEY (`id_document`, `id_category`) ,
  INDEX `fk_document_has_category_document1` (`id_document` ASC) ,
  INDEX `fk_document_has_category_category1` (`id_category` ASC) ,
  INDEX `fk_document_has_category_revision1` (`id_revision_created` ASC) ,
  INDEX `fk_document_has_category_revision2` (`id_revision_deleted` ASC) ,
  CONSTRAINT `fk_document_has_category_document1`
    FOREIGN KEY (`id_document` )
    REFERENCES `expertfinder`.`document` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_document_has_category_category1`
    FOREIGN KEY (`id_category` )
    REFERENCES `expertfinder`.`category` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_document_has_category_revision1`
    FOREIGN KEY (`id_revision_created` )
    REFERENCES `expertfinder`.`revision` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_document_has_category_revision2`
    FOREIGN KEY (`id_revision_deleted` )
    REFERENCES `expertfinder`.`revision` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`application_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`application_data` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`application_data` (
  `key` VARCHAR(128) NOT NULL ,
  `value` VARCHAR(256) NULL ,
  PRIMARY KEY (`key`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`author_has_credibility`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`author_has_credibility` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`author_has_credibility` (
  `id_author` INT NOT NULL ,
  `id_concept` INT NOT NULL ,
  `credibility` DOUBLE NULL ,
  `expertise` DOUBLE NULL ,
  PRIMARY KEY (`id_author`, `id_concept`) ,
  INDEX `fk_author_has_concept_author1` (`id_author` ASC) ,
  INDEX `fk_author_has_concept_concept1` (`id_concept` ASC) ,
  INDEX `idx_expertise` (`credibility` DESC) ,
  CONSTRAINT `fk_author_has_concept_author1`
    FOREIGN KEY (`id_author` )
    REFERENCES `expertfinder`.`author` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_author_has_concept_concept1`
    FOREIGN KEY (`id_concept` )
    REFERENCES `expertfinder`.`concept` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `expertfinder`.`author_contributions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `expertfinder`.`author_contributions` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `expertfinder`.`author_contributions` (
  `id_author` INT NOT NULL ,
  `id_concept` INT NOT NULL ,
  PRIMARY KEY (`id_author`, `id_concept`) ,
  INDEX `fk_author_has_concept_author2` (`id_author` ASC) ,
  INDEX `fk_author_has_concept_concept2` (`id_concept` ASC) ,
  CONSTRAINT `fk_author_has_concept_author2`
    FOREIGN KEY (`id_author` )
    REFERENCES `expertfinder`.`author` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_author_has_concept_concept2`
    FOREIGN KEY (`id_concept` )
    REFERENCES `expertfinder`.`concept` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

SHOW WARNINGS;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
