package com.easycompany.trappd.config;

import java.io.InputStream;

public interface AwsS3Client {

  void uploadFile(InputStream inputStream, String fileName, long contentLength);

  InputStream downloadFile(String fileName);
}
