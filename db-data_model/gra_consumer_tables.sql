/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dump della struttura del database twstats
CREATE DATABASE IF NOT EXISTS `twstats` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `twstats`;


-- Dump della struttura di tabella twstats.precuc_geo_gender
CREATE TABLE IF NOT EXISTS `precuc_geo_gender` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aggr_time` timestamp NULL DEFAULT NULL,
  `lat_trunc` float NOT NULL,
  `long_trunc` float NOT NULL,
  `gran` int(11) NOT NULL,
  `num_tw_males` int(11) NOT NULL,
  `num_rtw_males` int(11) NOT NULL,
  `num_reply_males` int(11) NOT NULL,
  `num_tw_females` int(11) NOT NULL,
  `num_rtw_females` int(11) NOT NULL,
  `num_reply_females` int(11) NOT NULL,
  `num_tw_pages` int(11) NOT NULL,
  `num_rtw_pages` int(11) NOT NULL,
  `num_reply_pages` int(11) NOT NULL,
  `num_tw_unknown` int(11) NOT NULL,
  `num_rtw_unknown` int(11) NOT NULL,
  `num_reply_unknown` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `latitude` (`lat_trunc`),
  KEY `aggr_time` (`aggr_time`),
  KEY `longitude` (`long_trunc`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L’esportazione dei dati non era selezionata.


-- Dump della struttura di tabella twstats.precuc_geo_gender_bound
CREATE TABLE IF NOT EXISTS `precuc_geo_gender_bound` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_time` timestamp NULL DEFAULT NULL,
  `to_time` timestamp NULL DEFAULT NULL,
  `lat_trunc` float NOT NULL,
  `long_trunc` float NOT NULL,
  `num_tw_males` int(11) NOT NULL,
  `num_rtw_males` int(11) NOT NULL,
  `num_reply_males` int(11) NOT NULL,
  `num_tw_females` int(11) NOT NULL,
  `num_rtw_females` int(11) NOT NULL,
  `num_reply_females` int(11) NOT NULL,
  `num_tw_pages` int(11) NOT NULL,
  `num_rtw_pages` int(11) NOT NULL,
  `num_reply_pages` int(11) NOT NULL,
  `num_tw_unknown` int(11) NOT NULL,
  `num_rtw_unknown` int(11) NOT NULL,
  `num_reply_unknown` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `from` (`from_time`),
  KEY `to` (`to_time`),
  KEY `lat_trunc` (`lat_trunc`),
  KEY `long_trunc` (`long_trunc`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L’esportazione dei dati non era selezionata.


-- Dump della struttura di tabella twstats.precuc_ht_gender
CREATE TABLE IF NOT EXISTS `precuc_ht_gender` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aggr_time` timestamp NULL DEFAULT NULL,
  `ht` varchar(80) NOT NULL,
  `gran` int(11) NOT NULL,
  `num_tw_males` int(11) NOT NULL,
  `num_rtw_males` int(11) NOT NULL,
  `num_reply_males` int(11) NOT NULL,
  `num_tw_females` int(11) NOT NULL,
  `num_rtw_females` int(11) NOT NULL,
  `num_reply_females` int(11) NOT NULL,
  `num_tw_pages` int(11) NOT NULL,
  `num_rtw_pages` int(11) NOT NULL,
  `num_reply_pages` int(11) NOT NULL,
  `num_tw_unknown` int(11) NOT NULL,
  `num_rtw_unknown` int(11) NOT NULL,
  `num_reply_unknown` int(11) NOT NULL,
  `num_male` int(11) NOT NULL,
  `num_female` int(11) NOT NULL,
  `num_non_human` int(11) NOT NULL,
  `num_undefined` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `aggr_time` (`aggr_time`),
  KEY `ht` (`ht`),
  KEY `gran` (`gran`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L’esportazione dei dati non era selezionata.


-- Dump della struttura di tabella twstats.precuc_ht_gender_bound
CREATE TABLE IF NOT EXISTS `precuc_ht_gender_bound` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_time` timestamp NULL DEFAULT NULL,
  `to_time` timestamp NULL DEFAULT NULL,
  `ht` varchar(120) NOT NULL,
  `num_tw_males` int(11) NOT NULL,
  `num_rtw_males` int(11) NOT NULL,
  `num_reply_males` int(11) NOT NULL,
  `num_tw_females` int(11) NOT NULL,
  `num_rtw_females` int(11) NOT NULL,
  `num_reply_females` int(11) NOT NULL,
  `num_tw_pages` int(11) NOT NULL,
  `num_rtw_pages` int(11) NOT NULL,
  `num_reply_pages` int(11) NOT NULL,
  `num_tw_unknown` int(11) NOT NULL,
  `num_rtw_unknown` int(11) NOT NULL,
  `num_reply_unknown` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `from` (`from_time`),
  KEY `to` (`to_time`),
  KEY `ht` (`ht`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L’esportazione dei dati non era selezionata.


-- Dump della struttura di tabella twstats.tw_user_gender
CREATE TABLE IF NOT EXISTS `tw_user_gender` (
  `uid` bigint(20) NOT NULL,
  `screen_name` varchar(255) NOT NULL,
  `gender` char(1) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L’esportazione dei dati non era selezionata.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
