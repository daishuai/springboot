package com.daishuai.graphql.dto;

import lombok.Data;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 16:14
 */
@Data
public class BookInput {
    
    private String title;
    
    private String isbn;
    
    private int pageCount;
    
    private String authorId;
}
