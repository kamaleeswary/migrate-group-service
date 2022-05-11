package org.sunbird.notifications;

import org.apache.commons.collections4.CollectionUtils;
import org.sunbird.common.request.Request;
import org.sunbird.common.util.JsonKey;
import org.sunbird.common.util.Notification;
import org.sunbird.common.util.NotificationType;
import org.sunbird.models.MemberResponse;
import org.sunbird.service.GroupService;
import org.sunbird.service.GroupServiceImpl;
import org.sunbird.service.MemberService;
import org.sunbird.service.MemberServiceImpl;
import org.sunbird.util.LoggerUtil;

import java.util.*;

public class MemberUpdateNotificationHandler implements INotificationHandler{
    private LoggerUtil logger = new LoggerUtil(MemberUpdateNotificationHandler.class);

    @Override
    public List<Notification> getNotificationObj(Request request, Map<String,Object> updatedBy) {
        List<Notification> notifications = new ArrayList<>();
        Map<String,Object> reqObj = (Map<String,Object>)request.getRequest().get(JsonKey.REQUEST);
        Map<String,Object> groupDetails = (Map<String, Object>) request.getRequest().get(JsonKey.GROUP);
        if(null != groupDetails){
            Map memberOperationMap = (Map)reqObj.get(JsonKey.MEMBERS);
            // Get Member Details of the Updated By user
            List<MemberResponse> membersInDB = (List<MemberResponse>) request.getRequest().get(JsonKey.MEMBERS);
            Map<String, Object> additionalInfo = getAdditionalInfo(groupDetails);

            if (CollectionUtils.isNotEmpty(
                    (List<Map<String, Object>>) memberOperationMap.get(JsonKey.ADD))) {
                Notification notification = handleMemberAddNotifications(memberOperationMap,groupDetails,updatedBy,additionalInfo);
                if(null != notification){
                    notifications.add(notification);
                }
            }

            if (CollectionUtils.isNotEmpty(
                    (List<Map<String, Object>>) memberOperationMap.get(JsonKey.EDIT))) {

                //No notifications to be send
            }
            // Validate Member Remove
            if (CollectionUtils.isNotEmpty((List<String>) memberOperationMap.get(JsonKey.REMOVE))) {
                Notification notification = handleMemberRemoveNotifications(memberOperationMap,groupDetails,membersInDB,updatedBy,additionalInfo);
                if(null != notification){
                    notifications.add(notification);
                }
            }
            return notifications;
        }
        return null;
    }

    private Notification handleMemberRemoveNotifications(Map memberOperationMap, Map<String, Object> groupDetails, List<MemberResponse> membersInDb, Map<String, Object> updatedBy, Map<String, Object> additionalInfo) {
        Notification notification = new Notification();
        Map<String,Object> actionData = new HashMap<>();
        actionData.put(JsonKey.CATEGORY,JsonKey.GROUP);
        actionData.put(JsonKey.CREATED_BY,updatedBy);
        actionData.put(JsonKey.ADDITIONAL_INFO,additionalInfo);
        List<String> removeMemberList = (List<String>) memberOperationMap.get(JsonKey.REMOVE);
        boolean isExitRequest = removeMemberList.size() == 1 && ((String)updatedBy.get(JsonKey.ID)).equals(removeMemberList.get(0));
        List<String> userIds = new ArrayList<>();
        if(isExitRequest){
            actionData.put(JsonKey.TYPE, NotificationType.GROUP_MEMBER_EXIT);
            Map<String,Object> templates = getMemberExitTemplateObj(groupDetails, updatedBy);
            actionData.put(JsonKey.TEMPLATE,templates);
            for (MemberResponse member:membersInDb) {
                if(!updatedBy.equals(member.getUserId()) && JsonKey.ADMIN.equals(member.getRole())){
                    userIds.add(member.getUserId());
                }
            }
        }else{
            actionData.put(JsonKey.TYPE, NotificationType.GROUP_MEMBER_REMOVED);
            Map<String,Object> templates = getMemberRemovedTemplateObj(groupDetails, updatedBy);
            actionData.put(JsonKey.TEMPLATE,templates);
            userIds.addAll(removeMemberList);
        }
        notification.setIds(userIds);
        notification.setPriority(1);
        notification.setType(JsonKey.FEED);
        notification.setAction(actionData);
        return CollectionUtils.isNotEmpty(userIds) ? notification : null;
    }

