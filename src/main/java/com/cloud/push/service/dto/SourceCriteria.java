package com.cloud.push.service.dto;

import java.io.Serializable;
import com.cloud.push.domain.enumeration.Status;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the Source entity. This class is used in SourceResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /sources?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SourceCriteria implements Serializable {
    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private StringFilter code;

    private StatusFilter status;

    public SourceCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getCode() {
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StatusFilter getStatus() {
        return status;
    }

    public void setStatus(StatusFilter status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SourceCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (code != null ? "code=" + code + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
            "}";
    }

}
