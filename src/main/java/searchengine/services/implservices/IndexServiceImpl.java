package searchengine.services.implservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.IndexDto;
import searchengine.model.Index;
import searchengine.repositories.IndexRepo;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class IndexServiceImpl implements searchengine.services.Iservices.IndexService<IndexDto> {
    private final IndexRepo indexRepo;

    @Override
    public IndexDto getById(int id) {
        return mapToDto(indexRepo.getReferenceById(id));
    }

    @Override
    public Collection<IndexDto> getAll() {
        return indexRepo.findAll()
                .stream()
                .map(IndexServiceImpl::mapToDto)
                .toList();
    }

    @Override
    public void add(IndexDto lemmaDto) {
        indexRepo.save(mapToEntity(lemmaDto));
    }

    @Override
    public void update(IndexDto lemmaDto) {
        indexRepo.save(mapToEntity(lemmaDto));
    }

    @Override
    public void delete(Integer id) {
        indexRepo.deleteById(id);
    }

    @Override
    public void deleteAllByPageId(int pageId) {
        indexRepo.deleteAllByPageId(pageId);
    }

    @Override
    public List<Integer> getLemmaIdsByPageId(Integer pageId) {
        return indexRepo.getLemmaIdsByPageId(pageId);
    }

    @Override
    public Set<Integer> findPageIdsByLemmaId(int lemmaId) {
        return indexRepo.findPageIdsByLemmaId(lemmaId);
    }

    @Override
    public Float findRankByLemmaIdAndPageId(Integer lemmaId, Integer pageId) {
        return indexRepo.findRankByLemmaIdAndPageId(lemmaId, pageId);
    }

    private static IndexDto mapToDto(Index index) {
        IndexDto indexDto = new IndexDto();
        indexDto.setId(index.getId());
        indexDto.setRank(index.getRank());
        indexDto.setLemmaId(index.getLemmaId());
        indexDto.setPageId(index.getPageId());
        return indexDto;
    }

    private static Index mapToEntity(IndexDto indexDto) {
        Index index = new Index();
        index.setId(indexDto.getId());
        index.setRank(indexDto.getRank());
        index.setLemmaId(indexDto.getLemmaId());
        index.setPageId(indexDto.getPageId());
        return index;
    }
}
