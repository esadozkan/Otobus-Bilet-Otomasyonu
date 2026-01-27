-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: obilet_db
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `araclar`
--

DROP TABLE IF EXISTS `araclar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `araclar` (
  `id` int NOT NULL AUTO_INCREMENT,
  `plaka` varchar(20) COLLATE utf8mb4_turkish_ci NOT NULL,
  `marka_model` varchar(50) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `koltuk_sayisi` int DEFAULT '40',
  PRIMARY KEY (`id`),
  UNIQUE KEY `plaka` (`plaka`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_turkish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `araclar`
--

LOCK TABLES `araclar` WRITE;
/*!40000 ALTER TABLE `araclar` DISABLE KEYS */;
INSERT INTO `araclar` VALUES (1,'34 TR 1453','Mercedes Travego 16 SHD',40),(2,'06 ANK 1923','Neoplan Tourliner',40),(3,'35 IZM 3535','Temsa Safir Plus',40),(4,'16 BRS 0016','MAN Lion Coach',40),(5,'18 CNK 1818','Mercedes Tourismo',40),(6,'61 TS 1967','Neoplan Cityliner',40),(7,'27 GAZ 9027','Temsa Maraton',40),(8,'23 ELZ 2324','Mercedes Travego 15 SHD',40),(9,'55 SMS 1919','MAN Lion Coach',40),(10,'34 IST 2025','Setra S 516 HD',40);
/*!40000 ALTER TABLE `araclar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biletler`
--

DROP TABLE IF EXISTS `biletler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biletler` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pnr_kod` varchar(10) COLLATE utf8mb4_turkish_ci NOT NULL,
  `sefer_id` int NOT NULL,
  `musteri_id` int DEFAULT NULL,
  `yolcu_ad` varchar(100) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `yolcu_tc` varchar(11) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `koltuk_no` int NOT NULL,
  `cinsiyet` varchar(10) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `tutar` int DEFAULT NULL,
  `durum` varchar(20) COLLATE utf8mb4_turkish_ci DEFAULT 'Aktif',
  PRIMARY KEY (`id`),
  UNIQUE KEY `pnr_kod` (`pnr_kod`),
  KEY `sefer_id` (`sefer_id`),
  KEY `musteri_id` (`musteri_id`),
  CONSTRAINT `biletler_ibfk_1` FOREIGN KEY (`sefer_id`) REFERENCES `seferler` (`id`) ON DELETE CASCADE,
  CONSTRAINT `biletler_ibfk_2` FOREIGN KEY (`musteri_id`) REFERENCES `musteriler` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_turkish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biletler`
--

LOCK TABLES `biletler` WRITE;
/*!40000 ALTER TABLE `biletler` DISABLE KEYS */;
INSERT INTO `biletler` VALUES (3,'PNR773610',2,1,'essad','33333333333',10,'Erkek',500,'Aktif'),(7,'PNR93661',2,1,'ok','98989898988',1,'Kadın',500,'Aktif'),(8,'PNR16758',5,1,'oo','45454545545',8,'Kadın',40,'Aktif'),(9,'PNR680433',3,NULL,'ppp','78788787888',33,'Erkek',500,'Aktif'),(10,'PNR41703',5,2,'9','99999999999',3,'Erkek',40,'Aktif'),(11,'PNR16094',5,4,'e204','23323232322',4,'Kadın',40,'Aktif'),(12,'PNR74679',3,4,'e204','78787878787',9,'Erkek',500,'Aktif'),(13,'PNR792411',3,4,'e204','76767676766',11,'Kadın',500,'Aktif'),(14,'PNR885415',3,4,'e204','76767676766',15,'Erkek',500,'Aktif'),(15,'PNR669617',4,4,'tf','34563456345',17,'Erkek',500,'Aktif'),(16,'PNR436718',4,4,'ed','23452345345',18,'Erkek',500,'İptal'),(17,'PNR36188',2,3,'lm','09870987987',8,'Kadın',500,'Aktif'),(18,'PNR520211',2,3,'ıj','78966789789',11,'Kadın',500,'Aktif'),(19,'PNR679416',4,2,'rr','23456456786',16,'Kadın',1200,'Aktif'),(20,'PNR48451',10,2,'esad','34567845678',1,'Erkek',650,'Aktif'),(22,'PNR313411',5,2,'er','34555678908',11,'Erkek',1200,'Aktif'),(23,'PNR340312',5,2,'ty','65465465466',12,'Kadın',1200,'İptal'),(24,'PNR542010',23,NULL,'rd','56789567894',10,'Erkek',750,'Aktif'),(25,'PNR222011',15,NULL,'eses','43658756567',11,'Kadın',850,'Aktif'),(26,'PNR810415',15,NULL,'yygy','43658756590',15,'Erkek',850,'Aktif'),(27,'PNR743011',23,NULL,'ws','43658756545',11,'Erkek',750,'Aktif'),(28,'PNR527712',23,NULL,'qwer','43658756565',12,'Erkek',750,'Aktif'),(29,'PNR261617',23,NULL,'vcbn','43658732567',17,'Kadın',750,'Aktif'),(30,'PNR995818',23,NULL,'zxcvbnmö','88658756567',18,'Kadın',750,'Aktif'),(31,'PNR991226',23,NULL,'mjuh','43658996567',26,'Erkek',750,'Aktif'),(32,'PNR598127',23,NULL,'mjuhvv','43658756667',27,'Kadın',750,'Aktif'),(33,'PNR531125',23,NULL,'uhuhuh','43658750567',25,'Kadın',750,'İptal'),(34,'PNR499931',23,NULL,'kokokok','43658756000',31,'Erkek',750,'Aktif'),(35,'PNR898211',14,NULL,'mıncırto','12345678910',11,'Erkek',750,'İptal'),(41,'PNR726017',9,NULL,'testtt','12345678945',17,'Erkek',600,'Aktif'),(43,'PNR69301',9,8,'hasan kahraman','34456677856',1,'Erkek',600,'Aktif'),(44,'PNR77447',24,7,'erikdalı','56786787654',7,'Erkek',1500,'Aktif'),(45,'PNR67939',24,9,'ahmet','45644433356',9,'Erkek',1500,'Aktif'),(46,'PNR668112',20,9,'sedf','87687687654',12,'Kadın',500,'Aktif'),(47,'PNR71978',13,1,'ıuytr','34563456567',8,'Erkek',700,'Aktif'),(48,'PNR19254',33,1,'esad','45678906577',4,'Erkek',500,'Aktif'),(49,'PNR33687',37,NULL,'ıuytre','45678945678',7,'Erkek',788,'Aktif'),(50,'PNR83592',37,NULL,'esad','45678945678',2,'Kadın',788,'Aktif'),(51,'PNR98811',37,NULL,'nhnhn','45678945678',1,'Erkek',788,'Aktif'),(52,'PNR57386',37,NULL,'rfrfr','45678945678',6,'Erkek',788,'İptal'),(53,'PNR15711',36,12,'fatih','34344545644',1,'Erkek',500,'Aktif'),(54,'PNR23685',36,12,'yıldız','34563456567',5,'Kadın',500,'Aktif'),(55,'PNR19236',36,12,'eda','78786778987',6,'Kadın',500,'İptal'),(56,'PNR924910',36,12,'ela','34534545667',10,'Kadın',500,'Aktif'),(57,'PNR81315',38,NULL,'sese','45678923456',5,'Erkek',900,'Aktif'),(58,'PNR46186',38,NULL,'tty','23232323235',6,'Erkek',900,'Aktif'),(59,'PNR877210',38,NULL,'uytre','76576576897',10,'Kadın',900,'Aktif'),(60,'PNR37567',35,13,'twst','12345621332',7,'Erkek',500,'İptal'),(61,'PNR95021',29,14,'yusuf','10008258705',1,'Erkek',2000,'Aktif'),(62,'PNR48291',51,NULL,'ali','75675656678',1,'Erkek',1000,'Aktif'),(63,'PNR19015',51,NULL,'yıldız','75675656675',5,'Kadın',1000,'Aktif'),(64,'PNR72486',51,NULL,'dicle','75675656674',6,'Kadın',1000,'Aktif'),(65,'PNR164610',51,NULL,'okay','75675656678',10,'Erkek',1000,'Aktif'),(66,'PNR305714',51,NULL,'esad','75675656672',14,'Erkek',1000,'Aktif'),(67,'PNR694718',51,NULL,'ismail','75675656679',18,'Erkek',1000,'Aktif'),(68,'PNR522117',51,NULL,'beyza','75675656670',17,'Kadın',1000,'Aktif'),(69,'PNR67725',50,NULL,'mıncırto','75675656673',5,'Erkek',2000,'Aktif'),(70,'PNR20155',49,NULL,'eda','75675656672',5,'Kadın',550,'Aktif'),(71,'PNR68526',49,NULL,'esra','75675656690',6,'Kadın',550,'Aktif'),(72,'PNR99031',48,NULL,'Umit','75675656640',1,'Erkek',1190,'Aktif'),(73,'PNR99134',48,NULL,'Dursun','75675656673',4,'Erkek',1190,'Aktif'),(74,'PNR751610',48,NULL,'ali','75675656675',10,'Erkek',1190,'Aktif'),(75,'PNR210413',48,NULL,'veli','75675656674',13,'Kadın',1190,'Aktif'),(76,'PNR34988',47,NULL,'sedat','75678756676',8,'Erkek',1300,'Aktif'),(77,'PNR98679',47,NULL,'samii','75678756672',9,'Erkek',1300,'Aktif'),(78,'PNR253914',47,NULL,'fatma','75678756676',14,'Kadın',1300,'Aktif'),(79,'PNR32138',40,NULL,'mehmet','75678756674',8,'Erkek',250,'Aktif'),(80,'PNR54089',40,NULL,'muhammed','75678756676',9,'Erkek',250,'Aktif'),(81,'PNR27554',49,12,'fatih','75678756674',4,'Erkek',550,'Aktif'),(82,'PNR25758',43,NULL,'idil','75678756677',8,'Kadın',700,'Aktif'),(83,'PNR79249',43,NULL,'mahmud','75678756672',9,'Erkek',700,'Aktif'),(84,'PNR947610',43,NULL,'zehra','75678756672',10,'Kadın',700,'Aktif'),(85,'PNR17431',41,NULL,'ali','34345676558',1,'Erkek',1190,'Aktif'),(86,'PNR14923',44,NULL,'ahu','77889955446',3,'Kadın',300,'İptal'),(87,'PNR53242',39,NULL,'tekin','67867887890',2,'Erkek',800,'Aktif'),(88,'PNR12292',42,NULL,'emin','87877887656',2,'Erkek',890,'Aktif'),(89,'PNR93503',42,NULL,'nur','87877887655',3,'Kadın',890,'Aktif'),(90,'PNR94635',42,NULL,'yavuz','87877887652',5,'Erkek',890,'İptal'),(91,'PNR88123',44,NULL,'ahu','87877887654',3,'Kadın',300,'Aktif'),(92,'PNR16902',51,12,'fatih','56768987876',2,'Erkek',1000,'İptal');
/*!40000 ALTER TABLE `biletler` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kaptanlar`
--

