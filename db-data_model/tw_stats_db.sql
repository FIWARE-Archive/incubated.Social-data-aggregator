-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versione server:              5.5.25-log - MySQL Community Server (GPL)
-- S.O. server:                  Win64
-- HeidiSQL Versione:            9.1.0.4867
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dump della struttura del database twstats
CREATE DATABASE IF NOT EXISTS `twstats` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `twstats`;


-- Dump della struttura di tabella twstats.on_monitoring_geo
CREATE TABLE IF NOT EXISTS `on_monitoring_geo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `latitude_from` double NOT NULL,
  `latitude_to` double NOT NULL,
  `longitude_from` double NOT NULL,
  `longitude_to` double NOT NULL,
  `on_monitoring_from` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `monitor_from_node` char(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L’esportazione dei dati non era selezionata.


-- Dump della struttura di tabella twstats.on_monitoring_keys
CREATE TABLE IF NOT EXISTS `on_monitoring_keys` (
  `key` varchar(50) NOT NULL,
  `on_monitoring_from` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `monitor_from_node` char(5) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L’esportazione dei dati non era selezionata.


-- Dump della struttura di tabella twstats.on_monitoring_users
CREATE TABLE IF NOT EXISTS `on_monitoring_users` (
  `uid` bigint(20) NOT NULL COMMENT 'twitter user id',
  `on_monitoring_from` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `monitor_from_node` char(5) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- L’esportazione dei dati non era selezionata.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
