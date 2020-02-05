package dev.janku.softskillsengineering.abstractapi.init;

import dev.janku.softskillsengineering.abstractapi.core.domain.Episode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SwEngPageScraper {

    private static final Logger log = LoggerFactory.getLogger(SwEngPageScraper.class.getName());

    @NonNull
    @Value("${softSkillsEngineering.url.root}")
    String urlRoot;
    @NonNull
    @Value("${softSkillsEngineering.url.episodes}")
    String urlEpisodes;

    private static Episode.Builder extractEpisodeInformation(Document element) {
        final var builder = new Episode.Builder();
        builder.id(Integer.parseInt(
                element.select("article > div > div > h1").text()
                .replaceFirst("[^\\d]*", "")
                .replaceFirst("[:\\s].*", "")));
        builder.name(element.select("article > div > div > h1").text());
        builder.date(LocalDate.parse(element.select("article > div > div > p > time").text(),
                DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)));
        builder.description(element.select("article > div:nth-child(3)").html()
                .replaceAll("\\s*<p>\\s*", "")
                .replaceAll("\\s*</p>\\s*", "\n")
                .replaceAll("\\s*<ol>\\s*", "")
                .replaceAll("\\s*</ol>\\s*", "")
                .replaceAll("\\s*</li>\\s*", "\n")
                .replaceAll("\\s*<li>\\s*", "\n- ")
        );
        return builder;
    }

    private static Document followLinkToDetailPage(Element element, String rootUrl) {
        final var episodeUrl = element.attr("href");
        try {
            return Jsoup.connect(rootUrl + episodeUrl).get();
        } catch (IOException e) {
            log.error("Failed parsing episode info at {}", episodeUrl, e);
            return null;
        }
    }

    private static Episode.Builder appendNotes(Episode.Builder builder) {
        try {
            final var fileNotes = ResourceUtils.getFile("notes/" + builder.getId() + ".note");
            if (fileNotes.exists()) {
                builder.notes(new String(FileCopyUtils.copyToByteArray(fileNotes)));
            }
        } catch (IOException e) {
            log.error("Could not read additional notes for episode {}", builder.getId(), e);
        }
        return builder;
    }

    public List<Episode> scrapeAllEpisodesInfo() {
        try {
            
            final var swEpisodesPage = Jsoup.connect(urlEpisodes).get();
            return swEpisodesPage.select("article > h1 > a").stream()
                    .map(element -> SwEngPageScraper.followLinkToDetailPage(element, urlRoot))
                    .filter(Objects::nonNull)
                    .map(SwEngPageScraper::extractEpisodeInformation)
                    .map(SwEngPageScraper::appendNotes)
                    .map(Episode.Builder::build)
                    .collect(Collectors.toUnmodifiableList());

        } catch (IOException e) {
            log.error("Failed initialization", e);
            return Collections.emptyList();
        }
    }
}
