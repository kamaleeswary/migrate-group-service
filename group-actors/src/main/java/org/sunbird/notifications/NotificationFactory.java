package org.sunbird.notifications;

import org.sunbird.common.util.NotificationType;

public class NotificationFactory {


    public static INotificationHandler  getNotificationHandler(String operation){
        INotificationHandler notificationHandler = null;
        switch (operation){
            case NotificationType.ACTIVITY_UPDATE :
                          notificationHandler = new ActivityUpdateNotificationHandler();
                          break;
            case NotificationType.MEMBER_UPDATE:
                          notificationHandler = new MemberUpdateNotificationHandler();
                          break;
            case NotificationType.GROUP_DELETE:
                          notificationHandler = new GroupDeleteNotificationHandler();
                          break;

        }
        return notificationHandler;
    }
}
