-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.45 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.16.0.7229
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for sguadmissor
CREATE DATABASE IF NOT EXISTS `sguadmissor` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `sguadmissor`;

-- Dumping structure for table sguadmissor.bangquydoi
CREATE TABLE IF NOT EXISTS `bangquydoi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `phuongthuc` varchar(45) DEFAULT NULL,
  `tohop` varchar(45) DEFAULT NULL,
  `mon` varchar(45) DEFAULT NULL,
  `diema` decimal(6,2) DEFAULT NULL,
  `diemb` decimal(6,2) DEFAULT NULL,
  `diemc` decimal(6,2) DEFAULT NULL,
  `diemd` decimal(6,2) DEFAULT NULL,
  `maquydoi` varchar(45) DEFAULT NULL,
  `phanvi` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `maquydoi_UNIQUE` (`maquydoi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.bangquydoi: ~0 rows (approximately)

-- Dumping structure for table sguadmissor.diemcong
CREATE TABLE IF NOT EXISTS `diemcong` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cccd` varchar(45) NOT NULL,
  `manganh` varchar(20) DEFAULT '0.00',
  `matohop` varchar(10) DEFAULT '0.00',
  `phuongthuc` varchar(45) DEFAULT NULL,
  `diemCC` decimal(6,2) DEFAULT NULL,
  `diemUtxt` decimal(6,2) DEFAULT NULL,
  `diemTong` decimal(6,2) DEFAULT '0.00',
  `ghichu` text,
  `dc_key` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dc_keys_UNIQUE` (`dc_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.diemcong: ~0 rows (approximately)

-- Dumping structure for table sguadmissor.diemthi
CREATE TABLE IF NOT EXISTS `diemthi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cccd` varchar(20) NOT NULL,
  `sobaodanh` varchar(45) DEFAULT NULL,
  `phuongthuc` varchar(10) DEFAULT NULL,
  `TO` decimal(8,2) DEFAULT '0.00',
  `LI` decimal(8,2) DEFAULT '0.00',
  `HO` decimal(8,2) DEFAULT '0.00',
  `SI` decimal(8,2) DEFAULT '0.00',
  `SU` decimal(8,2) DEFAULT '0.00',
  `DI` decimal(8,2) DEFAULT '0.00',
  `VA` decimal(8,2) DEFAULT '0.00',
  `N1_THI` decimal(8,2) DEFAULT NULL COMMENT 'Điểm thi gốc',
  `N1_CC` decimal(8,2) DEFAULT '0.00' COMMENT 'max(N1_Thi, N1_QD)',
  `CNCN` decimal(8,2) DEFAULT '0.00',
  `CNNN` decimal(8,2) DEFAULT '0.00',
  `TI` decimal(8,2) DEFAULT '0.00',
  `KTPL` decimal(8,2) DEFAULT '0.00',
  `NL1` decimal(8,2) DEFAULT NULL,
  `NK1` decimal(8,2) DEFAULT NULL,
  `NK2` decimal(8,2) DEFAULT NULL,
  `NK3` decimal(8,2) DEFAULT NULL,
  `NK4` decimal(8,2) DEFAULT NULL,
  `NK5` decimal(8,2) DEFAULT NULL,
  `NK6` decimal(8,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cccd_UNIQUE` (`cccd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.diemthi: ~0 rows (approximately)

-- Dumping structure for table sguadmissor.nganh
CREATE TABLE IF NOT EXISTS `nganh` (
  `id` int NOT NULL AUTO_INCREMENT,
  `manganh` varchar(45) NOT NULL,
  `tennganh` varchar(100) NOT NULL,
  `tohopgoc` varchar(3) DEFAULT NULL,
  `chitieu` int NOT NULL DEFAULT '0',
  `diemsan` decimal(10,2) DEFAULT NULL,
  `diemtrungtuyen` decimal(10,2) DEFAULT NULL,
  `tuyenthang` tinyint(1) DEFAULT NULL,
  `dgnl` tinyint(1) DEFAULT NULL,
  `thpt` tinyint(1) DEFAULT NULL,
  `vsat` tinyint(1) DEFAULT NULL,
  `sl_xtt` int DEFAULT NULL,
  `sl_dgnl` int DEFAULT NULL,
  `sl_vsat` int DEFAULT NULL,
  `sl_thpt` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.nganh: ~0 rows (approximately)

-- Dumping structure for table sguadmissor.nganh_tohop
CREATE TABLE IF NOT EXISTS `nganh_tohop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `manganh` varchar(45) NOT NULL,
  `matohop` varchar(45) NOT NULL,
  `mon1` varchar(10) DEFAULT NULL,
  `hs_mon1` tinyint DEFAULT NULL,
  `mon2` varchar(10) DEFAULT NULL,
  `hs_mon2` tinyint DEFAULT NULL,
  `mon3` varchar(10) DEFAULT NULL,
  `hs_mon3` tinyint DEFAULT NULL,
  `tb_key` varchar(45) DEFAULT NULL COMMENT 'manganh_matohop',
  `N1` tinyint(1) DEFAULT NULL,
  `TO` tinyint(1) DEFAULT NULL,
  `LI` tinyint(1) DEFAULT NULL,
  `HO` tinyint(1) DEFAULT NULL,
  `SI` tinyint(1) DEFAULT NULL,
  `VA` tinyint(1) DEFAULT NULL,
  `SU` tinyint(1) DEFAULT NULL,
  `DI` tinyint(1) DEFAULT NULL,
  `TI` tinyint(1) DEFAULT NULL,
  `KHAC` tinyint(1) DEFAULT NULL,
  `KTPL` tinyint(1) DEFAULT NULL,
  `dolech` decimal(6,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`tb_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.nganh_tohop: ~0 rows (approximately)

-- Dumping structure for table sguadmissor.nguyenvong
CREATE TABLE IF NOT EXISTS `nguyenvong` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cccd` varchar(45) NOT NULL,
  `manganh` varchar(45) NOT NULL,
  `thutu` int NOT NULL,
  `diem_thxt` decimal(10,5) DEFAULT NULL COMMENT 'đã cộng điểm môn chính',
  `diem_utqd` decimal(10,5) DEFAULT NULL COMMENT 'Điểm UTQD theo tổ họp sẽ khác nhau.',
  `diem_cong` decimal(6,2) DEFAULT NULL COMMENT 'Tong 3 mon chua tinh mon chinh + diem uu tien\\\\\\\\n',
  `diem_xettuyen` decimal(10,5) DEFAULT NULL COMMENT 'đã cộng điểm ưu tiên',
  `ketqua` varchar(45) DEFAULT NULL,
  `nv_key` varchar(45) DEFAULT NULL,
  `phuongthuc` varchar(45) DEFAULT NULL,
  `tohopmon` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`nv_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.nguyenvong: ~0 rows (approximately)

-- Dumping structure for table sguadmissor.tohop
CREATE TABLE IF NOT EXISTS `tohop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matohop` varchar(45) NOT NULL,
  `mon1` varchar(10) NOT NULL,
  `mon2` varchar(10) NOT NULL,
  `mon3` varchar(10) NOT NULL,
  `tentohop` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `matohop_UNIQUE` (`matohop`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.tohop: ~0 rows (approximately)

-- Dumping structure for table sguadmissor.thisinh2025
CREATE TABLE IF NOT EXISTS `thisinh2025` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cccd` varchar(20) DEFAULT NULL,
  `sobaodanh` varchar(45) DEFAULT NULL,
  `hoten` varchar(100) DEFAULT NULL,
  `ngay_sinh` date DEFAULT NULL,
  `dien_thoai` varchar(20) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `gioi_tinh` varchar(10) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `noi_sinh` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `doi_tuong` varchar(45) DEFAULT NULL,
  `khu_vuc` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cccd_UNIQUE` (`cccd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.thisinh2025: ~0 rows (approximately)

-- Dumping structure for table sguadmissor.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table sguadmissor.user: ~1 rows (approximately)
INSERT INTO `user` (`id`, `username`, `password`, `role`, `is_active`) VALUES
	(1, 'ducem', '$2a$12$3tjwgyLnXsl2Xlk9/HyU4.OnRlX1H0p.xzTBuUMW38o62PkD1lmEK', 'admin', 1);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
