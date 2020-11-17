package com.daishuai.demo;

import com.alibaba.fastjson.JSONObject;
import com.epoint.sso.client.code.DES;
import com.epoint.sso.client.util.AssertionTokenUtil;
import com.epoint.sso.client.util.HttpClientUtil;
import com.epoint.sso.client.validation.Assertion;

/**
 * @author Daishuai
 * @date 2020/11/10 15:05
 */
public class CallAPI {
    static String AppKey = "a6c9fb20-813e-4478-83c5-f080dc763cb4";//客户端账号标识,请修正为实际值
    static String AppSecret = "e8bab41a-98c9-408d-998d-91b7bb7e1ca1";//客户端账号密码,请修正为实际值
    static String SSOUrl = "http://59.211.219.71/share/token";//共享交换平台获取token地址,请修正为实际值
    static Assertion TokenInfo;//调用凭证，考虑凭证需要远程调用接口获取，需要静态化，不用每次调用重新获取

    //生成调用凭证
    //此方法预期会出现3类异常情况，针对各类异常的信息和处置方法
    //1."error='invalid_client', description='非法的ClientID'",请检查AppKey值是否配置准确
    //2."error='unauthorized_client', description='未验证通过的客户端身份'",请检查AppSecret值是否配置准确
    //3."org.apache.oltu.oauth2.common.exception.OAuthSystemException: java.io.FileNotFoundException",请检查SSOUrl地址是否配置准确
    public static Assertion getToken(boolean refresh) {
        //如果凭证已经生成且不需要强制更新的情况下，则只需要获取原有凭证即可，避免不必要的远程调用
        if (TokenInfo == null || refresh) {
            //利用SDK方法获取调用凭证，传入客户端账号、密码和认证平台地址，采用的是客户端认证模式
            TokenInfo = AssertionTokenUtil.getAssertionStateless(AppKey, AppSecret, SSOUrl);
        }
        return TokenInfo;
    }

    //调用最终API方法
    public static String APIInvoke(String Url, String Access_Token, String Params) {
        //实际需要调用的API的地址，将调用凭证token作为参数传入
        if (Url.indexOf('?') > 0) {
            Url = Url + "&access_token=" + Access_Token;
        } else {
            Url = Url + "?access_token=" + Access_Token;
        }
        //通过SDK的HttpClientUtil调用API，获得返回值
        return HttpClientUtil.postBody(Url, Params);
    }

    public static void main(String[] args) throws Exception {
        String Url = "http://59.211.219.71/share/gxjsgcxfsjscysbajkGetAcceptanceCheckList";//实际API地址,请修正为实际值
        //String Params = "{\"pageIndex\": 1, \"areaCode\": \"450100\", \"isContainSub\": 0, \"startUpdateTime\": \"2020-01-11 09:00:00\"}"; //调用API的参数,请修正为实际值
        JSONObject paramMap = new JSONObject();
        paramMap.put("pageIndex", 1);
        paramMap.put("areaCode", "450100");
        paramMap.put("isContainSub", 0);
        paramMap.put("startUpdateTime", "2020-04-07T15:48:01.360Z");
        //考虑调用凭证缓存化，一定时间后调用凭证肯定会过期，当凭证过期时，引入重试机制，即凭证需要强制更新，并重新调用API接口
        //本示例重试最多3次，3次失败则不再继续重试。
        for (int i = 0; i < 3; i++) {
            String Access_Token = getToken(i > 0).getAccessToken();
            String result = APIInvoke(Url, Access_Token, paramMap.toJSONString());
            System.out.println(result);
            if (result != HttpClientUtil.UNAUTHORIZED) {
                result = DES.decrypt(result);//获取到了正确的返回值，如有必要，对返回值进行解密处理，非必须
                break;
            }
        }
        //1.[32].[32].[32]
        //1,34,67
    }
}
