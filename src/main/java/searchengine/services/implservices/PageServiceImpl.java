package searchengine.services.implservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.PageDto;
import searchengine.model.Page;
import searchengine.repositories.PageRepo;
import searchengine.services.indexing_services.PageService;

import java.util.Collection;


@Slf4j
@RequiredArgsConstructor
@Service
public class PageServiceImpl implements PageService<PageDto> {
    private final PageRepo pageRepo;

    @Override
    public PageDto getById(int id) {
        log.info("Getting page by id: {}", id);
        Page page = pageRepo.findById(id).orElse(new Page());
        return mapToDto(page);
    }

    @Override
    public Collection<PageDto> getAll() {
        return pageRepo.findAll()
                .stream()
                .map(PageServiceImpl::mapToDto)
                .toList();
    }

    @Override
    public void add(PageDto pageDto) {
        pageRepo.save(mapToEntity(pageDto));
    }

    @Override
    public void update(PageDto pageDto) {
        log.info("Updating page: {}", pageDto);
        pageRepo.save(mapToEntity(pageDto));
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting page: {}", id);
        pageRepo.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.info("Deleting all pages");
        pageRepo.deleteAll();
    }

    @Override
    public void addWithDefineErrorCode(PageDto pageDto, int code) {
        log.info("Adding page with error code: {}", code);
        pageDto.setCode(code);
        pageDto.setContent("");
        pageRepo.save(mapToEntity(pageDto));
    }

    @Override
    public Integer findIdByPathAndSiteId(String path, int siteId) {
        return pageRepo.findIdByPathAndSiteId(path, siteId);
    }

    @Override
    public Integer countBySiteId(int siteId) {
        return pageRepo.countBySiteId(siteId);
    }

    private static PageDto mapToDto(Page page) {
        PageDto pageDto = new PageDto();
        pageDto.setId(page.getId());
        pageDto.setPath(page.getPath());
        pageDto.setCode(page.getCode());
        pageDto.setContent(page.getContent());
        pageDto.setSiteId(page.getSiteId());
        return pageDto;
    }

    private static Page mapToEntity(PageDto pageDto) {
        Page page = new Page();
        if (pageDto.getId() != null) {
            page.setId(pageDto.getId());
        }
        page.setPath(pageDto.getPath());
        page.setCode(pageDto.getCode());
        page.setContent(pageDto.getContent());
        page.setSiteId(pageDto.getSiteId());
        return page;
    }
}
