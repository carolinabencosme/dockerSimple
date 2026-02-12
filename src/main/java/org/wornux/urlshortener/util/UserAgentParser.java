package org.wornux.urlshortener.util;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * Utility class for parsing user agent strings to extract IP, browser, and operating system
 * details.
 */
public class UserAgentParser {

  /**
   * Parses the user agent string to extract browser information.
   *
   * @param userAgentString The user agent string.
   * @return The name of the browser.
   */
  public static String getBrowser(String userAgentString) {
    UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
    Browser browser = userAgent.getBrowser();
    return browser.getName();
  }

  /**
   * Parses the user agent string to extract operating system information.
   *
   * @param userAgentString The user agent string.
   * @return The name of the operating system.
   */
  public static String getOperatingSystem(String userAgentString) {
    UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
    OperatingSystem os = userAgent.getOperatingSystem();
    return os.getName();
  }

  /**
   * Extracts the IP address from the HTTP context.
   *
   * @param ctx The Javalin HTTP context.
   * @return The IP address of the client.
   */
  public static String getIpAddress(io.javalin.http.Context ctx) {
    return ctx.ip();
  }
}
