/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


CREATE DATABASE IF NOT EXISTS `twstats` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `twstats`;

CREATE TABLE IF NOT EXISTS `ss_stats_pre_geo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `lat_trunc` double NOT NULL,
  `long_trunc` double NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gran` int(10) unsigned NOT NULL,
  `num_tw` int(10) unsigned NOT NULL,
  `num_rtw` int(10) unsigned NOT NULL,
  `num_rply` int(10) unsigned NOT NULL,
  `tot_tw` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `lat_trunc` (`lat_trunc`),
  KEY `long_trunc` (`long_trunc`),
  KEY `created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `ss_stats_pre_geo_bound` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `from_time` timestamp NULL DEFAULT NULL,
  `to_time` timestamp NULL DEFAULT NULL,
  `lat_trunc` double NOT NULL,
  `long_trunc` double NOT NULL,
  `num_tw` int(10) unsigned NOT NULL,
  `num_rtw` int(10) unsigned NOT NULL,
  `num_rply` int(10) unsigned NOT NULL,
  `tot_tw` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `lat_trunc` (`lat_trunc`),
  KEY `long_trunc` (`long_trunc`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;



CREATE TABLE IF NOT EXISTS `ss_stats_pre_hts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `hash_tag` varchar(50) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gran` int(10) unsigned NOT NULL,
  `num_tw` int(10) unsigned NOT NULL,
  `num_rtw` int(10) unsigned NOT NULL,
  `num_rply` int(10) unsigned NOT NULL,
  `tot_tw` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;



CREATE TABLE IF NOT EXISTS `ss_stats_pre_hts_bound` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `from_time` timestamp NULL DEFAULT NULL,
  `to_time` timestamp NULL DEFAULT NULL,
  `hash_tag` varchar(50) NOT NULL,
  `num_tw` int(10) unsigned NOT NULL,
  `num_rtw` int(10) unsigned NOT NULL,
  `num_rply` int(10) unsigned NOT NULL,
  `tot_tw` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
