package com.cloud.push.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import io.github.jhipster.service.filter.InstantFilter;




/**
 * Criteria class for the Message entity. This class is used in MessageResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /messages?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MessageCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter cid;

    private StringFilter icon;

    private StringFilter title;

    private StringFilter content;

    private StringFilter target;

    private InstantFilter createdTime;

    private InstantFilter sentTime;

    private IntegerFilter retries;

    private IntegerFilter version;

    private LongFilter sourceId;

    public MessageCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCid() {
        return cid;
    }

    public void setCid(StringFilter cid) {
        this.cid = cid;
    }

    public StringFilter getIcon() {
        return icon;
    }

    public void setIcon(StringFilter icon) {
        this.icon = icon;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getContent() {
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public StringFilter getTarget() {
        return target;
    }

    public void setTarget(StringFilter target) {
        this.target = target;
    }

    public InstantFilter getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(InstantFilter createdTime) {
        this.createdTime = createdTime;
    }

    public InstantFilter getSentTime() {
        return sentTime;
    }

    public void setSentTime(InstantFilter sentTime) {
        this.sentTime = sentTime;
    }

    public IntegerFilter getRetries() {
        return retries;
    }

    public void setRetries(IntegerFilter retries) {
        this.retries = retries;
    }

    public IntegerFilter getVersion() {
        return version;
    }

    public void setVersion(IntegerFilter version) {
        this.version = version;
    }

    public LongFilter getSourceId() {
        return sourceId;
    }

    public void setSourceId(LongFilter sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString() {
        return "MessageCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (cid != null ? "cid=" + cid + ", " : "") +
                (icon != null ? "icon=" + icon + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (target != null ? "target=" + target + ", " : "") +
                (createdTime != null ? "createdTime=" + createdTime + ", " : "") +
                (sentTime != null ? "sentTime=" + sentTime + ", " : "") +
                (retries != null ? "retries=" + retries + ", " : "") +
                (version != null ? "version=" + version + ", " : "") +
                (sourceId != null ? "sourceId=" + sourceId + ", " : "") +
            "}";
    }

}
