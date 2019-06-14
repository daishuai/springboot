package com.daishuai.es.aspect;

import com.daishuai.es.config.ElasticsearchApiBuilder;
import com.daishuai.es.config.RestElasticsearchApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author Daishuai
 * @description Es查询切面
 * @date 2019/6/13 15:53
 */
@Slf4j
//@Component
@Aspect
public class EsApiAspect {

    @Autowired
    private RestElasticsearchApi restElasticsearchApi;

    /**
     * 需要过拦截的方法名
     */
    @Value("${method-name:getGetResponse,getSearchResponse,getDataById,getDateByIds,searchData,pageDataByScroll,searchDataByScroll}")
    private String methodName;

    @Autowired
    private ElasticsearchApiBuilder builder;

    /**
     * 切点
     */
    @Pointcut(value = "execution(* com.daishuai.es.config.RestElasticsearchApi.*(..))")
    public void pointCut() {
        throw new UnsupportedOperationException();
    }


    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        RestElasticsearchApi dynamicRestEsApi = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //方法参数
        Object[] args = joinPoint.getArgs();
        Method method = signature.getMethod();
        //方法名
        String name = method.getName();
        String[] methodNames = StringUtils.split(this.methodName, ",");
        log.info("method name :{}", name);
        if (!StringUtils.equalsAny(name, methodNames) || ArrayUtils.isEmpty(args)) {
            return joinPoint.proceed();
        }

        if (args[0] instanceof String) {
            String index = (String) args[0];
            String[] strs = StringUtils.split(index, ":");
            if (ArrayUtils.isNotEmpty(strs) && strs.length == 2) {
                args[0] = strs[1];
                dynamicRestEsApi = builder.build(strs[0]);
                return method.invoke(dynamicRestEsApi, args);
            }
        }

        for (int i = 0; i < args.length; i++) {
            Object param = args[i];
            if (param instanceof GetRequest) {
                GetRequest request = (GetRequest) param;
                String index = request.index();
                String[] strs = StringUtils.split(index, ":");
                if (ArrayUtils.isNotEmpty(strs) && strs.length == 2) {
                    request.index(strs[1]);
                    args[i] = request;
                    dynamicRestEsApi = builder.build(strs[0]);
                    return method.invoke(dynamicRestEsApi, args);
                }
            }
            if (param instanceof SearchRequest) {
                SearchRequest request = (SearchRequest) param;
                String[] indices = request.indices();
                String index = indices[0];
                String[] strs = StringUtils.split(index, ":");
                if (ArrayUtils.isNotEmpty(strs) && strs.length == 2) {
                    request.indices(strs[1]);
                    args[i] = request;
                    dynamicRestEsApi = builder.build(strs[0]);
                    return method.invoke(dynamicRestEsApi, args);
                }
            }
        }
        return joinPoint.proceed(args);
    }

}
