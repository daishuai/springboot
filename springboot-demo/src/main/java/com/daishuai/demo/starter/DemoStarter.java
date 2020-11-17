package com.daishuai.demo.starter;

import com.daishuai.demo.CallAPI;
import com.epoint.sso.client.code.DES;
import com.epoint.sso.client.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static com.daishuai.demo.CallAPI.APIInvoke;

/**
 * @author Daishuai
 * @date 2020/11/11 11:28
 */
@Slf4j
@Component
public class DemoStarter implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        String Url = "http://59.211.219.71/share/gxjsgcxfsjscysbajkGetAcceptanceCheckList";//实际API地址,请修正为实际值
        String Params = "{\"pageIndex\": 1, \"areaCode\": \"450100\", \"isContainSub\": 0, \"startUpdateTime\": \"2020-04-07T15:48:01.360Z\"}"; //调用API的参数,请修正为实际值
        //考虑调用凭证缓存化，一定时间后调用凭证肯定会过期，当凭证过期时，引入重试机制，即凭证需要强制更新，并重新调用API接口
        //本示例重试最多3次，3次失败则不再继续重试。
        for (int i = 0; i < 3; i++) {
            log.info("start get token >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            String Access_Token = CallAPI.getToken(i > 0).getAccessToken();
            log.info("success get token : {}", Access_Token);
            String result = APIInvoke(Url, Access_Token, Params);
            log.info( "get result: {}" , result);
            if (result != HttpClientUtil.UNAUTHORIZED) {
                result = DES.decrypt(result);//获取到了正确的返回值，如有必要，对返回值进行解密处理，非必须
                break;
            }
        }
    }
}
