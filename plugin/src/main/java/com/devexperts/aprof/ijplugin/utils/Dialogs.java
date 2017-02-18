package com.devexperts.aprof.ijplugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * Created by eugene on 2016/04/17.
 */
public class Dialogs {
  public static void showErrorNotification(String content) {
    Notification notification = new Notification("Aprof", "Aprof", content, NotificationType.ERROR);
    Notifications.Bus.notify(notification);
  }
}
