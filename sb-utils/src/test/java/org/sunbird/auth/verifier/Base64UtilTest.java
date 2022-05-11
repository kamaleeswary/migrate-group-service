package org.sunbird.auth.verifier;

import org.jboss.resteasy.util.Base64;
import org.junit.Assert;
import org.junit.Test;

public class Base64UtilTest {

    @Test
    public void testDecode(){
        String str ="sunbird.com";
        String encodeValue = new String(Base64Util.encode(str.getBytes(), Base64Util.DEFAULT));
        String decoded = new String(Base64Util.decode(encodeValue,Base64Util.DEFAULT));
        Assert.assertTrue(str.equals(decoded));
    }
}
