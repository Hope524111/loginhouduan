package com.xxz.loginhouduan.service;

import com.xxz.loginhouduan.entity.Comment;
import com.xxz.loginhouduan.entity.Post;
import com.xxz.loginhouduan.repository.CommentRepository;
import com.xxz.loginhouduan.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllPosts_returnsPostsWithIsLikedAndTimeAgo() {
        Post post = new Post();
        post.setUserName("bob");
        post.setCreatedAt(new Date());
        post.setLikedUsers(new HashSet<>(Arrays.asList("alice")));
        post.setComments(new HashSet<>());

        when(postRepository.findAllPosts()).thenReturn(new ArrayList<>(Collections.singletonList(post)));

        List<Post> result = postService.getAllPosts("alice");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsLiked());
        assertNotNull(result.get(0).getTimeAgo());
    }

    @Test
    public void testCreatePost_setsDefaultFieldsAndSaves() {
        Post post = new Post();
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post saved = postService.createPost(post);

        assertEquals(0, saved.getLikes());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Just now", saved.getTimeAgo());
    }

    @Test
    public void testDeletePost_successfulDeletionByAuthor() {
        Post post = new Post();
        post.setId(1L);
        post.setUserName("alice");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertTrue(postService.deletePost(1L, "alice"));
        verify(postRepository).deleteById(1L);
    }

    @Test
    public void testDeletePost_unauthorizedDeletion_shouldThrow() {
        Post post = new Post();
        post.setUserName("bob");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> postService.deletePost(1L, "alice"));
        assertEquals("Unauthorized to delete this post", ex.getMessage());
    }

    @Test
    public void testDeletePost_postNotFound_shouldThrow() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> postService.deletePost(99L, "anyone"));
        assertEquals("Post not found", ex.getMessage());
    }

    @Test
    public void testLikePost_toggleLikeAndUnlike() {
        Post post = new Post();
        post.setId(1L);
        post.setLikes(0);
        post.setLikedUsers(new HashSet<>());

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Map<String, Object> liked = postService.likePost(1L, "alice");
        assertEquals(true, liked.get("isLiked"));
        assertEquals(1, liked.get("likes"));

        Map<String, Object> unliked = postService.likePost(1L, "alice");
        assertEquals(false, unliked.get("isLiked"));
        assertEquals(0, unliked.get("likes"));
    }

    @Test
    public void testLikePost_postNotFound_shouldThrow() {
        when(postRepository.findById(100L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> postService.likePost(100L, "someone"));
        assertEquals("Post not found", ex.getMessage());
    }

    @Test
    public void testAddComment_successful() {
        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setContent("Nice!");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        Comment saved = postService.addComment(1L, comment);

        assertEquals("Just now", saved.getTimeAgo());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    public void testAddComment_postNotFound_shouldThrow() {
        when(postRepository.findById(101L)).thenReturn(Optional.empty());

        Comment comment = new Comment();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> postService.addComment(101L, comment));
        assertEquals("Post not found", ex.getMessage());
    }
}
