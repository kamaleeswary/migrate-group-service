package org.sunbird.util.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.util.HttpClientUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        HttpClients.class

 })
@PowerMockIgnore({
        "javax.management.*",
        "javax.net.ssl.*",
        "javax.security.*",
        "jdk.internal.reflect.*"
})
public class HttpClientUtilTest {
      String url="http://sunbird.test.com";
      String req ="{\"test\":\"rest\"}";
      String successResponse ="{\"id\":\"api.user.response\",\"ver\":\"v1\",\"ts\":\"2021-10-14 08:07:47:366+0000\",\"params\":{\"resmsgid\":null,\"msgid\":\"23131\",\"err\":null,\"status\":\"success\",\"errmsg\":null},\"responseCode\":\"OK\",\"result\":{\"response\":{}}}";

      @Test
      public void testPostSuccess() throws IOException {
          PowerMockito.mockStatic(HttpClients.class);
          CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
          CloseableHttpResponse httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
          StatusLine statusLine = PowerMockito.mock(StatusLine.class);
          Mockito.when(statusLine.getStatusCode()).thenReturn(200);
          Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
          HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
          InputStream targetStream = new ByteArrayInputStream(successResponse.getBytes());
          Mockito.when(httpEntity.getContent()).thenReturn(targetStream);
          Mockito.when(httpEntity.getContentLength()).thenReturn((long) successResponse.getBytes().length);

          Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
          Mockito.when(httpClient.execute(Mockito.any())).thenReturn(httpResponse);

          HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
          Mockito.when(httpClientBuilder.build()).thenReturn(httpClient);
          Mockito.when(HttpClients.custom()).thenReturn(httpClientBuilder);
          HttpClientUtil.getInstance();
          Map<String,Object> reqContext = new HashMap<>();
          reqContext.put("userid","123123123");
          Map<String,String> headers = new HashMap<>();
          headers.put("Content-Type","application/json");
          String response = HttpClientUtil.post(url,req,headers,reqContext);
          Assert.assertTrue(null != response);
    }

    @Test
    public void testGetSuccess() throws IOException {
        PowerMockito.mockStatic(HttpClients.class);
        CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = PowerMockito.mock(StatusLine.class);
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
        HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
        InputStream targetStream = new ByteArrayInputStream(successResponse.getBytes());
        Mockito.when(httpEntity.getContent()).thenReturn(targetStream);
        Mockito.when(httpEntity.getContentLength()).thenReturn((long) successResponse.getBytes().length);

        Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
        Mockito.when(httpClient.execute(Mockito.any())).thenReturn(httpResponse);

        HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
        Mockito.when(httpClientBuilder.build()).thenReturn(httpClient);
        Mockito.when(HttpClients.custom()).thenReturn(httpClientBuilder);
        HttpClientUtil.getInstance();
        Map<String,Object> reqContext = new HashMap<>();
        reqContext.put("userid","123123123");
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        String response = HttpClientUtil.get(url,headers,reqContext);
        Assert.assertTrue(null != response);
    }

    @Test
    public void testPatchSuccess() throws IOException {
        PowerMockito.mockStatic(HttpClients.class);
        CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = PowerMockito.mock(StatusLine.class);
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
        HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
        InputStream targetStream = new ByteArrayInputStream(successResponse.getBytes());
        Mockito.when(httpEntity.getContent()).thenReturn(targetStream);
        Mockito.when(httpEntity.getContentLength()).thenReturn((long) successResponse.getBytes().length);

        Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
        Mockito.when(httpClient.execute(Mockito.any())).thenReturn(httpResponse);

        HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
        Mockito.when(httpClientBuilder.build()).thenReturn(httpClient);
        Mockito.when(HttpClients.custom()).thenReturn(httpClientBuilder);
        HttpClientUtil.getInstance();
        Map<String,Object> reqContext = new HashMap<>();
        reqContext.put("userid","123123123");
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        String response = HttpClientUtil.patch(url,req,headers,reqContext);
        Assert.assertTrue(null != response);
    }


    @Test
    public void testPostFormDataSuccess() throws IOException {
        PowerMockito.mockStatic(HttpClients.class);
        CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = PowerMockito.mock(StatusLine.class);
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
        HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
        InputStream targetStream = new ByteArrayInputStream(successResponse.getBytes());
        Mockito.when(httpEntity.getContent()).thenReturn(targetStream);
        Mockito.when(httpEntity.getContentLength()).thenReturn((long) successResponse.getBytes().length);

        Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
        Mockito.when(httpClient.execute(Mockito.any())).thenReturn(httpResponse);

        HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
        Mockito.when(httpClientBuilder.build()).thenReturn(httpClient);
        Mockito.when(HttpClients.custom()).thenReturn(httpClientBuilder);
        HttpClientUtil.getInstance();
        Map<String,Object> reqContext = new HashMap<>();
        reqContext.put("userid","123123123");
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        Map<String,String> reqObj = new HashMap<>();
        reqObj.put("test","13132");
        reqObj.put("testing","true");
        String response = HttpClientUtil.postFormData(url,reqObj,headers,reqContext);
        Assert.assertTrue(null != response);
    }

    @Test
    public void testPostFormDataFailed() throws IOException {
        PowerMockito.mockStatic(HttpClients.class);
        CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = PowerMockito.mock(StatusLine.class);
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
        HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
        InputStream targetStream = new ByteArrayInputStream(successResponse.getBytes());
        Mockito.when(httpEntity.getContent()).thenReturn(targetStream);
        Mockito.when(httpEntity.getContentLength()).thenReturn((long) successResponse.getBytes().length);

        Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
        Mockito.when(httpClient.execute(Mockito.any())).thenThrow(new RuntimeException());

        HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
        Mockito.when(httpClientBuilder.build()).thenReturn(httpClient);
        Mockito.when(HttpClients.custom()).thenReturn(httpClientBuilder);
        HttpClientUtil.getInstance();
        Map<String,Object> reqContext = new HashMap<>();
        reqContext.put("userid","123123123");
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        Map<String,String> reqObj = new HashMap<>();
        reqObj.put("test","13132");
        reqObj.put("testing","true");
        String response = HttpClientUtil.postFormData(url,reqObj,headers,reqContext);
        Assert.assertTrue(StringUtils.isEmpty(response));
    }
}
