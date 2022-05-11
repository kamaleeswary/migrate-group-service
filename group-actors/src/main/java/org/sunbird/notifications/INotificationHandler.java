package org.sunbird.notifications;

import org.sunbird.common.request.Request;
import org.sunbird.common.util.Notification;

import java.util.List;
import java.util.Map;

public interface INotificationHandler {

    List<Notification> getNotificationObj(Request request, Map<String,Object> updatedBy);
}
