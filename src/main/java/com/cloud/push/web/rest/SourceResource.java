package com.cloud.push.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.push.service.SourceService;
import com.cloud.push.web.rest.errors.BadRequestAlertException;
import com.cloud.push.web.rest.util.HeaderUtil;
import com.cloud.push.web.rest.util.PaginationUtil;
import com.cloud.push.service.dto.SourceDTO;
import com.cloud.push.service.dto.SourceCriteria;
import com.cloud.push.service.SourceQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Source.
 */
@RestController
@RequestMapping("/api")
public class SourceResource {

    private final Logger log = LoggerFactory.getLogger(SourceResource.class);

    private static final String ENTITY_NAME = "source";

    private final SourceService sourceService;

    private final SourceQueryService sourceQueryService;

    public SourceResource(SourceService sourceService, SourceQueryService sourceQueryService) {
        this.sourceService = sourceService;
        this.sourceQueryService = sourceQueryService;
    }

    /**
     * POST  /sources : Create a new source.
     *
     * @param sourceDTO the sourceDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sourceDTO, or with status 400 (Bad Request) if the source has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sources")
    @Timed
    public ResponseEntity<SourceDTO> createSource(@Valid @RequestBody SourceDTO sourceDTO) throws URISyntaxException {
        log.debug("REST request to save Source : {}", sourceDTO);
        if (sourceDTO.getId() != null) {
            throw new BadRequestAlertException("A new source cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SourceDTO result = sourceService.save(sourceDTO);
        return ResponseEntity.created(new URI("/api/sources/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sources : Updates an existing source.
     *
     * @param sourceDTO the sourceDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sourceDTO,
     * or with status 400 (Bad Request) if the sourceDTO is not valid,
     * or with status 500 (Internal Server Error) if the sourceDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sources")
    @Timed
    public ResponseEntity<SourceDTO> updateSource(@Valid @RequestBody SourceDTO sourceDTO) throws URISyntaxException {
        log.debug("REST request to update Source : {}", sourceDTO);
        if (sourceDTO.getId() == null) {
            return createSource(sourceDTO);
        }
        SourceDTO result = sourceService.save(sourceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, sourceDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sources : get all the sources.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of sources in body
     */
    @GetMapping("/sources")
    @Timed
    public ResponseEntity<List<SourceDTO>> getAllSources(SourceCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Sources by criteria: {}", criteria);
        Page<SourceDTO> page = sourceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sources");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sources/:id : get the "id" source.
     *
     * @param id the id of the sourceDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sourceDTO, or with status 404 (Not Found)
     */
    @GetMapping("/sources/{id}")
    @Timed
    public ResponseEntity<SourceDTO> getSource(@PathVariable Long id) {
        log.debug("REST request to get Source : {}", id);
        SourceDTO sourceDTO = sourceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(sourceDTO));
    }

    /**
     * DELETE  /sources/:id : delete the "id" source.
     *
     * @param id the id of the sourceDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sources/{id}")
    @Timed
    public ResponseEntity<Void> deleteSource(@PathVariable Long id) {
        log.debug("REST request to delete Source : {}", id);
        sourceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
