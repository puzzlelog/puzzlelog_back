-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        8.0.40 - MySQL Community Server - GPL
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- puzzlelog 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `puzzlelog` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `puzzlelog`;

-- 테이블 puzzlelog.friend 구조 내보내기
CREATE TABLE IF NOT EXISTS `friend` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `friend_id` int NOT NULL,
  `status` enum('PENDING','ACCEPTED','DEACTIVATED','BLOCKED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `friend_id` (`friend_id`),
  KEY `user_id_index` (`user_id`),
  KEY `friend_id_index` (`friend_id`),
  CONSTRAINT `friend_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `friend_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 puzzlelog.friend:~6 rows (대략적) 내보내기
INSERT INTO `friend` (`id`, `user_id`, `friend_id`, `status`, `created_at`, `updated_at`) VALUES
	(3, 1, 2, 'PENDING', '2025-03-11 21:02:13', '2025-03-11 21:02:13'),
	(4, 1, 2, 'PENDING', '2025-03-11 21:03:30', '2025-03-11 21:03:30'),
	(5, 1, 2, 'PENDING', '2025-03-11 21:04:21', '2025-03-11 21:04:21'),
	(6, 1, 2, 'PENDING', '2025-03-11 21:25:32', '2025-03-11 21:25:32'),
	(7, 1, 2, 'BLOCKED', '2025-03-11 21:28:14', '2025-03-12 17:45:35'),
	(8, 2, 1, 'DEACTIVATED', '2025-03-11 22:55:30', '2025-03-12 00:19:45');

-- 테이블 puzzlelog.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_pwd` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `gender` enum('MALE','FEMALE') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_alarm` tinyint(1) NOT NULL DEFAULT '1',
  `profile_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` enum('ACTIVE','DELETED','BANNED') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE',
  `last_login` timestamp NULL DEFAULT NULL,
  `role` enum('USER','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'USER',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `UKa3imlf41l37utmxiquukk8ajc` (`user_id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  KEY `gender_index` (`gender`) USING BTREE,
  KEY `birth_date_index` (`birth_date`) USING BTREE,
  KEY `status_index` (`status`) USING BTREE,
  KEY `is_alarm_index` (`is_alarm`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 puzzlelog.user:~4 rows (대략적) 내보내기
INSERT INTO `user` (`id`, `user_id`, `user_pwd`, `birth_date`, `gender`, `nickname`, `email`, `is_alarm`, `profile_img`, `created_at`, `updated_at`, `status`, `last_login`, `role`) VALUES
	(1, 'tad', NULL, NULL, NULL, 'test1', 'testuser0@example.com', 1, NULL, '2025-03-12 05:59:48', '2025-03-13 06:00:31', 'ACTIVE', NULL, 'ADMIN'),
	(2, 'testuser', '$2a$10$ncFVw7vpAhGEX1/JcQLEPO.OX6O9aCt6CnGgTIvBD/pP72WvIV6xm', '1990-12-31', 'FEMALE', '4', 'testuser@example.com', 0, 'http://example.com/profile.png', '2025-03-10 21:01:13', '2025-03-13 20:00:05', 'ACTIVE', NULL, 'USER'),
	(3, 'testuser1', '$2a$10$KOc9IxDGCqVEyT4VAU0AN.MTv1JQWnDRYoZRQq5KmFDEJBNLwRHQi', '1995-08-20', 'MALE', 'test3', 'testuser2@example.com', 1, 'uploading', '2025-03-12 18:33:31', '2025-03-13 05:59:46', 'ACTIVE', NULL, 'USER'),
	(4, 'testuser13', '$2a$10$pgSvWAK6cIBjVVtoIwyqsu.CfuCKMJrEShLF5fwJpeCYwN/KFanmi', '1995-08-20', 'MALE', 'test4', 'testuser23@example.com', 1, 'uploading', '2025-03-12 18:35:01', '2025-03-13 05:59:49', 'ACTIVE', NULL, 'USER');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
