package org.sunbird.actors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.actor.core.ActorConfig;
import org.sunbird.common.request.Request;
import org.sunbird.common.response.Response;
import org.sunbird.common.util.JsonKey;
import org.sunbird.common.util.Notification;
import org.sunbird.models.MemberResponse;
import org.sunbird.notifications.INotificationHandler;
import org.sunbird.notifications.NotificationFactory;
import org.sunbird.service.UserService;
import org.sunbird.service.UserServiceImpl;
import org.sunbird.util.GroupRequestHandler;
import org.sunbird.util.LoggerUtil;
import org.sunbird.util.NotificationService;
import org.sunbird.util.helper.PropertiesCache;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@ActorConfig(
        tasks = {"sendNotifications"},
        asyncTasks = {},
        dispatcher = "group-dispatcher"
)
public class GroupNotificationActor extends BaseActor{

    private LoggerUtil logger = new LoggerUtil(GroupNotificationActor.class);

    @Override
    public void onReceive(Request request) throws Throwable {

        logger.info(request.getContext(), MessageFormat.format("Notification Actor: sending notifications for ",request.getOperation()));
        String updatedBy = new GroupRequestHandler().getRequestedBy(request);
        Map<String,Map<String,Object>> userDetails = getUserDetails(request);
        String channelEnabled = PropertiesCache.getInstance().getProperty(JsonKey.ENABLE_TENANT_CONFIGURATION);
        logger.info(request.getContext(),MessageFormat.format("Notification Enabled for Tenants:{0} ",channelEnabled));
        //If notification is available for all channel, the value will be *
        if(!JsonKey.ASTERISK.equals(channelEnabled)){
            removeDisabledChannelMembers(request, userDetails,channelEnabled);
        }
        INotificationHandler notificationHandler = NotificationFactory.getNotificationHandler(request.getOperation());
        Map<String, Object> user = new HashMap<>();
        if(null != updatedBy) {
           user.put(JsonKey.ID,updatedBy);
           user.put(JsonKey.TYPE,JsonKey.USER);
           user.put(JsonKey.NAME,userDetails.get(updatedBy) != null?userDetails.get(updatedBy).get(JsonKey.NAME):null);
        }else {
            user.put(JsonKey.ID, JsonKey.GROUPS_SERVICE);
            user.put(JsonKey.TYPE, JsonKey.SYSTEM);
        }
        List<Notification> notifications = notificationHandler.getNotificationObj(request,user);
        if(CollectionUtils.isNotEmpty(notifications)){
            sendNotifications(notifications, request.getContext());
        }
        Response response = new Response();
        response.put(JsonKey.RESULT,"ok");
        sender().tell(response, self());
    }

    /**
     * Remove user from getting notification does not belong to the state for which feature is enabled
     * @param request
     * @param userDetails
     * @param channel
     */
    private void removeDisabledChannelMembers(Request request, Map<String, Map<String, Object>> userDetails, String channel) {
        // Remove already existing members not belonging to enabled tenant
        List<MemberResponse> members = (List<MemberResponse>) request.getRequest().get(JsonKey.MEMBERS);
        Iterator<MemberResponse> itr = members.iterator();
        while(itr.hasNext()){
            MemberResponse member = itr.next();
            Map<String,Object> memberInfo = userDetails.get(member.getUserId());
            if(MapUtils.isEmpty(memberInfo) || !channel.contains((String)memberInfo.get(JsonKey.CHANNEL))){
                itr.remove();
            }
        }
        request.getRequest().put(JsonKey.MEMBERS,members);
        // Remove newly added members not belonging to enabled channel
        Map<String,Object> reqObj = (Map<String,Object>)request.getRequest().get(JsonKey.REQUEST);
        Map memberOperationMap = (Map)reqObj.get(JsonKey.MEMBERS);
        if(MapUtils.isNotEmpty(memberOperationMap)){
            List<Map<String,Object>> memberTobeAdded = (List<Map<String, Object>>) memberOperationMap.get(JsonKey.ADD);
            if(CollectionUtils.isNotEmpty(memberTobeAdded)) {
                Iterator<Map<String, Object>> memberAddItr = memberTobeAdded.iterator();
                while (memberAddItr.hasNext()) {
                    Map<String, Object> member = memberAddItr.next();
                    Map<String, Object> memberInfo = userDetails.get(member.get(JsonKey.USER_ID));
                    if (MapUtils.isEmpty(memberInfo) || !channel.contains((String) memberInfo.get(JsonKey.CHANNEL))) {
                        memberAddItr.remove();
                    }
                }
            }
        }

    }
    /**
     * Get User Details from user service
     * @param request
     *
     * @return
     */

    private Map<String,Map<String,Object>> getUserDetails(Request request) {
        Map<String,Map<String,Object>> userDetails  = new HashMap<>();
        List<String> users = getMemberIds(request);
        UserService userService = UserServiceImpl.getInstance();
        Response response = userService.searchUserByIds(users, request.getContext());
        if (null != response && null != response.getResult()) {
            Map<String, Object> userRes =
                    (Map<String, Object>) response.getResult().get(JsonKey.RESPONSE);
            if (null != userRes) {
                List<Map<String, Object>> userLists =
                        (List<Map<String, Object>>) userRes.get(JsonKey.CONTENT);

                if (CollectionUtils.isNotEmpty(userLists)) {
                    for (Map<String,Object> userInfo: userLists) {
                        String firstName =
                                StringUtils.isNotEmpty((String) userInfo.get(JsonKey.FIRSTNAME))
                                        ? (String) userInfo.get(JsonKey.FIRSTNAME)
                                        : "";

                        String lastName =
                                StringUtils.isNotEmpty((String) userInfo.get(JsonKey.LASTNAME))
                                        ? " " + (String) userInfo.get(JsonKey.LASTNAME)
                                        : "";
                        userInfo.put(JsonKey.NAME, firstName+lastName);
                        userDetails.put((String) userInfo.get(JsonKey.ID), userInfo);
                    }

                }
            }

        }
        return userDetails;
    }

    //Get user ids to fetch details
    private List<String> getMemberIds(Request request) {
        List<MemberResponse> members = (List<MemberResponse>) request.getRequest().get(JsonKey.MEMBERS);
        Map<String,Object> reqObj = (Map<String,Object>)request.getRequest().get(JsonKey.REQUEST);
        Map memberOperationMap = (Map)reqObj.get(JsonKey.MEMBERS);
        List<String> users = members.stream().map(MemberResponse::getUserId).collect(Collectors.toList());
        if(MapUtils.isNotEmpty(memberOperationMap)) {
            List<Map<String, Object>> memberTobeAdded = (List<Map<String, Object>>) memberOperationMap.get(JsonKey.ADD);
            if (CollectionUtils.isNotEmpty(memberTobeAdded)) {
                for (Map<String, Object> itrMap : memberTobeAdded) {
                    users.add((String) itrMap.get(JsonKey.USER_ID));
                }
            }
        }
        return users;
    }

    private void sendNotifications(List<Notification> notifications, Map<String,Object> reqContext) {
        NotificationService notificationService = new NotificationService();
        notificationService.sendSyncNotification(notifications, reqContext);

    }
}
