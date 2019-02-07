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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.nio.ByteBuffer;

public class AWSImageRecognitionServices implements ImageRecognitionServices{

	private TaggingService		taggingService;
	private ContentService		contentService;

	private AmazonRekognition	client;
	private String				accessKey;
	private String				secretKey;

	private int					maxNumberOfLabels;
	private float				confidentLevel;
	
	private static Logger 		logger = Logger.getLogger(AWSImageRecognitionServices.class);
	
	
	// This is necessary to initialize after this bean is created.
    public void initialize() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		
		this.client = AmazonRekognitionClientBuilder
				.standard()
				.withRegion(Regions.US_WEST_2)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
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
	      
	    List<String> tags = new ArrayList<String>();
	    try {
	         DetectLabelsResult result = client.detectLabels(request);
	         List <Label> labels = result.getLabels();
	         
	         for (Label label: labels) {
	        	logger.info("Getting Tag : "+label.getName() + " with confidence :" + label.getConfidence());
	            tags.add(label.getName());
	         }
	      } catch(AmazonRekognitionException e) {
	         e.printStackTrace();
	      }
	      
	      return tags;
	}
	
	// Detect labels by AWS services. passing image inputStream.
	private DetectLabelsRequest sendDetectLablesRequest(InputStream inputStream){

		DetectLabelsRequest request = null;
		try{
		    request = new DetectLabelsRequest();
		    request.setImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(inputStream))));
		    request.withMaxLabels(maxNumberOfLabels);
		    request.withMinConfidence(confidentLevel);

		} catch(IOException ex){
			logger.error("Error while requesting recognition to AWS.");
			ex.printStackTrace();
		}
		
		return request;
	}
	
	/* Setters for spring bean properties */
	public void setTaggingService(TaggingService taggingService) {
		this.taggingService = taggingService;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	
	public void setMaxNumberOfLabels(int maxNumberOfLabels) {
		this.maxNumberOfLabels = maxNumberOfLabels;
	}

	public void setConfidentLevel(float confidentLevel) {
		this.confidentLevel = confidentLevel;
	}

	
}
