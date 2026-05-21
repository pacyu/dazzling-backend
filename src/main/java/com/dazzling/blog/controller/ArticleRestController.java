package com.dazzling.blog.controller;

import com.dazzling.blog.dto.ArticleRequestDto;
import com.dazzling.blog.models.Article;
import com.dazzling.blog.models.Category;
import com.dazzling.blog.models.Comment;
import com.dazzling.blog.models.Tag;
import com.dazzling.blog.models.User;
import com.dazzling.blog.utils.SlugUtil;
import com.dazzling.blog.repositories.*;
import com.dazzling.blog.service.ImageDownloadService;
import com.dazzling.blog.service.ImageService;
import com.dazzling.blog.exception.ResourceNotFoundException;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/article")
public class ArticleRestController {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ImageDownloadService imageDownloadService;

	@Value("${file.upload.coverPath}")
  private String coverPath;

	@Value("${pollinations.ai.prompt}")
	private String prompt;

	@GetMapping
	public Page<Article> getArticlesByPagebean(@NonNull @PageableDefault(size = 10) Pageable pageable) {
		return articleRepository.findAll(pageable);
	}

	@GetMapping(params = "title")
	public Article getByTitle(
		@RequestParam(value = "title", required = false, defaultValue = "") String title) {
		return articleRepository.findByTitle(title)
				.orElseThrow(() -> 
				new ResourceNotFoundException("未找到标题为 「" + title + "」 的文章！"));
	}

	@GetMapping(params = "v")
	public Article getBySlug(
		@RequestParam(value = "v", required = false, defaultValue = "") String slug) {
		return articleRepository.findBySlug(slug)
			.orElseThrow(() -> 
			new ResourceNotFoundException("未找到文章！"));
	}

	@PostMapping
	public ResponseEntity<?> newArticle(
		Principal principal,
		@RequestBody ArticleRequestDto payload
	) throws DataAccessException, MethodArgumentNotValidException {
		
		String author = principal.getName();
		User user = userRepository.findByUsername(author)
		.orElseThrow(() -> 
		new ResourceNotFoundException("未找到用户！"));
		Long userId = user.getId();
		String userSlug = user.getSlug();

		String title = payload.getTitle();
		String content = payload.getContent();
		String introduction = payload.getIntroduction();
		String cover = payload.getCover();
		ArrayList<String> categories = payload.getCategories();
		Boolean isRelease = payload.getIsRelease();
		ArrayList<String> tags = payload.getTags();

		String slug = SlugUtil.randomSlug();
		if (cover == "") {
			cover = slug + ".png";
			byte[] coverImage = imageService.generateCover(prompt);
			imageDownloadService.saveImage(coverImage, coverPath + cover);
		}

		// 分类
		for (String string : categories) {
			if (categoryRepository.findByName(string) == null) {
				String categorySlug = SlugUtil.randomSlug(7);

				String categoryCover = categorySlug + ".png";

        byte[] categoryCoverImage = imageService.generateCover(prompt);

        imageDownloadService.saveImage(categoryCoverImage, coverPath + categoryCover);

				Category category = new Category(categorySlug, string, categoryCover);
				categoryRepository.save(category);
			}
		}

		// 标签
		for (String string : tags) {
			if (tagRepository.findByName(string) == null) {
				Tag tag = new Tag(SlugUtil.randomSlug(7), string);
				tagRepository.save(tag);
			}
		}

		Date created = new Date();
		String html_content = HtmlRenderer.builder().build().render(Parser.builder().build().parse(content));
		
		Article article = new Article(
			slug, userId, userSlug, title, introduction,
			html_content, cover, categories, created,
			null, null,
			author, 0L, 0L,
			0L, 0L, false,
			false, tags);
		if (isRelease) {
			article.setReleasedAt(created);
		} else {
			article.setIsDraft(true);
		}
		
		article.setHashString(article.genMd5());
		articleRepository.save(article);

		return ResponseEntity.ok(Map.of(
				"status", 200,
				"message", "添加成功！",
				"article", article.getSlug()
		));
	}

	@PutMapping
	public ResponseEntity<?> updateArticle(
		@RequestHeader Map<String, String> headers,
		@RequestBody ArticleRequestDto payload
	) throws DataAccessException, MethodArgumentNotValidException {

		String slug = payload.getSlug();
		Article article = articleRepository.findBySlug(slug)
		.orElseThrow(() -> new 
		ResourceNotFoundException("未找到文章！"));
		String type = payload.getType();
		String title = payload.getTitle();
		String content = payload.getContent();
		String introduction = payload.getIntroduction();
		String cover = payload.getCover();
		ArrayList<String> categories = payload.getCategories();
		ArrayList<String> tags = payload.getTags();
		Boolean isRelease = payload.getIsRelease();

		switch (type) {
			case "draft":
				article.setIsDraft(!article.getIsDraft());
				break;
			case "update":
				article.setTitle(title);
				article.setIntroduction(introduction);
				String html_content = HtmlRenderer.builder().build().render(Parser.builder().build().parse(content));
				article.setContent(html_content);
				if (cover == "") {
					String coverSlug = SlugUtil.randomSlug();
					cover = coverSlug + ".png";
					byte[] coverImage = imageService.generateCover(prompt);
					imageDownloadService.saveImage(coverImage, coverPath + cover);
				}
				article.setCover(cover);
				article.setUpdatedAt(new Date());
				article.setCategory(categories);
				article.setTag(tags);
				article.setEditCount(article.getEditCount() + 1L);
				article.setIsDraft(!isRelease);

				for (String string : categories) {
					if (categoryRepository.findByName(string) == null) {
						String categorySlug = SlugUtil.randomSlug(7);

						String categoryCover = categorySlug + ".png";

						byte[] categoryCoverImage = imageService.generateCover(prompt);

						imageDownloadService.saveImage(categoryCoverImage, coverPath + categoryCover);

						Category category = new Category(categorySlug, string, categoryCover);
						categoryRepository.save(category);
					}
				}

				for (String string : tags) {
					if (tagRepository.findByName(string) == null) {
						Tag tag = new Tag(SlugUtil.randomSlug(7), string);
						tagRepository.save(tag);
					}
				}
				break;
			case "remove":
				article.setIsRemove(!article.getIsRemove());
				break;
			default:
				article.setIsDraft(!article.getIsDraft());
				break;
		}
		articleRepository.save(article);

		return ResponseEntity.ok(Map.of(
				"status", 200,
				"message", "修改成功！",
				"article", slug
		));
	}

	@DeleteMapping
	public ResponseEntity<?> deleteArticle(
		@RequestBody Map<String, Object> payload
	) throws DataAccessException, MethodArgumentNotValidException {

		Long id = ((Number) payload.get("id")).longValue();

		List<Comment> comments = commentRepository.findByArticleId(id).orElseThrow(() -> new 
		ResourceNotFoundException("未找到文章下的评论！"));
		for (Comment comment : comments) {
				commentRepository.deleteById(comment.getId().longValue());
		}
		articleRepository.deleteById(id);

		return ResponseEntity.ok(Map.of(
				"status", 200,
				"message", "删除成功！",
				"article", id
		));
	}
}