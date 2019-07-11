package com.daishuai.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.daishuai.graphql.dao.AuthorDao;
import com.daishuai.graphql.dao.BookDao;
import com.daishuai.graphql.entity.Author;
import com.daishuai.graphql.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 16:06
 */
@Component
public class Query implements GraphQLQueryResolver {
    
    @Autowired
    private AuthorDao authorDao;
    
    @Autowired
    private BookDao bookDao;
    
    public Author findAuthorById(String authorId) {
        return authorDao.findAuthorByUuid(authorId);
    }
    
    public List<Author> findAllAuthors() {
        return authorDao.findAll();
    }
    
    public Long countAuthors() {
        return authorDao.count();
    }
    
    public List<Book> findAllBooks() {
        return bookDao.findAll();
    }
    
    public Long countBooks() {
        return bookDao.count();
    }
}
