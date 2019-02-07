/*
 * Copyright 2019 Ricksoft Co., Ltd.
 * All rights reserved.
 */
package jp.ricksoft.alfresco.image.recognition;

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

public class AzureImageRecognitionServices implements ImageRecognitionServices {


	@Override
	public void setTags(NodeRef node) {
		// Unimplemented yet.
	}

	@Override
	public List<String> recognizeImageAndGetTagList(NodeRef nodeRef) {
		// Unimplemented yet.
		return null;
	}

}
