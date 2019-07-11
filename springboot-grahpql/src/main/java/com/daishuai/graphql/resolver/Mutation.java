package com.daishuai.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.daishuai.graphql.dao.AuthorDao;
import com.daishuai.graphql.dao.BookDao;
import com.daishuai.graphql.dto.BookInput;
import com.daishuai.graphql.entity.Author;
import com.daishuai.graphql.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 15:59
 */
@Component
public class Mutation implements GraphQLMutationResolver {
    
    @Autowired
    private BookDao bookDao;
    
    @Autowired
    private AuthorDao authorDao;
    
    public Author newAuthor(String firstName, String lastName) {
        Author author = new Author();
        author.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return authorDao.save(author);
    }
    
    public Book newBook(String title, String isbn, int pageCount, String authorId) {
        Book book = new Book();
        book.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPageCount(pageCount);
        book.setAuthorId(authorId);
        return bookDao.save(book);
    }
    
    public Book saveBook(BookInput bookInput) {
        Book book = new Book();
        book.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        book.setTitle(bookInput.getTitle());
        book.setIsbn(bookInput.getIsbn());
        book.setPageCount(bookInput.getPageCount());
        book.setAuthorId(bookInput.getAuthorId());
        return bookDao.save(book);
    }
    
    public Boolean deleteBook(String bookId) {
        return bookDao.deleteBookByUuid(bookId);
    }
    
    public Book updateBookPageCount(Integer pageCount, String bookId) {
        Book book = bookDao.findBookByUuid(bookId);
        book.setPageCount(pageCount);
        return bookDao.save(book);
    }
}
