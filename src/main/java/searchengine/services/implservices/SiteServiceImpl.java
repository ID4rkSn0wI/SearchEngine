package searchengine.services.implservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.Site;
import searchengine.repositories.PageRepo;
import searchengine.repositories.SiteRepo;
import searchengine.services.indexing_services.SiteService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class SiteServiceImpl implements SiteService<SiteDto> {
    private final SiteRepo siteRepo;
    private final PageRepo pageRepo;

    @Override
    public SiteDto getById(int id) {
        log.info("Getting site by id: {}", id);
        return mapToDto(siteRepo.getReferenceById(id));
    }

    @Override
    public Collection<SiteDto> getAll() {
        log.info("Getting all sites");
        return siteRepo.findAll()
                .stream()
                .map(SiteServiceImpl::mapToDto)
                .toList();
    }

    @Override
    public void add(SiteDto site) {
        log.info("Adding site: {}", site.getUrl());
        siteRepo.save(mapToEntity(site));
    }

    @Override
    public void update(SiteDto site) {
        log.info("Updating site: {}", site.getUrl());
        siteRepo.save(mapToEntity(site));
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting site by id: {}", id);
        siteRepo.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.info("Deleting all sites");
        siteRepo.deleteAll();
    }

    @Override
    public SiteDto findSiteByUrl(String path) {
        log.info("Finding site by url: {}", path);
        return siteRepo.findByUrl(path) == null ? null : mapToDto(siteRepo.findByUrl(path));
    }

    private static SiteDto mapToDto(Site site) {
        SiteDto siteDto = new SiteDto();
        siteDto.setId(site.getId());
        siteDto.setName(site.getName());
        siteDto.setUrl(site.getUrl());
        siteDto.setStatus(site.getStatus());
        siteDto.setLastError(site.getLastError());
        siteDto.setStatusTime(site.getStatusTime());
        return siteDto;
    }

    private static Site mapToEntity(SiteDto siteDto) {
        Site site = new Site();
        site.setId(siteDto.getId());
        site.setName(siteDto.getName());
        site.setUrl(siteDto.getUrl());
        site.setStatus(siteDto.getStatus());
        site.setLastError(siteDto.getLastError());
        site.setStatusTime(siteDto.getStatusTime());
        return site;
    }
}
