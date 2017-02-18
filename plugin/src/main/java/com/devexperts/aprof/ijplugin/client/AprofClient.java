package com.devexperts.aprof.ijplugin.client;

import com.devexperts.aprof.dump.SnapshotRoot;
import com.devexperts.aprof.ijplugin.utils.Dialogs;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by eugene on 2016/02/22.
 */
public class AprofClient {
  private final String host;
  private final int port;

  public AprofClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public @Nullable SnapshotRoot getSnapshot() throws IOException, ClassNotFoundException {
    SnapshotRoot snapshot = null;
    try (Socket clientSocket = new Socket(host, port);
         PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

      output.println("DUMP");
      try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())) {
        snapshot = (SnapshotRoot) input.readObject();
      }
    } catch (Exception e) {
      Dialogs.showErrorNotification("Can't connect to " + host + ":" + port);
      throw e;
    }

    return snapshot;
  }

}
