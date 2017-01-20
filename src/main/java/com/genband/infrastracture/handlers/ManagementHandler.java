package com.genband.infrastracture.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.genband.infrastracture.management.LifeCycleManagement;

public class ManagementHandler implements Runnable {

  private static Logger log = Logger.getLogger(ManagementHandler.class);

  private Socket socket;
  private PrintWriter writer;
  private BufferedReader reader;

  public ManagementHandler(Socket socket) {

    super();
    this.socket = socket;

    try {

      writer = new PrintWriter(socket.getOutputStream());
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    } catch (Exception e) {
      e.printStackTrace();
      try {
        socket.close();
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }

  }

  @Override
  public void run() {

    try {

      String content = getSocketDataData();
      processAction(content);

      writer.close();
      reader.close();
      socket.close();


    } catch (Exception e) {

      try {
        socket.close();
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      e.printStackTrace();

    }

  }

  private void processAction(String content) throws IOException {
    // TODO Auto-generated method stub
    switch (content) {

      case "start":
        log.info("start server... ");
        LifeCycleManagement.getInstance().runServer();
        break;

      case "stop":
        log.info("stop server... ");
        LifeCycleManagement.getInstance().stopServer();
        break;

      case "heartbeat":
        log.info("heartbeating... ");
        LifeCycleManagement.getInstance().heartBeat(writer);
        break;

      default:
        log.error("Unknow action: " + content + "|end");

    }

  }

  private String getSocketDataData() throws IOException {
    // TODO Auto-generated method stub
    StringBuilder sb = new StringBuilder();
    String line;

    // while ((line = reader.readLine()) != null) {
    line = reader.readLine();
    sb.append(line);
    // }

    String content = sb.toString();

    return content;

  }

}
