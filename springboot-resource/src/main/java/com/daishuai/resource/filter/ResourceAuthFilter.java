package com.daishuai.resource.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description 静态资源过滤器
 * @createTime 2022-10-21 22:05:08
 */
@Slf4j
@Component
public class ResourceAuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info(">>>>>>>>>>>>>>>>>>doFilter");
        String accessToken = request.getParameter("accessToken");
        log.info("accessToken: {}", accessToken);
        if (StringUtils.isBlank(accessToken)) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "没有访问的权限");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(JSON.toJSONString(result));
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
