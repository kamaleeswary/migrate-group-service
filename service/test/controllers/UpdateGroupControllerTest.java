package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.sunbird.common.exception.BaseException;
import org.sunbird.common.util.JsonKey;
import play.mvc.Result;

public class UpdateGroupControllerTest extends BaseApplicationTest {

  @Before
  public void before() throws BaseException {
    setup(DummyActor.class);
  }

  @Test
  public void testUpdateGroupPasses() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put(JsonKey.GROUP_ID, "group1");
    reqMap.put(JsonKey.NAME, "group name1");
    Map<String, Object> request = new HashMap<>();
    List<Map<String, Object>> members = new ArrayList<>();
    Map<String, Object> member = new HashMap<>();
    member.put(JsonKey.ROLE, JsonKey.MEMBER);
    member.put(JsonKey.STATUS, JsonKey.ACTIVE);
    member.put(JsonKey.USER_ID, "userID");
    members.add(member);
    Map<String,Object> memberOp = new HashMap<>();
    memberOp.put(JsonKey.ADD, members);
    reqMap.put(JsonKey.MEMBERS,memberOp);
    request.put("request", reqMap);

    Result result = performTest("/v1/group/update", "PATCH", request);
    assertTrue(getResponseStatus(result) == Response.Status.OK.getStatusCode());
  }

  @Test
  public void testEmptyGroupId() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put(JsonKey.GROUP_ID, "");
    Map<String, Object> request = new HashMap<>();
    request.put("request", reqMap);
    Result result = performTest("/v1/group/update", "PATCH", request);
    System.out.println(getResponseStatus(result));
    assertTrue(getResponseStatus(result) == Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Test
  public void testMandatoryParamGroupId() {
    Map<String, Object> reqMap = new HashMap<>();
    Map<String, Object> request = new HashMap<>();
    request.put("request", reqMap);
    Result result = performTest("/v1/group/update", "PATCH", request);
    assertTrue(getResponseStatus(result) == Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Test
  public void testMandatoryParamActivityType() {
    Map<String, Object> reqMap = new HashMap<>();
    Map<String, Object> request = new HashMap<>();
    reqMap.put(JsonKey.GROUP_ID, "group1");
    Map<String, Object> activities = new HashMap<>();
    List<Map<String, Object>> addActvity = new ArrayList<>();
    Map<String, Object> activity =
        new HashMap<String, Object>() {
          {
            put(JsonKey.ID, "id");
          }
        };
    addActvity.add(activity);
    activities.put(JsonKey.ADD, addActvity);
    reqMap.put(JsonKey.ACTIVITIES, activities);
    request.put("request", reqMap);
    Result result = performTest("/v1/group/update", "PATCH", request);
    assertEquals(getResponseStatus(result), Response.Status.BAD_REQUEST.getStatusCode());
  }
}
