/*
 * Copyright 2019 Ricksoft Co., Ltd.
 * All rights reserved.
 */
package jp.ricksoft.alfresco.image.recognition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.tagging.TaggingService;
import org.apache.log4j.Logger;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.visual_recognition.v3.model.ClassifyOptions;
import com.ibm.watson.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.visual_recognition.v3.model.ClassResult;

public class BluemixImageRecognitionServices implements ImageRecognitionServices {

	private TaggingService		taggingService;
	private ContentService		contentService;
	private NodeService			nodeService;

	private VisualRecognition 	ibmVisualRecognitionService;

	private String 				apiKey;
	private String				version;
	private float				confidentLevel;
	private String              customModelId;

	private static Logger		logger = Logger.getLogger(BluemixImageRecognitionServices.class);

	/* setter for spring bean properties */
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setTaggingService(TaggingService taggingService) {
		this.taggingService = taggingService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setConfidentLevel(float confidentLevel) {
		this.confidentLevel = confidentLevel;
	}

	public void setCustomModelId(String customModelId) {
		this.customModelId = customModelId;
	}

	// This is necessary to initialize after this bean is created.
	public void initialize() {
		IamAuthenticator authenticator = new IamAuthenticator(apiKey);
		this.ibmVisualRecognitionService = new VisualRecognition(version, authenticator);
	}

	@Override
	public List<String> recognizeImageAndGetTagList(NodeRef nodeRef){

		ContentReader contentReader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		String fileName = (String)nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		InputStream in = contentReader.getContentInputStream();
		if(in == null){
			logger.error("InputStream for content is null");
			throw new ContentIOException("InputStream for content is null!");
		}


		List<String> tags = new ArrayList<String>();
		File tmpFile = null;
		try {
			// File type gives to Bluemix is jpeg format only
			tmpFile = File.createTempFile("vision-"+fileName, ".jpg");
			tmpFile.deleteOnExit();
			Files.copy(in, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
					.acceptLanguage("ja")
					.threshold(confidentLevel)
					.imagesFile(tmpFile)
					.classifierIds(Arrays.asList(customModelId))
					.build();
			ClassifiedImages returnClassify = ibmVisualRecognitionService.classify(classifyOptions).execute().getResult();
			// Always passing only one image to watson. then below get first image (0).
			List<ClassResult> classifiers = returnClassify.getImages().get(0).getClassifiers().get(0).getClasses();
			for(ClassResult cls: classifiers){
				tags.add(cls.getXClass());
			}

		} catch (IOException ex) {
			logger.error("IOException occured while creating temp file for BluemixImageRecognition !" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if(tmpFile != null && tmpFile.exists()){
				tmpFile.delete();
			}
		}

		/* For debug
		 * Watson eventually returns empty tag, it raises lucene error when indexing tags.
		 */
		if(logger.isDebugEnabled()){
			logger.debug("Number of tags: " + tags.size());
			for(String tag: tags){
				if(tag.isEmpty()){
					logger.debug("Tag is Empty !!!!");
				}
				else {
					logger.debug("Tag : " + tag);
				}
			}
		}

		// Remove empty tag in case.
		for(int i=0; i< tags.size(); i++){
			if(tags.get(i).isEmpty()){
				tags.remove(i);
			}
		}

		return tags;
	}


	// Set tags to corresponding node
	@Override
	public void setTags(NodeRef nodeRef) {
		List<String> tags = this.recognizeImageAndGetTagList(nodeRef);
		taggingService.setTags(nodeRef, tags);
	}

}