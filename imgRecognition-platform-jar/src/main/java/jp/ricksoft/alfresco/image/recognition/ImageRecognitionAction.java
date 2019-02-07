/*
 * Copyright 2019 Ricksoft Co., Ltd.
 * All rights reserved.
 */
package jp.ricksoft.alfresco.image.recognition;

import java.util.List;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

public class ImageRecognitionAction extends ActionExecuterAbstractBase {
	private ImageRecognitionServices imgRecognitionService;
	
	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		imgRecognitionService.setTags(nodeRef);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		/* Does not need parameters */
	}
	
	/* setter for spring bean properties */
	public void setImgRecognitionService(ImageRecognitionServices imgRecognitionService) {
		this.imgRecognitionService = imgRecognitionService;
	}
}
