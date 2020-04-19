package com.easycompany.trappd.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AwsClient implements AwsS3Client {

  private AmazonS3 s3client;

  @Value("${amazonProperties.bucketName}")
  private String bucketName;

  @Value("${amazonProperties.accessKey}")
  private String accessKey;

  @Value("${amazonProperties.secretKey}")
  private String secretKey;

  @PostConstruct
  private void initializeAws() {
    try {
      if (isAccessSecretKeyAvailable()) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3client =
            AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_SOUTH_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
      } else {
        this.s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();
      }
    } catch (Exception e) {
      this.s3client=AmazonS3ClientBuilder.defaultClient();
    }
  }

  private boolean isAccessSecretKeyAvailable() {
    return accessKey != null && !accessKey.equals("") && secretKey != null && !secretKey.equals("");
  }

  @Override
  public void uploadFile(InputStream inputStream, String fileName, long contentLength) {
    log.info("File ready to upload to S3 [ name: {}]", fileName);
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(contentLength);
    PutObjectRequest request =
        new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata);
    s3client.putObject(request);
    log.info("File uploaded to S3 at path {}/{}", bucketName, fileName);
  }

  @NotNull
  @Override
  public InputStream downloadFile(String fileName) throws FileNotFoundException {
    log.info("File ready to download from S3 [ name: {}]", fileName);
    String directory = "/tmp/trappd/";
    File file = new File(directory + fileName);
    if (!file.exists()) {
      log.debug("Local file doesn't exists");
      GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName);
      S3Object s3Object = s3client.getObject(getObjectRequest);
      log.info("File downloaded from S3 at path {}/{}", bucketName, fileName);
      return copyToDumpFile(s3Object.getObjectContent(), fileName, directory);
    } else {
      log.debug("Local file exists. Falling back to local file instead of S3");
      try {
        return new FileInputStream(file);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    throw new FileNotFoundException("File not found to be downloaded with name " + fileName);
  }

  private InputStream copyToDumpFile(
      S3ObjectInputStream inputStream, String fileName, String directoryName) {
    log.debug("Copying file to local instance");
    FileOutputStream outstream = null;

    try {
      File directory = new File(directoryName);
      if (!directory.exists()) {
        directory.mkdir();
      }
      File outfile = new File(directoryName + fileName);
      if (!outfile.exists()) {
        outfile.createNewFile();
      }
      outstream = new FileOutputStream(outfile);

      byte[] buffer = new byte[1024];

      int length;
      /*copying the contents from input stream to
       * output stream using read and write methods
       */
      while ((length = inputStream.read(buffer)) > 0) {
        outstream.write(buffer, 0, length);
      }

      // Closing the input/output file streams
      inputStream.close();
      outstream.close();

      System.out.println("File copied successfully!!");
      return new FileInputStream(directoryName + fileName);

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return null;
  }
  /*@Override
  public String downloadFileInString(String pathToFile) throws IOException {
    byte[] base64byte = Base64.encodeBase64(downloadFileInBytes(pathToFile));
    return new String(base64byte, StandardCharsets.US_ASCII);
  }

  @Override
  public byte[] downloadFileInBytes(String pathToFile) throws IOException {
    File filePath = new File(pathToFile);
    GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filePath.getName());
    S3Object s3Object = null;
    try {
      s3Object = s3client.getObject(getObjectRequest);
    } catch (AmazonS3Exception e) {
      log.error("S3 bucket file as String not working for file : {}", e);
      return null;
    } catch (Exception e) {
      log.error("S3 bucket file as String not working for file : {}", e);
      return null;
    }
    S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
    return IOUtils.toByteArray(objectInputStream);
  }


  @Override
  public void deleteFiles(String... files) {
    String filename[] = new String[files.length];
    int i = 0;
    for (String file : files) {
      filename[i++] = file.substring(file.lastIndexOf("/") + 1);
    }
    try {
      DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(bucketName).withKeys(filename);
      s3client.deleteObjects(delObjReq);
      Arrays.stream(filename).forEach(f -> log.info("Files are deleted with name :{} ", f));

    } catch (SdkClientException e) {
      log.error("Error to delete files :{} ", e);

    } catch (Exception e) {
      log.error("Error to delete files : {} ", e);
    }
  }*/
}
