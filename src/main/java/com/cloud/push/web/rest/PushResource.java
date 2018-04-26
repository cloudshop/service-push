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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @author 逍遥子
     * @email 756898059@qq.com
     * @date 2018年3月17日
     * @version 1.0
     * @param Push
     * @return PushResult
     * @throws Exception
     */
    @PostMapping("/push")
    @Timed
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
     * 根据用户id推送
     * @author 逍遥子
     * @email 756898059@qq.com
     * @date 2018年4月26日
     * @version 1.0
     * @param userid
     * @param content
     */
    @PostMapping("/push/user/{userid}")
    public void sendPushByUserid(@PathVariable("userid") String userid,@RequestBody String content) {
    	ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
    	String registrationID = opsForValue.get("gongrong_jpush_registrationID_userid_"+userid);
    	PushClient pushClient = new PushClient("7b86cdd203328e704718bce9", "bd5ea486e324b235c89b6340");
    	try {
			PushResult sendPush = pushClient.sendPush(PushPayload.newBuilder()
					.setPlatform(Platform.android_ios())
			    	.setNotification(Notification.alert(content))
			    	.setAudience(Audience.registrationId(registrationID)).build());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 群退
     * @author 逍遥子
     * @email 756898059@qq.com
     * @date 2018年4月26日
     * @version 1.0
     * @param userid
     * @param content
     */
    @PostMapping("/push/all")
    public void sendPushToAll(@RequestBody String content) {
    	PushClient pushClient = new PushClient("7b86cdd203328e704718bce9", "bd5ea486e324b235c89b6340");
    	try {
    		pushClient.sendPush(PushPayload.alertAll(content));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
}
