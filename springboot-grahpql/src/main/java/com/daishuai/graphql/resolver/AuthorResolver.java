package com.daishuai.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.daishuai.graphql.dao.BookDao;
import com.daishuai.graphql.entity.Author;
import com.daishuai.graphql.entity.Book;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 15:49
 */
@Component
public class AuthorResolver implements GraphQLResolver<Author> {
    
    @Autowired
    private BookDao bookDao;
    
    public String getCreateTime(Author author) {
        return DateFormatUtils.format(author.getCreatedTime(), "yyyy-MM-dd HH:mm:ss");
    }
    
    public List<Book> getBooks(Author author) {
        return bookDao.findBooksByAuthorId(author.getUuid());
    }
}
