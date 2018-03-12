package com.cloud.push.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cid")
    private String cid;

    @Column(name = "icon")
    private String icon;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "target")
    private String target;

    @Column(name = "created_time")
    private Instant createdTime;

    @Column(name = "sent_time")
    private Instant sentTime;

    @Column(name = "retries")
    private Integer retries;

    @Column(name = "version")
    private Integer version;

    @ManyToOne
    private Source source;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public Message cid(String cid) {
        this.cid = cid;
        return this;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getIcon() {
        return icon;
    }

    public Message icon(String icon) {
        this.icon = icon;
        return this;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public Message title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public Message content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTarget() {
        return target;
    }

    public Message target(String target) {
        this.target = target;
        return this;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public Message createdTime(Instant createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getSentTime() {
        return sentTime;
    }

    public Message sentTime(Instant sentTime) {
        this.sentTime = sentTime;
        return this;
    }

    public void setSentTime(Instant sentTime) {
        this.sentTime = sentTime;
    }

    public Integer getRetries() {
        return retries;
    }

    public Message retries(Integer retries) {
        this.retries = retries;
        return this;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getVersion() {
        return version;
    }

    public Message version(Integer version) {
        this.version = version;
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Source getSource() {
        return source;
    }

    public Message source(Source source) {
        this.source = source;
        return this;
    }

    public void setSource(Source source) {
        this.source = source;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
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
