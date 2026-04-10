package com.xxz.loginhouduan.service;

import com.xxz.loginhouduan.entity.Post;
import com.xxz.loginhouduan.entity.Comment;
import com.xxz.loginhouduan.repository.PostRepository;
import com.xxz.loginhouduan.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    // ✅ Get all posts (automatically calculate timeAgo)
    public List<Post> getAllPosts(String userName) {
        List<Post> posts = postRepository.findAllPosts();
        for (Post post : posts) {
            post.setIsLiked(post.getLikedUsers().contains(userName));
        }

        posts.forEach(post -> {
            if (post.getCreatedAt() != null) {
                post.setTimeAgo(formatTimeAgo(post.getCreatedAt()));
            }
            post.getComments().forEach(comment -> {
                if (comment.getCreatedAt() != null) {
                    comment.setTimeAgo(formatTimeAgo(comment.getCreatedAt()));
                }
            });
        });

        return posts;
    }

    // ✅ Post a new post
    public Post createPost(Post post) {
        post.setLikes(0);
        post.setCreatedAt(new Date());
        post.setTimeAgo("Just now");
        return postRepository.save(post);
    }

    // ✅ Delete the post (only the author can delete)
    public boolean deletePost(Long postId, String userName) {
        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            if (post.getUserName().equals(userName)) {
                postRepository.deleteById(postId);
                return true;
            } else {
                throw new RuntimeException("Unauthorized to delete this post");
            }
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    // ✅ like post
    public Map<String, Object> likePost(Long postId, String userName) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }

        Post post = postOpt.get();
        if (post.getLikedUsers() == null) {
            post.setLikedUsers(new HashSet<>());
        }

        boolean isLiked = post.getLikedUsers().contains(userName);
        if (isLiked) {
            post.getLikedUsers().remove(userName);
            post.setLikes(post.getLikes() - 1);
        } else {
            post.getLikedUsers().add(userName);
            post.setLikes(post.getLikes() + 1);
        }

        postRepository.save(post);

        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", !isLiked);
        response.put("likes", post.getLikes());
        return response;
    }

    // ✅ add comment
    public Comment addComment(Long postId, Comment comment) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }

        comment.setPost(postOpt.get());
        comment.setCreatedAt(new Date());
        comment.setTimeAgo("Just now");
        return commentRepository.save(comment);
    }



    // ✅ Calculate the time difference (return the timeAgo format)
    private String formatTimeAgo(Date createdAt) {
        Instant instant = createdAt.toInstant();
        LocalDateTime postTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration duration = Duration.between(postTime, LocalDateTime.now());

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else {
            return days + " days ago";
        }
    }
}
