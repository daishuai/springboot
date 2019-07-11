package com.daishuai.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.daishuai.graphql.dao.AuthorDao;
import com.daishuai.graphql.entity.Author;
import com.daishuai.graphql.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 15:54
 */
@Component
public class BookResolver implements GraphQLResolver<Book> {
    
    @Autowired
    private AuthorDao authorDao;
    
    public Author getAuthor(Book book) {
        return authorDao.findAuthorByUuid(book.getAuthorId());
    }
}
