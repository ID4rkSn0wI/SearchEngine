package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class LuceneMorphologyService {
    private static final LuceneMorphology luceneMorphology;
    private static final String wordType = "\\W\\w&&[^а-яА-Я\\s]";
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    static {
        try {
            luceneMorphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, Integer> countLemmas(String text) {
        String[] words = parseText(text);
        Map<String, Integer> lemmas = new HashMap<>();

        for (String word : words) {
            if (word.isBlank() || word.length() < 2) {
                continue;
            }

            List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
            if (anyWordBaseFormIsParticle(wordBaseForms)) {
                continue;
            }

            List<String> normalForms = luceneMorphology.getNormalForms(word);
            if (normalForms.isEmpty()) {
                continue;
            }

            String normalWord = normalForms.get(0);

            if (lemmas.containsKey(normalWord)) {
                lemmas.put(normalWord, lemmas.get(normalWord) + 1);
            } else {
                lemmas.put(normalWord, 1);
            }
        }

        return lemmas;
    }


    public Set<String> getLemmaSet(String text) {
        String[] textArray = parseText(text);
        Set<String> lemmaSet = new HashSet<>();
        for (String word : textArray) {
            if (!word.isEmpty() && isCorrectWordForm(word)) {
                List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
                if (anyWordBaseFormIsParticle(wordBaseForms)) {
                    continue;
                }
                lemmaSet.addAll(luceneMorphology.getNormalForms(word));
            }
        }
        return lemmaSet;
    }

    private boolean anyWordBaseFormIsParticle(List<String> wordBaseForms) {
        return wordBaseForms.stream().anyMatch(this::isParticle);
    }

    private boolean isParticle(String wordBase) {
        for (String property : particlesNames) {
            if (wordBase.toUpperCase().contains(property)) {
                return true;
            }
        }
        return false;
    }

    public static String[] parseText(String text) {
        return text.toLowerCase()
                .replaceAll("[^а-яё]", " ")
                .trim()
                .split("\\s+");
    }

    private boolean isCorrectWordForm(String word) {
        List<String> wordInfo = luceneMorphology.getMorphInfo(word);
        for (String morphInfo : wordInfo) {
            if (morphInfo.matches(wordType)) {
                return false;
            }
        }
        return true;
    }

    public String[] clearHtmlTags(Document document) {
        String[] result = new String[2];
        result[0] = document.getElementsByTag("title").text();
        result[1] = document.getElementsByTag("body").text();
        return result;
    }
}
