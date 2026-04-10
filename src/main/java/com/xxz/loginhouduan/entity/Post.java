package com.xxz.loginhouduan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.BatchSize;
import javax.persistence.*;
import java.util.*;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String content;
    private String timeAgo;
    private int likes = 0;

    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    private List<String> images; // Stores image URLs

    private String video; // Stores video URL

    @Column(name = "created_at", updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt; // New field: post creation time

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_liked_users", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "liked_user")
    private Set<String> likedUsers = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    @JsonIgnoreProperties("post")
    private Set<Comment> comments = new HashSet<>();

    @Transient
    private boolean isLiked;

    public boolean getIsLiked() { // Getter for isLiked
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) { // Setter for isLiked
        this.isLiked = isLiked;
    }

    // Automatically set `createdAt` when a post is created
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