    private Notification handleMemberAddNotifications(Map memberOperationMap,Map<String,Object> groupDetails
            , Map<String,Object> updatedBy, Map<String, Object> additionalInfo) {

        Notification notification = new Notification();
        Map<String,Object> actionData = new HashMap<>();
        actionData.put(JsonKey.TYPE, NotificationType.GROUP_MEMBER_ADD);
        actionData.put(JsonKey.CATEGORY,JsonKey.GROUP);
        Map<String,Object> templates = getAddMemberTemplateObj(groupDetails, updatedBy);
        actionData.put(JsonKey.TEMPLATE,templates);
        actionData.put(JsonKey.CREATED_BY,updatedBy);
        actionData.put(JsonKey.ADDITIONAL_INFO,additionalInfo);
        List<Map<String,Object>> members = (List<Map<String, Object>>) memberOperationMap.get(JsonKey.ADD);
        List<String> userIds = new ArrayList<>();
        for (Map<String,Object> member:members) {
            userIds.add((String) member.get(JsonKey.USER_ID));
        }
        notification.setIds(userIds);
        notification.setPriority(1);
        notification.setType(JsonKey.FEED);
        notification.setAction(actionData);
        return CollectionUtils.isNotEmpty(userIds)? notification : null;
    }

    private Map<String, Object> getAdditionalInfo(Map<String, Object> groupDetails) {
        Map<String, Object> additionalInfo = new HashMap<>();
        Map<String,Object> group= new HashMap<>();
        group.put(JsonKey.ID, groupDetails.get(JsonKey.ID));
        group.put(JsonKey.NAME, groupDetails.get(JsonKey.NAME));
        additionalInfo.put(JsonKey.GROUP, group);
        return additionalInfo;
    }

    private Map<String,Object> getAddMemberTemplateObj(Map<String, Object> groupDetails, Map<String,Object> updatedBy) {
          Map<String,Object> template = new HashMap<>();
          template.put(JsonKey.TYPE, "JSON");
          Map<String,Object> props = new HashMap<>();
          props.put(JsonKey.PARAM1, groupDetails.get(JsonKey.NAME)+" "+JsonKey.GROUP);
          props.put(JsonKey.PARAM2,updatedBy.get(JsonKey.NAME));
          template.put(JsonKey.PARAMS,props);
          return template;
    }

    private Map<String,Object> getMemberExitTemplateObj(Map<String, Object> groupDetails, Map<String,Object> updatedBy) {
        Map<String,Object> template = new HashMap<>();
        template.put(JsonKey.TYPE, "JSON");
        Map<String,Object> props = new HashMap<>();
        props.put(JsonKey.PARAM1, updatedBy.get(JsonKey.NAME));;
        props.put(JsonKey.PARAM2, groupDetails.get(JsonKey.NAME)+" "+JsonKey.GROUP);
        template.put(JsonKey.PARAMS,props);

        return template;
    }

    private Map<String,Object> getMemberRemovedTemplateObj(Map<String, Object> groupDetails, Map<String,Object> updatedBy) {
        Map<String,Object> template = new HashMap<>();
        template.put(JsonKey.TYPE, "JSON");
        Map<String,Object> props = new HashMap<>();
        props.put(JsonKey.PARAM1, groupDetails.get(JsonKey.NAME)+" "+JsonKey.GROUP);;
        props.put(JsonKey.PARAM2, updatedBy.get(JsonKey.NAME));
        template.put(JsonKey.PARAMS,props);

        return template;
    }
}
