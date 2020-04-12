package com.easycompany.trappd.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  /*@Override
    public Resource downloadFile(String pathToFile) throws AmazonS3BucketException {
      File filePath = new File(pathToFile);
      GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filePath.getName());

      Resource resource = null;
      try {
        S3Object s3Object = s3client.getObject(getObjectRequest);
        resource = new InputStreamResource(s3Object.getObjectContent());

      } catch (AmazonS3Exception e) {
        log.error("S3 bucket download not working for file : {}", e);
        throw new AmazonS3BucketException(pathToFile);

      } catch (Exception e) {
        log.error("S3 bucket download not working for file : {}", e);
        throw new AmazonS3BucketException(pathToFile);
      }
      return resource;
    }
  */
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
