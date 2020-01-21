/*
 * Copyright 2019 Ricksoft Co., Ltd.
 * All rights reserved.
 */
package jp.ricksoft.alfresco.image.recognition;


import java.io.InputStream;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

public interface ImageRecognitionServices {
	// Get Tag from Image
	List<String> recognizeImageAndGetTagList(NodeRef nodeRef);
	
	// Set Tags to Node
	void setTags(NodeRef nodeRef);
	
}
