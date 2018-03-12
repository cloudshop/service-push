package com.cloud.push.service.mapper;

import com.cloud.push.domain.*;
import com.cloud.push.service.dto.MessageDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Message and its DTO MessageDTO.
 */
@Mapper(componentModel = "spring", uses = {SourceMapper.class})
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {

    @Mapping(source = "source.id", target = "sourceId")
    @Mapping(source = "source.name", target = "sourceName")
    MessageDTO toDto(Message message);

    @Mapping(source = "sourceId", target = "source")
    Message toEntity(MessageDTO messageDTO);

    default Message fromId(Long id) {
        if (id == null) {
            return null;
        }
        Message message = new Message();
        message.setId(id);
        return message;
    }
}
