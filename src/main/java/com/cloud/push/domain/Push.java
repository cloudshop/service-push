package com.cloud.push.domain;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A Push.
 */
public class Push implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id; //暂留

    private String cid;

    private String icon;

    private String title;

    private String content;//内容

    private String target;
    
    private List<String> regIds;//推送设备id

    private Instant createdTime;

    private Instant sentTime;

    private Integer retries;

    private Integer version;
    
    private Integer type; //1发送给所有人（all） 2指定regid推送

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public Push cid(String cid) {
        this.cid = cid;
        return this;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getIcon() {
        return icon;
    }

    public Push icon(String icon) {
        this.icon = icon;
        return this;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public Push title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public Push content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTarget() {
        return target;
    }

    public Push target(String target) {
        this.target = target;
        return this;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public Push createdTime(Instant createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getSentTime() {
        return sentTime;
    }

    public Push sentTime(Instant sentTime) {
        this.sentTime = sentTime;
        return this;
    }

    public void setSentTime(Instant sentTime) {
        this.sentTime = sentTime;
    }

    public Integer getRetries() {
        return retries;
    }

    public Push retries(Integer retries) {
        this.retries = retries;
        return this;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getVersion() {
        return version;
    }

    public Push version(Integer version) {
        this.version = version;
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<String> getRegIds() {
		return regIds;
	}

	public void setRegIds(List<String> regIds) {
		this.regIds = regIds;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Push message = (Push) o;
        if (message.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", cid='" + getCid() + "'" +
            ", icon='" + getIcon() + "'" +
            ", title='" + getTitle() + "'" +
            ", content='" + getContent() + "'" +
            ", target='" + getTarget() + "'" +
            ", createdTime='" + getCreatedTime() + "'" +
            ", sentTime='" + getSentTime() + "'" +
            ", retries=" + getRetries() +
            ", version=" + getVersion() +
            "}";
    }
}
