package com.genband.infrastracture.management;

public class SharedConstantValue {

  public static final String TRYING = "SIP/2.0 100 Trying";
  public static final String OK_STATUS_FOR_LOGIN = "SIP/2.0 200 OK";

  public static final String UDP_USER_FROM =
      "From: [\"a-zA-Z0-9\\.\\: ]*<sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";
  public static final String UDP_USER_TO =
      "To: [\"a-zA-Z0-9\\.\\: ]*<sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";
  public static final String UDP_CONTACT =
      "Contact: [\"a-zA-Z0-9\\.\\: ]*<sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";


}
