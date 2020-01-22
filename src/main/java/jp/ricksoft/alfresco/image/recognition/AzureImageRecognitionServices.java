/*
 * Copyright 2019 Ricksoft Co., Ltd.
 * All rights reserved.
 */
package jp.ricksoft.alfresco.image.recognition;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.tagging.TaggingService;
import org.apache.log4j.Logger;

import java.net.URI;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class AzureImageRecognitionServices implements ImageRecognitionServices {

	private TaggingService taggingService;
	private ContentService contentService;

	private String         subscriptionKey;
	private String         endpoint;

	private static String  uriBase;

	private static Logger  logger = Logger.getLogger(AzureImageRecognitionServices.class);

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

	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	// This is necessary to initialize after this bean is created.
	public void initialize() {
		uriBase = endpoint + "vision/v2.1/read/core/asyncBatchAnalyze";
		logger.info("Endpoint setting success.");
	}

	@Override
	public void setTags(NodeRef node) {
		// Unimplemented yet.
	}

	@Override
	public List<String> recognizeImageAndGetTagList(NodeRef nodeRef) {

		ContentReader contentReader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);

		CloseableHttpClient httpTextClient = HttpClientBuilder.create().build();
		CloseableHttpClient httpResultClient = HttpClientBuilder.create().build();;

		try {
			// This operation requires two REST API calls. One to submit the image
			// for processing, the other to retrieve the text found in the image.

			URIBuilder builder = new URIBuilder(uriBase);

			// Prepare the URI for the REST API method.
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);

			// Request headers.
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);


		} catch (Exception e) {
			e.printStackTrace();
		}
		// Unimplemented yet.
		return null;
	}

}
