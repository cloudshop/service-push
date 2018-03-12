package com.cloud.push.web.rest;

import com.cloud.push.PushApp;

import com.cloud.push.config.SecurityBeanOverrideConfiguration;

import com.cloud.push.domain.Message;
import com.cloud.push.domain.Source;
import com.cloud.push.repository.MessageRepository;
import com.cloud.push.service.MessageService;
import com.cloud.push.service.dto.MessageDTO;
import com.cloud.push.service.mapper.MessageMapper;
import com.cloud.push.web.rest.errors.ExceptionTranslator;
import com.cloud.push.service.dto.MessageCriteria;
import com.cloud.push.service.MessageQueryService;

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

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.cloud.push.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MessageResource REST controller.
 *
 * @see MessageResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PushApp.class, SecurityBeanOverrideConfiguration.class})
public class MessageResourceIntTest {

    private static final String DEFAULT_CID = "AAAAAAAAAA";
    private static final String UPDATED_CID = "BBBBBBBBBB";

    private static final String DEFAULT_ICON = "AAAAAAAAAA";
    private static final String UPDATED_ICON = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_TARGET = "AAAAAAAAAA";
    private static final String UPDATED_TARGET = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SENT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_RETRIES = 1;
    private static final Integer UPDATED_RETRIES = 2;

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageQueryService messageQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMessageMockMvc;

    private Message message;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MessageResource messageResource = new MessageResource(messageService, messageQueryService);
        this.restMessageMockMvc = MockMvcBuilders.standaloneSetup(messageResource)
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
    public static Message createEntity(EntityManager em) {
        Message message = new Message()
            .cid(DEFAULT_CID)
            .icon(DEFAULT_ICON)
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .target(DEFAULT_TARGET)
            .createdTime(DEFAULT_CREATED_TIME)
            .sentTime(DEFAULT_SENT_TIME)
            .retries(DEFAULT_RETRIES)
            .version(DEFAULT_VERSION);
        return message;
    }

    @Before
    public void initTest() {
        message = createEntity(em);
    }

