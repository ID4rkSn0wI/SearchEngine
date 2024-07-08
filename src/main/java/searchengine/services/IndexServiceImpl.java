package searchengine.services;

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
public class IndexServiceImpl implements IndexService<IndexDto> {
    private final IndexRepo indexRepo;

    @Override
    public Collection<IndexDto> getAll() {
        return indexRepo.findAll()
                .stream()
                .map(IndexServiceImpl::mapToDto)
                .toList();
    }

    @Override
    public void addAll(Collection<IndexDto> indexes) {
        indexRepo.saveAll(indexes.stream().map(IndexServiceImpl::mapToEntity).toList());
    }

    @Override
    public void update(IndexDto indexDto) {
        indexRepo.save(mapToEntity(indexDto));
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

    @Override
    public void truncate() {
        indexRepo.truncate();
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
