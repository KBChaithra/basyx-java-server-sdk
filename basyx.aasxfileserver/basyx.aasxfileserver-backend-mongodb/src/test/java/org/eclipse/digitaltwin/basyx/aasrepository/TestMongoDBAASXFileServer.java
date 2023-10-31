/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/


package org.eclipse.digitaltwin.basyx.aasrepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.MongoDBAasxFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.MongoDBAasxFileServerFactory;
import org.eclipse.digitaltwin.basyx.aasxfileserver.core.AASXFileServerSuite;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescription;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Tests the {@link MongoDBConceptDescriptionRepository}
 * 
 * @author chaithra
 *
 */
public class TestMongoDBAASXFileServer extends AASXFileServerSuite {

	private final String COLLECTION = "aasxFileServerCollection";
	
	private static ConfigurableApplicationContext appContext;
	
	private static GridFsTemplate gridFsTemplate;
	
	
	@BeforeClass
	public static void setUp() {
		appContext = SpringApplication.run(DummyAASXComponent.class, new String[] {});
		gridFsTemplate = appContext.getBean(GridFsTemplate.class);
	}
	
//	@Mock
//	private MongoTemplate mongoTemplate;
	
	@Override
	protected AASXFileServer getAasxFileServer() {
		MongoTemplate template = createTemplate();

		clearDatabase(template);

		return new MongoDBAasxFileServerFactory(template, COLLECTION, gridFsTemplate).create();
	}

	private MongoTemplate createTemplate() {
		String connectionURL = "mongodb://127.0.0.1:27017/";
		MongoClient client = MongoClients.create(connectionURL);
		MongoTemplate template = new MongoTemplate(client, "BaSyxTestDb");
		
		return template;
	}
	
	private void clearDatabase(MongoTemplate template) {
		template.remove(new Query(), COLLECTION);
	}
	
	private void assertIsEmpty(Collection<PackageDescription> packageDescription) {
		assertTrue(packageDescription.isEmpty());
	}		
	

}
