package org.sunbird.service;

import org.apache.tools.ant.taskdefs.condition.Http;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.common.response.Response;
import org.sunbird.util.HttpClientUtil;


@RunWith(PowerMockRunner.class)
@PrepareForTest({
        System.class,
        HttpClientUtil.class
})
@PowerMockIgnore({"javax.management.*", "jdk.internal.reflect.*"})
public class UserServiceImplTest {
    String successResponse ="{\"id\":\"api.user.response\",\"ver\":\"v1\",\"ts\":\"2021-10-14 08:07:47:366+0000\",\"params\":{\"resmsgid\":null,\"msgid\":\"23131\",\"err\":null,\"status\":\"success\",\"errmsg\":null},\"responseCode\":\"OK\",\"result\":{\"response\":{}}}";

   @Before
   public void setup(){
        PowerMockito.mockStatic(HttpClientUtil.class);
        PowerMockito.when(HttpClientUtil.post(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),
                Mockito.anyMap())).thenReturn(successResponse);
       PowerMockito.when(HttpClientUtil.get(Mockito.anyString(),Mockito.anyMap(),
               Mockito.anyMap())).thenReturn(successResponse);
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv(Mockito.anyString())).thenReturn("systemuri");

    }
   @Test
    public void testGetOrganisationDetails(){
            UserService userService = UserServiceImpl.getInstance();
            Response response = userService.getOrganisationDetails("orgid");
            Assert.assertTrue(null != response);

   }

    @Test
    public void testSystemSettings(){
        UserService userService = UserServiceImpl.getInstance();
        Response response = userService.getSystemSettings();
        Assert.assertTrue(null != response);

    }
}
