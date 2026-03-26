package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.util.StringUtils;

/**
 * Compatibility stub for CibSeven 2.1 auto-configuration, which references this interface by its
 * Spring Boot 3.x location. In Spring Boot 4, the real interface moved to
 * {@code org.springframework.boot.jersey.autoconfigure.JerseyApplicationPath}.
 *
 * <p>Mirrors the full API so that CibSeven code compiled against Boot 3 can resolve all methods.
 * This stub can be removed once CibSeven ships Spring Boot 4 compatible starters.
 */
public interface JerseyApplicationPath {

  String getPath();

  default String getRelativePath(String path) {
    String prefix = getPrefix();
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    return prefix + path;
  }

  default String getPrefix() {
    String path = getPath();
    String prefix = (StringUtils.hasLength(path) ? path : "");
    if (prefix.endsWith("/*")) {
      prefix = prefix.substring(0, prefix.length() - 2);
    }
    return prefix;
  }

  default String getUrlMapping() {
    String path = getPath();
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    if (path.equals("/")) {
      return "/*";
    }
    if (path.endsWith("/*")) {
      return path;
    }
    return path.endsWith("/") ? path + "*" : path + "/*";
  }
}
