package org.sunbird.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sunbird.actors.GroupNotificationActor;
import org.sunbird.common.util.JsonKey;
import org.sunbird.common.util.Notification;
import org.sunbird.util.helper.PropertiesCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {
    private LoggerUtil logger = new LoggerUtil(NotificationService.class);

    private static String notificationServiceUrl;
    private static String notificationServiceBaseUrl;
    private static String max_batch_limits;
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        notificationServiceBaseUrl = PropertiesCache.getInstance().getProperty(JsonKey.NOTIFICATION_SERVICE_BASE_URL);
        notificationServiceUrl = PropertiesCache.getInstance().getProperty(JsonKey.NOTIFICATION_SERVICE_API_URL);
        max_batch_limits = PropertiesCache.getInstance().getProperty(JsonKey.MAX_BATCH_LIMIT);
    }

    public void sendSyncNotification(List<Notification> notifications, Map<String,Object> reqContext){
        List<Notification> notificationList = new ArrayList<>();
        for (Notification notification: notifications) {
            List<String> ids = notification.getIds();
            notificationList.addAll(createNotificationsBatch(notification));
        }
        Map<String, String> requestHeader = new HashMap<>();
        getUpdatedRequestHeader(requestHeader, reqContext);
        try {
            Map<String,Object> notificationReq = new HashMap<>();
            notificationReq.put(JsonKey.NOTIFICATIONS,notificationList);
            Map<String,Object> request  = new HashMap<>();
            request.put(JsonKey.REQUEST,notificationReq);
            String notificationStrReq = objectMapper.writeValueAsString(request);
            logger.info(reqContext,notificationStrReq);
            String response =
                        HttpClientUtil.post(
                                notificationServiceBaseUrl + notificationServiceUrl, notificationStrReq, requestHeader,reqContext);

        } catch (JsonProcessingException ex) {
            logger.error(reqContext,"Error sending notifications",ex);
        }
    }

    private List<Notification> createNotificationsBatch(Notification notification) {
        List<Notification> notifications = new ArrayList<>();
        int idSize = notification.getIds().size();
        int maxBatchLimit = Integer.parseInt(max_batch_limits);
        List<List<String>> idLists = new ArrayList<>();
        List<String> ids = notification.getIds();
        int index = 0;
        for (int i=0 ; i<ids.size(); i++){
            if(i % maxBatchLimit == 0){
                idLists.add(new ArrayList<>());
                index++;
            }
            idLists.get(index-1).add(ids.get(i));
        }
        for (List<String> idList : idLists) {
            notifications.add(createNotificationObj(notification, idList));
        }
        return notifications;
    }

    private Notification createNotificationObj(Notification notification, List<String> idList) {
            Notification newNotificationObj = new Notification();
            newNotificationObj.setIds(idList);
            newNotificationObj.setAction(notification.getAction());
            newNotificationObj.setPriority(notification.getPriority());
            newNotificationObj.setType(notification.getType());
            return newNotificationObj;
    }

    void getUpdatedRequestHeader(Map<String, String> header, Map<String, Object> reqContext) {
        if (null == header) {
            header = new HashMap<>();
        }
        header.put("Content-Type", "application/json");
        setTraceIdInHeader(header, reqContext);
    }

    public static void setTraceIdInHeader(Map<String, String> header,  Map<String, Object> reqContext) {
        if (null != reqContext) {
            header.put(JsonKey.X_TRACE_ENABLED, (String) reqContext.get(JsonKey.X_TRACE_ENABLED));
            header.put(JsonKey.X_REQUEST_ID, (String) reqContext.get(JsonKey.X_REQUEST_ID));
        }
    }
}
