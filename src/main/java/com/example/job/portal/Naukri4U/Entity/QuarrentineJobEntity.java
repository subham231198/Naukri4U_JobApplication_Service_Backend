package com.example.job.portal.Naukri4U.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quarrentine_job")
public class QuarrentineJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "job_req_id")
    @Column(nullable = false)
    private String jobReqId;

    @JsonProperty(value = "added_by")
    @Column(nullable = false)
    private String addedBy;

    @JsonProperty(value = "quarentine_timestamp")
    @Column(nullable = false)
    private String quarentineTimeStamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobReqId() {
        return jobReqId;
    }

    public void setJobReqId(String jobReqId) {
        this.jobReqId = jobReqId;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getQuarentineTimeStamp() {
        return quarentineTimeStamp;
    }

    public void setQuarentineTimeStamp(String quarentineTimeStamp) {
        this.quarentineTimeStamp = quarentineTimeStamp;
    }
}
