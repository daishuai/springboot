package com.daishuai.event.event;

import lombok.Data;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/2 17:06
 */
@Data
public class DemoEvent {
    
    private String type;
    
    private Object data;
}
