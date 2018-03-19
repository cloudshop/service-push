package com.cloud.push.web.rest;

import com.codahale.metrics.annotation.Timed;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.push.PushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

import com.cloud.push.service.MessageService;
import com.cloud.push.web.rest.errors.BadRequestAlertException;
import com.cloud.push.web.rest.util.HeaderUtil;
import com.cloud.push.web.rest.util.PaginationUtil;
import com.cloud.push.service.dto.MessageDTO;
import com.cloud.push.service.dto.MessageCriteria;
import com.cloud.push.domain.Push;
import com.cloud.push.service.MessageQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Message.
 */
@RestController
@RequestMapping("/api")
public class PushResource {

    private final Logger log = LoggerFactory.getLogger(PushResource.class);

    private static final String ENTITY_NAME = "push";

    @PostMapping("/push")
    @Timed
    /**
     * @author 逍遥子
     * @email 756898059@qq.com
     * @date 2018年3月17日
     * @version 1.0
     * @param Push
     * @return PushResult
     * @throws Exception
     */
    public @ResponseBody PushResult sendPush(@RequestBody Push push) throws Exception {
        log.debug("REST request to send push : {}", push);
        if (push.getId() != null) {
            throw new BadRequestAlertException("A new message cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PushClient pushClient = new PushClient("7b86cdd203328e704718bce9", "bd5ea486e324b235c89b6340");
        PushResult pushResult = null;
        //1发送给所有人（all） 2指定regid推送
        switch (push.getType()) {
		case 1:
			pushResult = pushClient.sendPush(PushPayload.alertAll(push.getContent()));
			break;
		case 2:
			if (push.getRegIds() == null || push.getRegIds().size() < 1) {
				throw new BadRequestAlertException("推送目标regid不合法", ENTITY_NAME, "regidsError");
			}
			pushClient.sendPush(PushPayload.newBuilder()
					.setPlatform(Platform.android_ios())
					.setNotification(Notification.alert(push.getContent()))
					.setAudience(Audience.registrationId(push.getRegIds())).build());
			break;
		default:
			throw new BadRequestAlertException("推送type不合法", ENTITY_NAME, "pushTypeError");
		}
		return pushResult;
    }
    
    /**
     * POST  /messages : Create a new message.
     *
     * @param messageDTO the messageDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new messageDTO, or with status 400 (Bad Request) if the message has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     
    @PostMapping("/messages")
    @Timed
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) throws URISyntaxException {
        log.debug("REST request to save Message : {}", messageDTO);
        if (messageDTO.getId() != null) {
            throw new BadRequestAlertException("A new message cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MessageDTO result = messageService.save(messageDTO);
        return ResponseEntity.created(new URI("/api/messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }*/

    /**
     * PUT  /messages : Updates an existing message.
     *
     * @param messageDTO the messageDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated messageDTO,
     * or with status 400 (Bad Request) if the messageDTO is not valid,
     * or with status 500 (Internal Server Error) if the messageDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     
    @PutMapping("/messages")
    @Timed
    public ResponseEntity<MessageDTO> updateMessage(@RequestBody MessageDTO messageDTO) throws URISyntaxException {
        log.debug("REST request to update Message : {}", messageDTO);
        if (messageDTO.getId() == null) {
            return createMessage(messageDTO);
        }
        MessageDTO result = messageService.save(messageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, messageDTO.getId().toString()))
            .body(result);
    }*/

    /**
     * GET  /messages : get all the messages.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of messages in body
     
    @GetMapping("/messages")
    @Timed
    public ResponseEntity<List<MessageDTO>> getAllMessages(MessageCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Messages by criteria: {}", criteria);
        Page<MessageDTO> page = messageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/messages");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }*/

    /**
     * GET  /messages/:id : get the "id" message.
     *
     * @param id the id of the messageDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the messageDTO, or with status 404 (Not Found)
     
    @GetMapping("/messages/{id}")
    @Timed
    public ResponseEntity<MessageDTO> getMessage(@PathVariable Long id) {
        log.debug("REST request to get Message : {}", id);
        MessageDTO messageDTO = messageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(messageDTO));
    }*/

    /**
     * DELETE  /messages/:id : delete the "id" message.
     *
     * @param id the id of the messageDTO to delete
     * @return the ResponseEntity with status 200 (OK)
    
    @DeleteMapping("/messages/{id}")
    @Timed
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        log.debug("REST request to delete Message : {}", id);
        messageService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    } */
}
