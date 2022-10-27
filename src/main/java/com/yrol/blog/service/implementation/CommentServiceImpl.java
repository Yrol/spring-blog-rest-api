package com.yrol.blog.service.implementation;


import com.yrol.blog.dto.CommentDto;
import com.yrol.blog.entity.Comment;
import com.yrol.blog.entity.Post;
import com.yrol.blog.exception.ResourceNotFoundException;
import com.yrol.blog.repository.CommentRepository;
import com.yrol.blog.repository.PostRepository;
import com.yrol.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    private PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(postId)));

        Comment comment = mapToEntity(commentDto);

        // setting the Post id (foreign key) in comments.
        comment.setPost(post);

        Comment newComment = commentRepository.save(comment);

        return this.mapToDto(newComment);
    }

    private CommentDto mapToDto(Comment comment) {
        CommentDto commentDto =  new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());

        return commentDto;
    }

    private Comment mapToEntity(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());

        return comment;
    }
}
