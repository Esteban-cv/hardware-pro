package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mine.hardware_pro.model.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer> {


}
