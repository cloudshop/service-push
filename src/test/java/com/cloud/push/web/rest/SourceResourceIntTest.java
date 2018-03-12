package com.cloud.push.web.rest;

import com.cloud.push.PushApp;

import com.cloud.push.config.SecurityBeanOverrideConfiguration;

import com.cloud.push.domain.Source;
import com.cloud.push.repository.SourceRepository;
import com.cloud.push.service.SourceService;
import com.cloud.push.service.dto.SourceDTO;
import com.cloud.push.service.mapper.SourceMapper;
import com.cloud.push.web.rest.errors.ExceptionTranslator;
import com.cloud.push.service.dto.SourceCriteria;
import com.cloud.push.service.SourceQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.cloud.push.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cloud.push.domain.enumeration.Status;
/**
 * Test class for the SourceResource REST controller.
 *
 * @see SourceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PushApp.class, SecurityBeanOverrideConfiguration.class})
public class SourceResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ENABLED;
    private static final Status UPDATED_STATUS = Status.DISABLED;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private SourceMapper sourceMapper;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private SourceQueryService sourceQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSourceMockMvc;

    private Source source;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SourceResource sourceResource = new SourceResource(sourceService, sourceQueryService);
        this.restSourceMockMvc = MockMvcBuilders.standaloneSetup(sourceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Source createEntity(EntityManager em) {
        Source source = new Source()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .desc(DEFAULT_DESC)
            .status(DEFAULT_STATUS);
        return source;
    }

    @Before
    public void initTest() {
        source = createEntity(em);
    }

    @Test
    @Transactional
    public void createSource() throws Exception {
        int databaseSizeBeforeCreate = sourceRepository.findAll().size();

        // Create the Source
        SourceDTO sourceDTO = sourceMapper.toDto(source);
        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isCreated());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeCreate + 1);
        Source testSource = sourceList.get(sourceList.size() - 1);
        assertThat(testSource.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSource.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSource.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testSource.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createSourceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sourceRepository.findAll().size();

        // Create the Source with an existing ID
        source.setId(1L);
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllSources() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get the source
        restSourceMockMvc.perform(get("/api/sources/{id}", source.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(source.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getAllSourcesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where name equals to DEFAULT_NAME
        defaultSourceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the sourceList where name equals to UPDATED_NAME
        defaultSourceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSourcesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSourceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the sourceList where name equals to UPDATED_NAME
        defaultSourceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSourcesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where name is not null
        defaultSourceShouldBeFound("name.specified=true");

        // Get all the sourceList where name is null
        defaultSourceShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where code equals to DEFAULT_CODE
        defaultSourceShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the sourceList where code equals to UPDATED_CODE
        defaultSourceShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllSourcesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where code in DEFAULT_CODE or UPDATED_CODE
        defaultSourceShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the sourceList where code equals to UPDATED_CODE
        defaultSourceShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllSourcesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where code is not null
        defaultSourceShouldBeFound("code.specified=true");

        // Get all the sourceList where code is null
        defaultSourceShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where status equals to DEFAULT_STATUS
        defaultSourceShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the sourceList where status equals to UPDATED_STATUS
        defaultSourceShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllSourcesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultSourceShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the sourceList where status equals to UPDATED_STATUS
        defaultSourceShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllSourcesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where status is not null
        defaultSourceShouldBeFound("status.specified=true");

        // Get all the sourceList where status is null
        defaultSourceShouldNotBeFound("status.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSourceShouldBeFound(String filter) throws Exception {
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSourceShouldNotBeFound(String filter) throws Exception {
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingSource() throws Exception {
        // Get the source
        restSourceMockMvc.perform(get("/api/sources/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);
        int databaseSizeBeforeUpdate = sourceRepository.findAll().size();

        // Update the source
        Source updatedSource = sourceRepository.findOne(source.getId());
        // Disconnect from session so that the updates on updatedSource are not directly saved in db
        em.detach(updatedSource);
        updatedSource
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .desc(UPDATED_DESC)
            .status(UPDATED_STATUS);
        SourceDTO sourceDTO = sourceMapper.toDto(updatedSource);

        restSourceMockMvc.perform(put("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isOk());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeUpdate);
        Source testSource = sourceList.get(sourceList.size() - 1);
        assertThat(testSource.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSource.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSource.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testSource.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingSource() throws Exception {
        int databaseSizeBeforeUpdate = sourceRepository.findAll().size();

        // Create the Source
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSourceMockMvc.perform(put("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isCreated());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);
        int databaseSizeBeforeDelete = sourceRepository.findAll().size();

        // Get the source
        restSourceMockMvc.perform(delete("/api/sources/{id}", source.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Source.class);
        Source source1 = new Source();
        source1.setId(1L);
        Source source2 = new Source();
        source2.setId(source1.getId());
        assertThat(source1).isEqualTo(source2);
        source2.setId(2L);
        assertThat(source1).isNotEqualTo(source2);
        source1.setId(null);
        assertThat(source1).isNotEqualTo(source2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SourceDTO.class);
        SourceDTO sourceDTO1 = new SourceDTO();
        sourceDTO1.setId(1L);
        SourceDTO sourceDTO2 = new SourceDTO();
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
        sourceDTO2.setId(sourceDTO1.getId());
        assertThat(sourceDTO1).isEqualTo(sourceDTO2);
        sourceDTO2.setId(2L);
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
        sourceDTO1.setId(null);
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(sourceMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(sourceMapper.fromId(null)).isNull();
    }
}
