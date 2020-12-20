package com.redit.clone.repository;

import com.redit.clone.model.Comment;
import com.redit.clone.model.Post;
import com.redit.clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findAllByUser(User user);
}
