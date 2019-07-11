package com.daishuai.graphql.dao;

import com.daishuai.graphql.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 15:36
 */
public interface BookDao extends JpaRepository<Book, String> {
    
    /**
     * 根据作者id查询该作者的书
     * @param authorId
     * @return
     */
    List<Book> findBooksByAuthorId(String authorId);
    
    /**
     * 根据图书编号删除图书
     * @param bookId
     * @return
     */
    boolean deleteBookByUuid(String bookId);
    
    /**
     * 根据图书编号查询图书
     * @param bookId
     * @return
     */
    Book findBookByUuid(String bookId);
}
