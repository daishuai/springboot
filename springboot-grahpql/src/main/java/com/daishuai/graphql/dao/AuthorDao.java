package com.daishuai.graphql.dao;

import com.daishuai.graphql.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 15:36
 */
public interface AuthorDao extends JpaRepository<Author, String> {
    
    /**
     * 根据作者id查找作者
     * @param authorId
     * @return
     */
    Author findAuthorByUuid(String authorId);
}
