/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.aasxfileserver;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.swing.text.Document;

import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackagesBody;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.result.DeleteResult;

/**
 * 
 * MongoDB implementation of the AasxFileServer
 *
 * @author chaithra
 *
 */
public class MongoDBAasxFileServer implements AASXFileServer {
	private static final String IDJSONPATH = "id";

	private MongoTemplate mongoTemplate;
	private String collectionName;
	
	private GridFsTemplate gridFsTemplate;

	private AtomicInteger packageId = new AtomicInteger(1);

	public MongoDBAasxFileServer(MongoTemplate mongoTemplate, String collectionName, GridFsTemplate gridFsTemplate) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		this.gridFsTemplate = gridFsTemplate;
		configureIndexForAasxFileId(mongoTemplate);
	}
	
	@Override
	public Collection<PackageDescription> getAllAASXPackageIds() {
		return mongoTemplate.findAll(PackageDescription.class, collectionName);
	}
	
	@Override
	public InputStream getAASXByPackageId(String packageId) throws ElementDoesNotExistException {
		InputStream inputStream = mongoTemplate.findOne(new Query().addCriteria(Criteria.where(IDJSONPATH).is(packageId)), InputStream.class, collectionName);
		
		if (inputStream == null)
			throw new ElementDoesNotExistException(packageId);
		
		return inputStream.getClass().getResourceAsStream(packageId);
	}

	@Override
	public void updateAASXByPackageId(String packageId, List<String> aasIds, InputStream file, String filename)
			throws ElementDoesNotExistException {
		
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(packageId));;
		
			throwIfAasxFileDoesNotExist(query, packageId);
				
		mongoTemplate.remove(query, Package.class, collectionName);
		mongoTemplate.save(packageId, collectionName);
	}

	@Override
	public PackageDescription createAASXPackage(List<String> aasIds, InputStream file, String fileName) throws CollidingIdentifierException {

//		int packageId = fetchAndUpdateLastPackageId();		 
				
		int packageId = 1;
		
		PackageDescription packageDescription = createPackageDescription(aasIds, String.valueOf(packageId));		
		
		if (mongoTemplate.exists(new Query(), Package.class, collectionName))
			throw new CollidingIdentifierException(fileName);
		
		gridFsTemplate.store(file, fileName, packageDescription);
		
//		mongoTemplate.save(packageDescription, collectionName);
		
		return packageDescription;
	}
	
	private int fetchAndUpdateLastPackageId() {
	    Query query = new Query().limit(1).with(Sort.by(Sort.Direction.DESC, "packageId"));
	    
	    MongoDBAasxFileServer lastAasxFileServer = mongoTemplate.findOne(query, MongoDBAasxFileServer.class, collectionName);

	    if (lastAasxFileServer != null) {
	        AtomicInteger lastPackageId = lastAasxFileServer.packageId;
	        int newPackageId = lastPackageId.addAndGet(1);

	        return newPackageId;
	    } 
	    else return 1;
	}
	

	@Override
	public void deleteAASXPackageById(String packageId) throws ElementDoesNotExistException {
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(packageId));
		
		DeleteResult result = mongoTemplate.remove(query, Package.class, collectionName);

		if (result.getDeletedCount() == 0)
			throw new ElementDoesNotExistException(packageId);
		
	}
	
	private PackagesBody createPackagesBody(List<String> aasIds, InputStream file, String fileName) {
		PackagesBody packagesBody = new PackagesBody();
		packagesBody.aasIds(aasIds);
		packagesBody.file(file);
		packagesBody.fileName(fileName);
		return packagesBody;
	}
	
	
	private void configureIndexForAasxFileId(MongoTemplate mongoTemplate) {
		TextIndexDefinition idIndex = TextIndexDefinition.builder().onField(IDJSONPATH).build();
		mongoTemplate.indexOps(PackagesBody.class).ensureIndex(idIndex);
	}
	
	private void throwIfAasxFileDoesNotExist(Query query, String packageId) {
		if (!mongoTemplate.exists(query, Package.class, collectionName))
			throw new ElementDoesNotExistException(packageId);
	}
	
	private PackageDescription createPackageDescription(List<String> aasIds, String newpackageId) {
		PackageDescription packageDescription = new PackageDescription();
		packageDescription.packageId(newpackageId);
		packageDescription.aasIds(aasIds);
		return packageDescription;
	}	
	
}
