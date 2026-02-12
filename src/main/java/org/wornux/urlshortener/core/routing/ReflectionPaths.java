package org.wornux.urlshortener.core.routing;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionPaths {

  public static List<Class<?>> getClasses(String packageName) {
    List<Class<?>> classes = new ArrayList<>();
    String path = packageName.replace('.', '/');
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL resource = classLoader.getResource(path);

    if (resource == null) {
      throw new RuntimeException("Package not found: " + packageName);
    }

    try {
      if (resource.getProtocol().equals("file")) {
        // Running from filesystem
        File directory = new File(resource.toURI());
        if (!directory.exists()) {
          throw new RuntimeException("Directory not found: " + directory.getAbsolutePath());
        }

        for (String file : directory.list()) {
          if (file.endsWith(".class")) {
            String className = packageName + '.' + file.substring(0, file.length() - 6);
            classes.add(Class.forName(className));
          }
        }
      } else if (resource.getProtocol().equals("jar")) {
        // Running from a JAR file
        JarFile jarFile;
        String jarPath;

        if (resource.toString().startsWith("jar:file:")) {
          jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
          jarFile = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        } else {
          JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
          jarFile = jarConnection.getJarFile();
        }

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          String entryName = entry.getName();
          if (entryName.startsWith(path) && entryName.endsWith(".class")) {
            String className = entryName.replace('/', '.').replace(".class", "");
            classes.add(Class.forName(className));
          }
        }
        jarFile.close();
      }
    } catch (IOException | ClassNotFoundException | URISyntaxException e) {
      throw new RuntimeException("Failed to load classes from package: " + packageName, e);
    }

    return classes;
  }
}
