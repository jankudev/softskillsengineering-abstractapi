package dev.janku.softskillsengineering.abstractapi.init;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SwEngPageScraperIT {

    @Test
    void initRepository() {
        final SwEngPageScraper scraper = new SwEngPageScraper();
        scraper.urlRoot = "https://softskills.audio";
        scraper.urlEpisodes = "https://softskills.audio/episodes";
        var episodes = scraper.scrapeAllEpisodesInfo();

        assertFalse(episodes.isEmpty());
    }
}