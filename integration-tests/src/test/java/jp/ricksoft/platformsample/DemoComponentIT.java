/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ricksoft.platformsample;

import org.alfresco.rad.test.AbstractAlfrescoIT;
import org.alfresco.rad.test.AlfrescoTestRunner;
import org.alfresco.rad.test.Remote;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.ricksoft.alfresco.image.recognition.ImageRecognitionServices;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Integration Test of the DemoComponent using the Alfresco Test Runner.
 * The Alfresco Test Runner (i.e. AlfrescoTestRunner.class) will check if it is running in an Alfresco instance,
 * if so it will execute normally locally. On the other hand, if it detects no
 * Alfresco Spring context, then it will make a call to a custom Web Script that
 * will execute this test in the running container remotely. The remote location is
 * determined by the @Remote config.
 *
 * @author martin.bergljung@alfresco.com
 * @since 3.0
 */
@RunWith(value = AlfrescoTestRunner.class)
// Specifying the remote endpoint is not required, it
// will default to http://localhost:8080/alfresco if
// not provided. This shows the syntax but simply
// sets the value back to the default value.
@Remote(endpoint = "http://localhost:8080/alfresco")
public class DemoComponentIT extends AbstractAlfrescoIT {

 
    
	@Test
	public void testCreateSampleNode(){
		QName type = ContentModel.TYPE_CONTENT;
		
		Map<QName, Serializable> nodeProperties = new HashMap<>();
		
		NodeRef nodeRef = createNode("IMG_2883.JPG", type, nodeProperties);
		assertNotNull(nodeRef);
		
		String sampleFile = DemoComponentIT.class.getClassLoader().getResource("IMG_2883.JPG").getPath();
		//System.out.println("File Path: " + sampleFile);
		addFileContent(nodeRef, new File(sampleFile));
		
		ImageRecognitionServices service = (ImageRecognitionServices) getApplicationContext().getBean("awsRecognitionService");
		service.setTags(nodeRef);
		
		List<String> tags = getServiceRegistry().getTaggingService().getTags(nodeRef);
		
		assertEquals(tags.size(), 3);
		//System.out.println("---------------");
		for(String tag : tags){
			System.out.println(tag);
		}
		//System.out.println("---------------");
		
		assertEquals(tags.size(), 1);
		
		//Cleanup Node
        if (nodeRef != null) {
            getServiceRegistry().getNodeService().deleteNode(nodeRef);
        }
	}
	
	
	
	/*
	 * Helper Methods
	 */

    
    private NodeRef createNode(String name, QName type, Map<QName, Serializable> properties) {
        NodeRef parentFolderNodeRef = getCompanyHomeNodeRef();
        QName associationType = ContentModel.ASSOC_CONTAINS;
        QName associationQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                QName.createValidLocalName(name));
        properties.put(ContentModel.PROP_NAME, name);
        ChildAssociationRef parentChildAssocRef = getServiceRegistry().getNodeService().createNode(
                parentFolderNodeRef, associationType, associationQName, type, properties);

        return parentChildAssocRef.getChildRef();
    }
    
    private NodeRef getCompanyHomeNodeRef() {
        return getServiceRegistry().getNodeLocatorService().getNode(CompanyHomeNodeLocator.NAME, null, null);
    }
    
    private void addFileContent(NodeRef nodeRef, String fileContent) {
        boolean updateContentPropertyAutomatically = true;
        ContentWriter writer = getServiceRegistry().getContentService().getWriter(nodeRef, ContentModel.PROP_CONTENT,
                updateContentPropertyAutomatically);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent(fileContent);
    }
    
    private void addFileContent(NodeRef nodeRef, File file){
    	boolean updateContentPropertyAutomatically = true;
    	ContentWriter writer = getServiceRegistry().getContentService().getWriter(nodeRef, ContentModel.PROP_CONTENT, updateContentPropertyAutomatically);
    	writer.putContent(file);
    }
}