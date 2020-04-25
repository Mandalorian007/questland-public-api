package com.questland.handbook.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class QueryUtils {

  public static HttpHeaders getHttpHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add("token", token);
    headers.add("Accept", APPLICATION_JSON.getType());
    headers.add("Content-Type", "application/x-www-form-urlencoded");
    return headers;
  }
}