DROP TABLE IF EXISTS `kaptanlar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kaptanlar` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ad_soyad` varchar(100) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `telefon` varchar(15) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `ehliyet` varchar(20) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `durum` varchar(20) COLLATE utf8mb4_turkish_ci DEFAULT 'Müsait',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_turkish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kaptanlar`
--

LOCK TABLES `kaptanlar` WRITE;
/*!40000 ALTER TABLE `kaptanlar` DISABLE KEYS */;
INSERT INTO `kaptanlar` VALUES (1,'Ahmet Yılmaz','0532 100 00 01','E Sınıfı','Seferde'),(2,'Mehmet Demir','0533 200 00 02','E Sınıfı','Müsait'),(3,'Mustafa Çelik','0542 300 00 03','DE Sınıfı','Müsait'),(4,'Ayşe Kaya','0555 400 00 04','E Sınıfı','Müsait'),(6,'Ali Koç','0535 600 00 06','E Sınıfı','Müsait'),(7,'Hasan Kara','0544 700 00 07','E Sınıfı','Müsait'),(8,'Hüseyin Aydın','0536 800 00 08','E Sınıfı','Müsait'),(9,'İbrahim Polat','0537 900 00 09','D Sınıfı','Seferde'),(10,'Yusuf Şahin','0538 111 22 33','DE Sınıfı','Müsait'),(11,'Ömer Faruk','0539 222 33 44','DE Sınıfı','Seferde'),(12,'Zeynep Yıldız','0541 333 44 55','E Sınıfı','Müsait'),(13,'Burak Can','0543 444 55 66','E Sınıfı','Müsait'),(14,'Kadir İnan','0545 555 66 77','DE Sınıfı','Müsait'),(15,'Serkan Bulut','0546 666 77 88','E Sınıfı','Müsait'),(16,'Esat Kır','0554 301 50 40','DE Sınıfı','Müsait'),(17,'Berşan Okay','0544 333 67 89','D Sınıfı ','Müsait'),(18,'Esad Özkan','0566 777 33 44','DE Sınıfı','Müsait'),(19,'Sabri Gün','0531 543 21 31','D Sınıfı ','Müsait'),(20,'Mıncırto Kır','0577 566 45 32','E Sınıfı ','Müsait');
/*!40000 ALTER TABLE `kaptanlar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kullanicilar`
--

DROP TABLE IF EXISTS `kullanicilar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kullanicilar` (
  `id` int NOT NULL AUTO_INCREMENT,
  `kullanici_adi` varchar(50) COLLATE utf8mb4_turkish_ci NOT NULL,
  `sifre` varchar(50) COLLATE utf8mb4_turkish_ci NOT NULL,
  `rol` varchar(20) COLLATE utf8mb4_turkish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `kullanici_adi` (`kullanici_adi`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_turkish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kullanicilar`
--

LOCK TABLES `kullanicilar` WRITE;
/*!40000 ALTER TABLE `kullanicilar` DISABLE KEYS */;
INSERT INTO `kullanicilar` VALUES (1,'root','root','admin'),(2,'user','user','musteri'),(3,'test','123','musteri'),(4,'e204','123','musteri'),(5,'78787878788','1234','musteri'),(7,'12121212121','1234','musteri'),(9,'erikdalı','123','musteri'),(10,'hasanali123','123','musteri'),(11,'ahmet','321','musteri'),(12,'67877788899','1234','musteri'),(13,'67875788899','1234','musteri'),(14,'fatih','123','musteri'),(15,'havaliad','1234','musteri'),(16,'kralyusuf','3131','musteri');
/*!40000 ALTER TABLE `kullanicilar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `musteriler`
--

DROP TABLE IF EXISTS `musteriler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `musteriler` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `ad_soyad` varchar(100) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `tc_kimlik` varchar(11) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `telefon` varchar(15) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `cinsiyet` varchar(10) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `musteriler_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `kullanicilar` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_turkish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `musteriler`
--

LOCK TABLES `musteriler` WRITE;
/*!40000 ALTER TABLE `musteriler` DISABLE KEYS */;
INSERT INTO `musteriler` VALUES (1,2,'Esad Özkan','11111111111','05555555555','Erkek'),(2,3,'test','11111111111','05555555555','Erkek'),(3,4,'esat ozkan','22222222222','05555555555','Erkek'),(4,5,'yusuf tekin','78787878788','07888888888','Erkek'),(6,7,'abuzer','12121212121','05555555555','Erkek'),(7,9,'kayıt6','12121212676','09997776655','Erkek'),(8,10,'Hasan Kahraman','52175486302','05078965262','Erkek'),(9,11,'ahmet','22221111445','02558796341','Erkek'),(10,12,'ali şimşek','67877788899','05768768877','Erkek'),(11,13,'melih şimşek','67875788899','05763268877','Erkek'),(12,14,'nurettin fatih özkan','56785678453','0566 544 34 21','Erkek'),(13,15,'süleyman sabri gün','98721345621','05323211211','Erkek'),(14,16,'Yusuf Kaplan','10008258705','05538496433','Erkek');
/*!40000 ALTER TABLE `musteriler` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seferler`
--

DROP TABLE IF EXISTS `seferler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seferler` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nereden` varchar(50) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `nereye` varchar(50) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `tarih` varchar(20) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `saat` varchar(10) COLLATE utf8mb4_turkish_ci DEFAULT NULL,
  `fiyat` int DEFAULT NULL,
  `kaptan_id` int DEFAULT NULL,
  `arac_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `kaptan_id` (`kaptan_id`),
  KEY `arac_id` (`arac_id`),
  CONSTRAINT `seferler_ibfk_1` FOREIGN KEY (`kaptan_id`) REFERENCES `kaptanlar` (`id`) ON DELETE SET NULL,
  CONSTRAINT `seferler_ibfk_2` FOREIGN KEY (`arac_id`) REFERENCES `araclar` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_turkish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seferler`
--

LOCK TABLES `seferler` WRITE;
/*!40000 ALTER TABLE `seferler` DISABLE KEYS */;
INSERT INTO `seferler` VALUES (1,'Ankara','Çankırı','15.12.2025','09:30',250,1,1),(2,'Çankırı','Ankara','15.12.2025','14:00',250,1,1),(3,'İstanbul','Çankırı','16.12.2025','23:00',600,2,2),(4,'İstanbul','Elazığ','15.12.2025','16:00',1200,15,10),(5,'Elazığ','İstanbul','16.12.2025','13:00',1200,15,10),(6,'Ankara','Elazığ','15.12.2025','20:00',900,14,9),(7,'İstanbul','Ankara','15.12.2025','09:00',600,3,3),(8,'İstanbul','Ankara','15.12.2025','12:00',600,4,4),(9,'İstanbul','Ankara','15.12.2025','15:00',600,NULL,5),(10,'İstanbul','Ankara','15.12.2025','19:00',650,6,6),(11,'İstanbul','Ankara','15.12.2025','23:59',550,7,7),(12,'İzmir','İstanbul','15.12.2025','10:00',700,8,8),(13,'İzmir','İstanbul','15.12.2025','14:00',700,9,9),(14,'İzmir','İstanbul','15.12.2025','22:00',750,10,1),(15,'Antalya','İstanbul','15.12.2025','20:30',850,11,2),(16,'Antalya','Ankara','15.12.2025','22:00',600,12,3),(17,'Bursa','İzmir','15.12.2025','08:00',400,13,4),(18,'Gaziantep','Adana','15.12.2025','07:30',300,14,5),(19,'Trabzon','Samsun','15.12.2025','11:00',350,1,6),(20,'Samsun','Ankara','15.12.2025','16:00',500,2,7),(21,'İstanbul','Ankara','16.12.2025','10:00',600,3,8),(22,'İstanbul','Ankara','17.12.2025','10:00',600,4,9),(23,'İstanbul','İzmir','20.12.2025','09:00',750,NULL,10),(24,'Erzincan','Iğdır','18.12.2025','18:43',1500,NULL,1),(25,'Ankara','Artvin','13.12.2025','19:34',500,1,1),(26,'Adana','Artvin','11.12.2025','19:43',500,1,1),(27,'Adana','Afyonkarahisar','08.12.2025','19:43',500,1,1),(28,'Sivas','İstanbul','12.12.2025','00:48',250,11,7),(29,'Ağrı','Afyonkarahisar','24.12.2025','17:00',2000,9,3),(30,'Bolu','Ankara','18.12.2025','17:00',588,6,3),(31,'Adana','Amasya','18.12.2025','18:55',999,3,5),(32,'Ankara','Antalya','18.12.2025','16:15',788,11,7),(33,'Bolu','Çankırı','18.12.2025','17:15',500,14,9),(34,'İstanbul','Sivas','18.12.2025','17:15',1250,16,10),(35,'Erzurum','Manisa','20.12.2025','11:43',500,1,1),(36,'Adana','Ankara','22.12.2025','12:45',500,9,1),(37,'Manisa','Kastamonu','25.12.2025','11:00',788,15,7),(38,'Giresun','Hatay','22.12.2025','12:14',900,8,9),(39,'Adana','Ankara','26.12.2025','11:00',800,1,1),(40,'Ankara','Çankırı','26.12.2025','11:00',250,2,2),(41,'Ankara','İstanbul','27.12.2025','12:00',1190,18,3),(42,'Edirne','Mersin','27.12.2025','15:00',890,17,10),(43,'Ankara','İzmir','27.12.2025','16:00',700,16,6),(44,'Ankara','Konya','28.12.2025','17:00',300,16,8),(45,'Antalya','Trabzon','28.12.2025','11:00',500,15,5),(46,'Antalya','Van','26.12.2025','11:00',1100,9,5),(47,'Ankara','Şanlıurfa','26.12.2025','12:00',1300,14,8),(48,'Ankara','İstanbul','26.12.2025','11:00',1190,8,3),(49,'Ankara','İstanbul','26.12.2025','12:00',550,7,7),(50,'Hatay','Kütahya','26.12.2025','12:00',2000,13,4),(51,'Ankara','İstanbul','26.12.2025','22:00',1000,18,9),(52,'Ankara','Bolu','29.12.2025','11:14',5000,18,4);
/*!40000 ALTER TABLE `seferler` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-29 12:31:22
