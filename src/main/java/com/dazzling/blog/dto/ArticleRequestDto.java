package com.dazzling.blog.dto;

import lombok.Data;
import java.util.ArrayList;

@Data
public class ArticleRequestDto {
  private String type;
  private String slug;
  private String title;
  private String content;
  private String introduction;
  private String cover;
  private ArrayList<String> categories;
  private ArrayList<String> tags;
  private Boolean isRelease;
}
