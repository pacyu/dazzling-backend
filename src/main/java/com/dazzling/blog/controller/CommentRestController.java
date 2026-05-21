package com.dazzling.blog.controller;

import com.dazzling.blog.dto.CommentCreateDto;
import com.dazzling.blog.dto.CommentTreeDto;
import com.dazzling.blog.exception.ResourceNotFoundException;
import com.dazzling.blog.models.Article;
import com.dazzling.blog.models.Comment;
import com.dazzling.blog.models.User;
import com.dazzling.blog.repositories.ArticleRepository;
import com.dazzling.blog.repositories.CommentRepository;
import com.dazzling.blog.repositories.UserRepository;
import com.dazzling.blog.service.ImageDownloadService;
import com.dazzling.blog.service.ImageService;
import com.dazzling.blog.service.SmtpService;
import com.dazzling.blog.utils.SlugUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.mail.MessagingException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comment")
public class CommentRestController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmtpService smtpService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDownloadService imageDownloadService;

    @Value("${blog.notify.email}")
    private String notifyEmail;

    @Value("${blog.notify.name}")
    private String notifyName;

    @Value("${file.upload.avatarPath}")
    private String avatarPath;

    private static final Logger log = LoggerFactory.getLogger(CommentRestController.class);

    @GetMapping
	public Page<Comment> getCommentsByPagebean(@NonNull @PageableDefault(size = 10) Pageable pageable) {
		return commentRepository.findAll(pageable);
	}

    @GetMapping(params = "v")
    public ResponseEntity<List<CommentTreeDto>> getCommentsBySlug(
        @RequestParam(value = "v", required = false, defaultValue = "") String slug
    ) {
        // 1. 查询所有评论（按 root_id 分组，depth 排序）
        List<Comment> allComments = commentRepository.findByArticleSlug(slug)
                .orElse(Collections.emptyList());
        
        // 2. 构建映射：id -> dto
        Map<Long, CommentTreeDto> dtoMap = new HashMap<>();
        List<CommentTreeDto> roots = new ArrayList<>();

        Set<Long> userIds = allComments.stream()
        .map(Comment::getUserId)
        .collect(Collectors.toSet());
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));
        
        for (Comment comment : allComments) {
            CommentTreeDto dto = new CommentTreeDto(comment);
            User u = userMap.get(comment.getUserId());
            if (u != null) {
                dto.setUsername(u.getUsername());
                dto.setEmail(u.getEmail());
            }
            dtoMap.put(comment.getId(), dto);
        }
        
        // 3. 组装父子关系
        for (Comment comment : allComments) {
            CommentTreeDto dto = dtoMap.get(comment.getId());
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                roots.add(dto);
            } else {
                CommentTreeDto parent = dtoMap.get(comment.getParentId());
                if (parent != null) {
                    parent.getReplies().add(dto);
                } else {
                    // 防御：父评论不在当前查询结果中（可能被删除），则作为顶级评论
                    roots.add(dto);
                }
            }
        }
        
        // 4. 按创建时间排序顶级评论，子回复也排序（可选）
        roots.sort(Comparator.comparing(CommentTreeDto::getCreatedAt));
        for (CommentTreeDto root : roots) {
            if (root.getReplies() != null) {
                root.getReplies().sort(Comparator.comparing(CommentTreeDto::getCreatedAt));
            }
        }
        
        return ResponseEntity.ok(roots);
    }

    @PostMapping
    public ResponseEntity<?> newComment(
        @RequestHeader Map<String, String> headers,
        @RequestBody CommentCreateDto payload,
        final Locale locale
    ) throws MessagingException {

        String articleSlug = payload.getSlug();
        String username = payload.getUsername();
        String email = payload.getEmail();
        String content = payload.getContent();
        Long parentId = payload.getParentId();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            String userSlug = SlugUtil.randomSlug();

            String avatar = username + ".svg";

            String avatarUrl = imageService.generateAvatarUrl(username);

            imageDownloadService.downloadAndSave(avatarUrl, avatarPath + avatar);

            user = new User(userSlug, email, username, "", avatar, "ROLE_GUEST");
            userRepository.save(user);
        }
        Long userId = user.getId();

        Article article = articleRepository.findBySlug(articleSlug)
        .orElseThrow(() -> new ResourceNotFoundException("文章不存在！"));

        Long articleId = article.getId();

        Comment comment = new Comment(
            articleId,
            userId,
            articleSlug,
            null,
            null,
            0L,
            content);

        try {
            
            if (parentId != null) {
                
                Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("被回复的评论不存在！"));
                
                User toUser = userRepository
                .findById(parentComment.getUserId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("被回复的用户不存在！"));

                comment.setParentId(parentId);
                comment.setDepth(1L);

                if (parentComment.getDepth() == 0L) {
                    comment.setRootId(parentId);
                } else {
                    comment.setRootId(parentComment.getRootId());
                }

                this.smtpService.sendTextMail(
                    username, email, article.getTitle(),
                    content, toUser.getUsername(), toUser.getEmail(), locale);

            } else {

                this.smtpService.sendTextMail(username, email, article.getTitle(),
                        content, notifyName, notifyEmail, locale);

            }
            
        } catch (MessagingException e) {
            log.error("邮件发送失败!", e);
        }

        commentRepository.save(comment);

        article.setReviews(article.getReviews() + 1);
        articleRepository.save(article);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "评论成功！",
                "content", content
        ));
    }

    @PutMapping
    public ResponseEntity<?> updateComment(
        @RequestHeader Map<String, String> headers,
        @RequestBody Map<String, Object> payload
    ) {
        Long id = ((Long) payload.get("id")).longValue();
        String content = (String) payload.get("content");

        Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("被回复的评论不存在！"));
        Date date = new Date();
        comment.setContent(content);
        comment.setUpdatedAt(date);
        comment.setIsEdit(true);
        commentRepository.save(comment);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "修改成功！",
                "content", content
        ));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteComment(
        @RequestHeader Map<String, String> headers,
        @RequestBody Map<String, Object> payload
    ) {
        Long id = ((Number) payload.get("id")).longValue();
        Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("评论不存在！"));
        Article article = articleRepository.findById(comment.getArticleId().longValue())
        .orElseThrow(() -> new ResourceNotFoundException("文章不存在！"));
        article.setReviews(article.getReviews() - 1L);
        articleRepository.save(article);
        commentRepository.deleteById(id);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "删除成功！"
        ));
    }
}