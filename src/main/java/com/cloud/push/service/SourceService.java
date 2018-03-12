package com.cloud.push.service;

import com.cloud.push.domain.Source;
import com.cloud.push.repository.SourceRepository;
import com.cloud.push.service.dto.SourceDTO;
import com.cloud.push.service.mapper.SourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Source.
 */
@Service
@Transactional
public class SourceService {

    private final Logger log = LoggerFactory.getLogger(SourceService.class);

    private final SourceRepository sourceRepository;

    private final SourceMapper sourceMapper;

    public SourceService(SourceRepository sourceRepository, SourceMapper sourceMapper) {
        this.sourceRepository = sourceRepository;
        this.sourceMapper = sourceMapper;
    }

    /**
     * Save a source.
     *
     * @param sourceDTO the entity to save
     * @return the persisted entity
     */
    public SourceDTO save(SourceDTO sourceDTO) {
        log.debug("Request to save Source : {}", sourceDTO);
        Source source = sourceMapper.toEntity(sourceDTO);
        source = sourceRepository.save(source);
        return sourceMapper.toDto(source);
    }

    /**
     * Get all the sources.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SourceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sources");
        return sourceRepository.findAll(pageable)
            .map(sourceMapper::toDto);
    }

    /**
     * Get one source by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public SourceDTO findOne(Long id) {
        log.debug("Request to get Source : {}", id);
        Source source = sourceRepository.findOne(id);
        return sourceMapper.toDto(source);
    }

    /**
     * Delete the source by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Source : {}", id);
        sourceRepository.delete(id);
    }
}
