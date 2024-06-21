package searchengine.services.implservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.LemmaDto;
import searchengine.model.Lemma;
import searchengine.repositories.LemmaRepo;
import searchengine.services.Iservices.LemmaService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LemmaServiceImpl implements LemmaService<LemmaDto> {
    private final LemmaRepo lemmaRepo;

    @Override
    public LemmaDto getById(int id) {
        return mapToDto(lemmaRepo.getReferenceById(id));
    }

    @Override
    public Collection<LemmaDto> getAll() {
        return lemmaRepo.findAll()
                .stream()
                .map(LemmaServiceImpl::mapToDto)
                .toList();
    }

    @Override
    public void add(LemmaDto lemmaDto) {
        lemmaRepo.save(mapToEntity(lemmaDto));
    }

    @Override
    public void update(LemmaDto lemmaDto) {
        lemmaRepo.save(mapToEntity(lemmaDto));
    }

    @Override
    public void delete(Integer id) {
        lemmaRepo.deleteById(id);
    }

    @Override
    public LemmaDto findLemmaDtoByLemmaAndSiteId(String lemma, int siteId) {
        Lemma lemmaEntity = lemmaRepo.findLemmaDtoByLemmaAndSiteId(lemma, siteId);
        if (lemmaEntity == null) {
            return new LemmaDto();
        }
        return mapToDto(lemmaEntity);
    }

    @Override
    public Integer countBySiteId(int siteId) {
        return lemmaRepo.countBySiteId(siteId);
    }

    @Override
    public Integer getIdByLemmaAndSiteId(String lemma, int siteId) {
        return lemmaRepo.getIdByLemmaAndSiteId(lemma, siteId);
    }

    @Override
    public void deleteAllByIds(Collection<Integer> lemmaIds) {
        lemmaRepo.deleteAllByIds(lemmaIds);
    }

    @Override
    public List<LemmaDto> findLemmasDtoByLemma(String lemma) {
        List<Lemma> lemmas = lemmaRepo.findLemmasDtoByLemma(lemma);
        if (lemmas.isEmpty()) {
            return new ArrayList<>();
        }
        return lemmas.stream().map(LemmaServiceImpl::mapToDto).toList();
    }

    public static LemmaDto mapToDto(Lemma lemma) {
        LemmaDto lemmaDto = new LemmaDto();
        if (lemma != null) {
            lemmaDto.setId(lemma.getId());
            lemmaDto.setLemma(lemma.getLemma());
            lemmaDto.setSiteId(lemma.getSiteId());
            lemmaDto.setFrequency(lemma.getFrequency());
        }
        return lemmaDto;
    }

    public static Lemma mapToEntity(LemmaDto lemmaDto) {
        Lemma lemma = new Lemma();
        lemma.setId(lemmaDto.getId());
        lemma.setLemma(lemmaDto.getLemma());
        lemma.setSiteId(lemmaDto.getSiteId());
        lemma.setFrequency(lemmaDto.getFrequency());
        return lemma;
    }
}