    @Test
    @Transactional
    public void createMessage() throws Exception {
        int databaseSizeBeforeCreate = messageRepository.findAll().size();

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);
        restMessageMockMvc.perform(post("/api/messages")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isCreated());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate + 1);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getCid()).isEqualTo(DEFAULT_CID);
        assertThat(testMessage.getIcon()).isEqualTo(DEFAULT_ICON);
        assertThat(testMessage.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testMessage.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testMessage.getTarget()).isEqualTo(DEFAULT_TARGET);
        assertThat(testMessage.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testMessage.getSentTime()).isEqualTo(DEFAULT_SENT_TIME);
        assertThat(testMessage.getRetries()).isEqualTo(DEFAULT_RETRIES);
        assertThat(testMessage.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    public void createMessageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = messageRepository.findAll().size();

        // Create the Message with an existing ID
        message.setId(1L);
        MessageDTO messageDTO = messageMapper.toDto(message);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMessageMockMvc.perform(post("/api/messages")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMessages() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList
        restMessageMockMvc.perform(get("/api/messages?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].cid").value(hasItem(DEFAULT_CID.toString())))
            .andExpect(jsonPath("$.[*].icon").value(hasItem(DEFAULT_ICON.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].target").value(hasItem(DEFAULT_TARGET.toString())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].sentTime").value(hasItem(DEFAULT_SENT_TIME.toString())))
            .andExpect(jsonPath("$.[*].retries").value(hasItem(DEFAULT_RETRIES)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }

    @Test
    @Transactional
    public void getMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get the message
        restMessageMockMvc.perform(get("/api/messages/{id}", message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(message.getId().intValue()))
            .andExpect(jsonPath("$.cid").value(DEFAULT_CID.toString()))
            .andExpect(jsonPath("$.icon").value(DEFAULT_ICON.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.target").value(DEFAULT_TARGET.toString()))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.sentTime").value(DEFAULT_SENT_TIME.toString()))
            .andExpect(jsonPath("$.retries").value(DEFAULT_RETRIES))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION));
    }

    @Test
    @Transactional
    public void getAllMessagesByCidIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where cid equals to DEFAULT_CID
        defaultMessageShouldBeFound("cid.equals=" + DEFAULT_CID);

        // Get all the messageList where cid equals to UPDATED_CID
        defaultMessageShouldNotBeFound("cid.equals=" + UPDATED_CID);
    }

    @Test
    @Transactional
    public void getAllMessagesByCidIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where cid in DEFAULT_CID or UPDATED_CID
        defaultMessageShouldBeFound("cid.in=" + DEFAULT_CID + "," + UPDATED_CID);

        // Get all the messageList where cid equals to UPDATED_CID
        defaultMessageShouldNotBeFound("cid.in=" + UPDATED_CID);
    }

    @Test
    @Transactional
    public void getAllMessagesByCidIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where cid is not null
        defaultMessageShouldBeFound("cid.specified=true");

        // Get all the messageList where cid is null
        defaultMessageShouldNotBeFound("cid.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesByIconIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where icon equals to DEFAULT_ICON
        defaultMessageShouldBeFound("icon.equals=" + DEFAULT_ICON);

        // Get all the messageList where icon equals to UPDATED_ICON
        defaultMessageShouldNotBeFound("icon.equals=" + UPDATED_ICON);
    }

    @Test
    @Transactional
    public void getAllMessagesByIconIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where icon in DEFAULT_ICON or UPDATED_ICON
        defaultMessageShouldBeFound("icon.in=" + DEFAULT_ICON + "," + UPDATED_ICON);

        // Get all the messageList where icon equals to UPDATED_ICON
        defaultMessageShouldNotBeFound("icon.in=" + UPDATED_ICON);
    }

    @Test
    @Transactional
    public void getAllMessagesByIconIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where icon is not null
        defaultMessageShouldBeFound("icon.specified=true");

        // Get all the messageList where icon is null
        defaultMessageShouldNotBeFound("icon.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where title equals to DEFAULT_TITLE
        defaultMessageShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the messageList where title equals to UPDATED_TITLE
        defaultMessageShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllMessagesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultMessageShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the messageList where title equals to UPDATED_TITLE
        defaultMessageShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllMessagesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where title is not null
        defaultMessageShouldBeFound("title.specified=true");

        // Get all the messageList where title is null
        defaultMessageShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where content equals to DEFAULT_CONTENT
        defaultMessageShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the messageList where content equals to UPDATED_CONTENT
        defaultMessageShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllMessagesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultMessageShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the messageList where content equals to UPDATED_CONTENT
        defaultMessageShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllMessagesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where content is not null
        defaultMessageShouldBeFound("content.specified=true");

        // Get all the messageList where content is null
        defaultMessageShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesByTargetIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where target equals to DEFAULT_TARGET
        defaultMessageShouldBeFound("target.equals=" + DEFAULT_TARGET);

        // Get all the messageList where target equals to UPDATED_TARGET
        defaultMessageShouldNotBeFound("target.equals=" + UPDATED_TARGET);
    }

    @Test
    @Transactional
    public void getAllMessagesByTargetIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where target in DEFAULT_TARGET or UPDATED_TARGET
        defaultMessageShouldBeFound("target.in=" + DEFAULT_TARGET + "," + UPDATED_TARGET);

        // Get all the messageList where target equals to UPDATED_TARGET
        defaultMessageShouldNotBeFound("target.in=" + UPDATED_TARGET);
    }

    @Test
    @Transactional
    public void getAllMessagesByTargetIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where target is not null
        defaultMessageShouldBeFound("target.specified=true");

        // Get all the messageList where target is null
        defaultMessageShouldNotBeFound("target.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdTime equals to DEFAULT_CREATED_TIME
        defaultMessageShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the messageList where createdTime equals to UPDATED_CREATED_TIME
        defaultMessageShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllMessagesByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultMessageShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the messageList where createdTime equals to UPDATED_CREATED_TIME
        defaultMessageShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllMessagesByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdTime is not null
        defaultMessageShouldBeFound("createdTime.specified=true");

        // Get all the messageList where createdTime is null
        defaultMessageShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesBySentTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where sentTime equals to DEFAULT_SENT_TIME
        defaultMessageShouldBeFound("sentTime.equals=" + DEFAULT_SENT_TIME);

        // Get all the messageList where sentTime equals to UPDATED_SENT_TIME
        defaultMessageShouldNotBeFound("sentTime.equals=" + UPDATED_SENT_TIME);
    }

    @Test
    @Transactional
    public void getAllMessagesBySentTimeIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where sentTime in DEFAULT_SENT_TIME or UPDATED_SENT_TIME
        defaultMessageShouldBeFound("sentTime.in=" + DEFAULT_SENT_TIME + "," + UPDATED_SENT_TIME);

        // Get all the messageList where sentTime equals to UPDATED_SENT_TIME
        defaultMessageShouldNotBeFound("sentTime.in=" + UPDATED_SENT_TIME);
    }

    @Test
    @Transactional
    public void getAllMessagesBySentTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where sentTime is not null
        defaultMessageShouldBeFound("sentTime.specified=true");

        // Get all the messageList where sentTime is null
        defaultMessageShouldNotBeFound("sentTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesByRetriesIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where retries equals to DEFAULT_RETRIES
        defaultMessageShouldBeFound("retries.equals=" + DEFAULT_RETRIES);

        // Get all the messageList where retries equals to UPDATED_RETRIES
        defaultMessageShouldNotBeFound("retries.equals=" + UPDATED_RETRIES);
    }

    @Test
    @Transactional
    public void getAllMessagesByRetriesIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where retries in DEFAULT_RETRIES or UPDATED_RETRIES
        defaultMessageShouldBeFound("retries.in=" + DEFAULT_RETRIES + "," + UPDATED_RETRIES);

        // Get all the messageList where retries equals to UPDATED_RETRIES
        defaultMessageShouldNotBeFound("retries.in=" + UPDATED_RETRIES);
    }

    @Test
    @Transactional
    public void getAllMessagesByRetriesIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where retries is not null
        defaultMessageShouldBeFound("retries.specified=true");

        // Get all the messageList where retries is null
        defaultMessageShouldNotBeFound("retries.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesByRetriesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where retries greater than or equals to DEFAULT_RETRIES
        defaultMessageShouldBeFound("retries.greaterOrEqualThan=" + DEFAULT_RETRIES);

        // Get all the messageList where retries greater than or equals to UPDATED_RETRIES
        defaultMessageShouldNotBeFound("retries.greaterOrEqualThan=" + UPDATED_RETRIES);
    }

    @Test
    @Transactional
    public void getAllMessagesByRetriesIsLessThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where retries less than or equals to DEFAULT_RETRIES
        defaultMessageShouldNotBeFound("retries.lessThan=" + DEFAULT_RETRIES);

        // Get all the messageList where retries less than or equals to UPDATED_RETRIES
        defaultMessageShouldBeFound("retries.lessThan=" + UPDATED_RETRIES);
    }


    @Test
    @Transactional
    public void getAllMessagesByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where version equals to DEFAULT_VERSION
        defaultMessageShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the messageList where version equals to UPDATED_VERSION
        defaultMessageShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllMessagesByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultMessageShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the messageList where version equals to UPDATED_VERSION
        defaultMessageShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllMessagesByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where version is not null
        defaultMessageShouldBeFound("version.specified=true");

        // Get all the messageList where version is null
        defaultMessageShouldNotBeFound("version.specified=false");
    }

    @Test
    @Transactional
    public void getAllMessagesByVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where version greater than or equals to DEFAULT_VERSION
        defaultMessageShouldBeFound("version.greaterOrEqualThan=" + DEFAULT_VERSION);

        // Get all the messageList where version greater than or equals to UPDATED_VERSION
        defaultMessageShouldNotBeFound("version.greaterOrEqualThan=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllMessagesByVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where version less than or equals to DEFAULT_VERSION
        defaultMessageShouldNotBeFound("version.lessThan=" + DEFAULT_VERSION);

        // Get all the messageList where version less than or equals to UPDATED_VERSION
        defaultMessageShouldBeFound("version.lessThan=" + UPDATED_VERSION);
    }


    @Test
    @Transactional
    public void getAllMessagesBySourceIsEqualToSomething() throws Exception {
        // Initialize the database
        Source source = SourceResourceIntTest.createEntity(em);
        em.persist(source);
        em.flush();
        message.setSource(source);
        messageRepository.saveAndFlush(message);
        Long sourceId = source.getId();

        // Get all the messageList where source equals to sourceId
        defaultMessageShouldBeFound("sourceId.equals=" + sourceId);

        // Get all the messageList where source equals to sourceId + 1
        defaultMessageShouldNotBeFound("sourceId.equals=" + (sourceId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultMessageShouldBeFound(String filter) throws Exception {
        restMessageMockMvc.perform(get("/api/messages?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].cid").value(hasItem(DEFAULT_CID.toString())))
            .andExpect(jsonPath("$.[*].icon").value(hasItem(DEFAULT_ICON.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].target").value(hasItem(DEFAULT_TARGET.toString())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].sentTime").value(hasItem(DEFAULT_SENT_TIME.toString())))
            .andExpect(jsonPath("$.[*].retries").value(hasItem(DEFAULT_RETRIES)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultMessageShouldNotBeFound(String filter) throws Exception {
        restMessageMockMvc.perform(get("/api/messages?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingMessage() throws Exception {
        // Get the message
        restMessageMockMvc.perform(get("/api/messages/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message
        Message updatedMessage = messageRepository.findOne(message.getId());
        // Disconnect from session so that the updates on updatedMessage are not directly saved in db
        em.detach(updatedMessage);
        updatedMessage
            .cid(UPDATED_CID)
            .icon(UPDATED_ICON)
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .target(UPDATED_TARGET)
            .createdTime(UPDATED_CREATED_TIME)
            .sentTime(UPDATED_SENT_TIME)
            .retries(UPDATED_RETRIES)
            .version(UPDATED_VERSION);
        MessageDTO messageDTO = messageMapper.toDto(updatedMessage);

        restMessageMockMvc.perform(put("/api/messages")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getCid()).isEqualTo(UPDATED_CID);
        assertThat(testMessage.getIcon()).isEqualTo(UPDATED_ICON);
        assertThat(testMessage.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testMessage.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testMessage.getTarget()).isEqualTo(UPDATED_TARGET);
        assertThat(testMessage.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testMessage.getSentTime()).isEqualTo(UPDATED_SENT_TIME);
        assertThat(testMessage.getRetries()).isEqualTo(UPDATED_RETRIES);
        assertThat(testMessage.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void updateNonExistingMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMessageMockMvc.perform(put("/api/messages")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isCreated());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);
        int databaseSizeBeforeDelete = messageRepository.findAll().size();

        // Get the message
        restMessageMockMvc.perform(delete("/api/messages/{id}", message.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Message.class);
        Message message1 = new Message();
        message1.setId(1L);
        Message message2 = new Message();
        message2.setId(message1.getId());
        assertThat(message1).isEqualTo(message2);
        message2.setId(2L);
        assertThat(message1).isNotEqualTo(message2);
        message1.setId(null);
        assertThat(message1).isNotEqualTo(message2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MessageDTO.class);
        MessageDTO messageDTO1 = new MessageDTO();
        messageDTO1.setId(1L);
        MessageDTO messageDTO2 = new MessageDTO();
        assertThat(messageDTO1).isNotEqualTo(messageDTO2);
        messageDTO2.setId(messageDTO1.getId());
        assertThat(messageDTO1).isEqualTo(messageDTO2);
        messageDTO2.setId(2L);
        assertThat(messageDTO1).isNotEqualTo(messageDTO2);
        messageDTO1.setId(null);
        assertThat(messageDTO1).isNotEqualTo(messageDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(messageMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(messageMapper.fromId(null)).isNull();
    }
}
