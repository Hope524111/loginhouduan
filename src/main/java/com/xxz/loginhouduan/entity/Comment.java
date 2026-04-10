package com.xxz.loginhouduan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnoreProperties("comments")  // Prevents JSON infinite recursion
    private Post post;

    private String userName;
    private String content;
    private String timeAgo;

    @Temporal(TemporalType.TIMESTAMP)  // Ensures Hibernate stores the field as a timestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    // Fixes equals() and hashCode() to exclude the post field
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
