package com.cloud.push.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.cloud.push.domain.Message;
import com.cloud.push.domain.*; // for static metamodels
import com.cloud.push.repository.MessageRepository;
import com.cloud.push.service.dto.MessageCriteria;

import com.cloud.push.service.dto.MessageDTO;
import com.cloud.push.service.mapper.MessageMapper;

/**
 * Service for executing complex queries for Message entities in the database.
 * The main input is a {@link MessageCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MessageDTO} or a {@link Page} of {@link MessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MessageQueryService extends QueryService<Message> {

    private final Logger log = LoggerFactory.getLogger(MessageQueryService.class);


    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;

    public MessageQueryService(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    /**
     * Return a {@link List} of {@link MessageDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> findByCriteria(MessageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Message> specification = createSpecification(criteria);
        return messageMapper.toDto(messageRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MessageDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MessageDTO> findByCriteria(MessageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Message> specification = createSpecification(criteria);
        final Page<Message> result = messageRepository.findAll(specification, page);
        return result.map(messageMapper::toDto);
    }

    /**
     * Function to convert MessageCriteria to a {@link Specifications}
     */
    private Specifications<Message> createSpecification(MessageCriteria criteria) {
        Specifications<Message> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Message_.id));
            }
            if (criteria.getCid() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCid(), Message_.cid));
            }
            if (criteria.getIcon() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIcon(), Message_.icon));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Message_.title));
            }
            if (criteria.getContent() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContent(), Message_.content));
            }
            if (criteria.getTarget() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTarget(), Message_.target));
            }
            if (criteria.getCreatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedTime(), Message_.createdTime));
            }
            if (criteria.getSentTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSentTime(), Message_.sentTime));
            }
            if (criteria.getRetries() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRetries(), Message_.retries));
            }
            if (criteria.getVersion() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVersion(), Message_.version));
            }
            if (criteria.getSourceId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getSourceId(), Message_.source, Source_.id));
            }
        }
        return specification;
    }

}
