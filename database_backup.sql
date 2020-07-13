-- MariaDB dump 10.17  Distrib 10.5.4-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: board
-- ------------------------------------------------------
-- Server version	8.0.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tb_certification`
--

DROP TABLE IF EXISTS `tb_certification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_certification` (
  `co_name` varchar(50) NOT NULL,
  `co_active_date` varchar(8) NOT NULL,
  `co_exp_date` varchar(8) NOT NULL,
  `co_cert_pw` varchar(20) NOT NULL,
  `co_cert_der` varchar(2560) NOT NULL,
  `co_cert_key` varchar(2560) NOT NULL,
  `co_certification` varchar(4096) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_certification`
--

LOCK TABLES `tb_certification` WRITE;
/*!40000 ALTER TABLE `tb_certification` DISABLE KEYS */;
INSERT INTO `tb_certification` VALUES ('haein','20190605','20200605','1','1','1','1'),('haeing','20200605','20210605','1','1','1','1');
/*!40000 ALTER TABLE `tb_certification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_key`
--

DROP TABLE IF EXISTS `tb_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_key` (
  `co_name` varchar(50) NOT NULL,
  `co_key` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_key`
--

LOCK TABLES `tb_key` WRITE;
/*!40000 ALTER TABLE `tb_key` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_key` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_person`
--

DROP TABLE IF EXISTS `tb_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_person` (
  `co_name` varchar(50) NOT NULL,
  `co_date` varchar(14) NOT NULL,
  `co_rrn1` varchar(6) NOT NULL,
  `co_rrn2` varchar(7) DEFAULT NULL,
  `co_tel` varchar(12) DEFAULT NULL,
  `co_addr` varchar(100) DEFAULT NULL,
  `co_relation` varchar(5) DEFAULT NULL,
  `co_relation_name` varchar(50) DEFAULT NULL,
  `co_house_hold` varchar(50) DEFAULT NULL,
  `co_car` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_person`
--

LOCK TABLES `tb_person` WRITE;
/*!40000 ALTER TABLE `tb_person` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_siteinfo`
--

DROP TABLE IF EXISTS `tb_siteinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_siteinfo` (
  `co_name` varchar(50) NOT NULL,
  `co_domain` varchar(50) NOT NULL,
  `co_id` varchar(20) NOT NULL,
  `co_pw` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_siteinfo`
--

LOCK TABLES `tb_siteinfo` WRITE;
/*!40000 ALTER TABLE `tb_siteinfo` DISABLE KEYS */;
INSERT INTO `tb_siteinfo` VALUES ('haein','KB','1','1'),('haein','SC','2','2'),('haein','kakao','3','3'),('haeing','KB','1','1');
/*!40000 ALTER TABLE `tb_siteinfo` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-07-13 18:04:00
