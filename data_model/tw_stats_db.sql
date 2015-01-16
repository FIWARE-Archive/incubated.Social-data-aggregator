/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

DROP DATABASE IF EXISTS `twstats`;
CREATE DATABASE IF NOT EXISTS `twstats` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `twstats`;

DROP TABLE IF EXISTS `on_monitoring_geo`;
CREATE TABLE IF NOT EXISTS `on_monitoring_geo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `latitude_from` double NOT NULL,
  `latitude_to` double NOT NULL,
  `longitude_from` double NOT NULL,
  `longitude_to` double NOT NULL,
  `tw_count` int(10) unsigned NOT NULL DEFAULT '0',
  `tw_count_week` int(10) unsigned NOT NULL DEFAULT '0',
  `tw_count_month` int(10) unsigned NOT NULL DEFAULT '0',
  `on_monitoring_from` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `monitor_from_node` char(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `on_monitoring_keys`;
CREATE TABLE IF NOT EXISTS `on_monitoring_keys` (
  `key` varchar(50) NOT NULL,
  `tw_count` int(10) unsigned NOT NULL DEFAULT '0',
  `tw_count_week` int(10) unsigned NOT NULL DEFAULT '0',
  `tw_count_month` int(10) unsigned NOT NULL DEFAULT '0',
  `on_monitoring_from` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `monitor_from_node` char(5) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
