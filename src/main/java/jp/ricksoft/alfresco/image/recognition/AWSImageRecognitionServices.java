/*
 * Copyright 2019 Ricksoft Co., Ltd.
 * All rights reserved.
 */
package jp.ricksoft.alfresco.image.recognition;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.tagging.TaggingService;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

public class AWSImageRecognitionServices implements ImageRecognitionServices{

	private TaggingService		taggingService;
	private ContentService		contentService;

	private RekognitionClient	client;
	private String				accessKey;
	private String				secretKey;
	private int					maxNumberOfLabels;
	private float				confidentLevel;
	
	private static Logger 		logger = Logger.getLogger(AWSImageRecognitionServices.class);

	/* Setters for spring bean properties */
	public void setTaggingService(TaggingService taggingService) {
		this.taggingService = taggingService;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public void setMaxNumberOfLabels(String maxNumberOfLabels) {
		this.maxNumberOfLabels = Integer.parseInt(maxNumberOfLabels);
	}

	public void setConfidentLevel(String confidentLevel) {
		this.confidentLevel = Float.parseFloat(confidentLevel);
	}

	// This is necessary to initialize after this bean is created.
    public void initialize() {
		StaticCredentialsProvider credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(this.accessKey, this.secretKey));

		this.client = RekognitionClient.builder()
				.credentialsProvider(credentials)
				.region(Region.US_WEST_2)
				.build();
		
		logger.info("AWS Credential Initialized");
    }	
	
	@Override
	public void setTags(NodeRef nodeRef) {

		List<String> tags = this.recognizeImageAndGetTagList(nodeRef);
		taggingService.addTags(nodeRef, tags);
		
	}
	
	@Override
	public List<String> recognizeImageAndGetTagList(NodeRef nodeRef) {
		
		ContentReader contentReader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		DetectLabelsRequest request = sendDetectLablesRequest(contentReader.getContentInputStream());
	      
	    List<String> tags = new ArrayList<>();
	    try {
	         DetectLabelsResponse result = client.detectLabels(request);
	         List <Label> labels = result.labels();
	         
	         for (Label label: labels) {
	        	logger.info("Getting Tag : "+label.name() + " with confidence :" + label.confidence());
	            tags.add(label.name());
	         }
	      } catch(RekognitionException e) {
	         e.printStackTrace();
	      }
	      
	      return tags;
	}
	
	// Detect labels by AWS services. passing image inputStream.
	private DetectLabelsRequest sendDetectLablesRequest(InputStream inputStream){

		DetectLabelsRequest request = null;
		try{
			request = DetectLabelsRequest.builder()
					.image(Image.builder().bytes(SdkBytes.fromByteBuffer(ByteBuffer.wrap(IOUtils.toByteArray(inputStream)))).build())
					.maxLabels(maxNumberOfLabels)
					.minConfidence(confidentLevel)
					.build();

		} catch(IOException ex){
			logger.error("Error while requesting recognition to AWS.");
			ex.printStackTrace();
		}
		
		return request;
	}
	
}
