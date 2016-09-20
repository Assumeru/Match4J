CREATE TABLE IF NOT EXISTS `lists` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8_bin NOT NULL,
  `first` varchar(255) COLLATE utf8_bin NOT NULL,
  `second` varchar(255) COLLATE utf8_bin NOT NULL,
  `password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `matches` (
  `id` int(11) NOT NULL,
  `first` int(11) NOT NULL,
  `second` int(11) NOT NULL,
  PRIMARY KEY (`id`,`first`,`second`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `words` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `list` int(11) NOT NULL,
  `type` smallint(1) NOT NULL,
  `word` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

ALTER TABLE `matches`
  ADD CONSTRAINT `matches_ibfk_1` FOREIGN KEY (`id`) REFERENCES `lists` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `matches_ibfk_2` FOREIGN KEY (`first`) REFERENCES `words` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `matches_ibfk_3` FOREIGN KEY (`second`) REFERENCES `words` (`id`) ON DELETE CASCADE;

ALTER TABLE `words`
  ADD CONSTRAINT `words_ibfk_1` FOREIGN KEY (`list`) REFERENCES `lists` (`id`) ON DELETE CASCADE;
