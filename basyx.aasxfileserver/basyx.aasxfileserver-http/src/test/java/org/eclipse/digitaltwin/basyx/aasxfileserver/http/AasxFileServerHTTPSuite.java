/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasxfileserver.http;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Tests the ConceptDescription specific parts of the
 * {@link ConceptDescriptionRepository} HTTP/REST API
 * 
 * @author chaithra
 *
 */
public abstract class AasxFileServerHTTPSuite {
	protected abstract String getURL();

	@Before
	@After
	public abstract void resetRepository();
	
	public static final String AASX_FILE_SERVER_ACCESS_URL = "http://localhost:8080/aasxFileserver";
	
	AASXFileServer server = getAASXFileServer();
	
	private static final List<String> DUMMY_AAS_IDS = new ArrayList<>(Arrays.asList("AAS_ID_1", "AAS_ID_2"));	
	private static final String DUMMY_FILENAME = "test_file.txt";
	private static final byte[] byteArray = {65, 66, 67, 68, 69};
	private static final InputStream DUMMY_FILE = new ByteArrayInputStream(byteArray);	

	@Test
	public void getAASXByPackageId() throws ParseException, IOException {
		
		PackageDescription packageDescription = createDummyAASXPackage(server);	
		
		String actualConceptDescriptionJSON = requestSpecificConceptDescriptionJSON(DummyConceptDescriptionFactory.createConceptDescription()
				.getId());
		String expectedConceptDescriptionJSON = getSingleConceptDescriptionJSON();

		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionJSON, actualConceptDescriptionJSON);
	}
	
	
	@Test
	public void getSpecificNonExistingPackageId() throws IOException {
		CloseableHttpResponse response = requestAasxPackage("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void updateExistingAASXByPackageId() throws IOException, ParseException {
		String id = ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID;
		String expectedConceptDescriptionJSON = getUpdatedConceptDescriptionJSON();

		CloseableHttpResponse creationResponse = putConceptDescription(id, expectedConceptDescriptionJSON);

		assertEquals(HttpStatus.NO_CONTENT.value(), creationResponse.getCode());

		String actualConceptDescriptionJSON = requestSpecificConceptDescriptionJSON(id);
		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionJSON, actualConceptDescriptionJSON);
	}

	@Test
	public void updateNonExistingAASXByPackageId() throws IOException {
		String id = "nonExisting";
		String expectedConceptDescriptionJSON = getUpdatedConceptDescriptionJSON();

		CloseableHttpResponse updateResponse = putConceptDescription(id, expectedConceptDescriptionJSON);

		assertEquals(HttpStatus.NOT_FOUND.value(), updateResponse.getCode());
	}

	
	@Test
	public void createConceptDescriptionNewId() throws IOException, ParseException {
		String expectedConceptDescriptionJSON = getNewConceptDescriptionJSON();
		CloseableHttpResponse creationResponse = createConceptDescription(expectedConceptDescriptionJSON);

		assertConceptDescriptionCreationReponse(expectedConceptDescriptionJSON, creationResponse);

		String actualConceptDescription = requestSpecificConceptDescriptionJSON("newConceptDescription");
		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionJSON, actualConceptDescription);
	}
	

	@Test
	public void deleteAASXPackageById() throws IOException {
		
		PackageDescription packageDescription = createDummyAASXPackage();
		String existingPackageId = packageDescription.getPackageId();
		
		String existingConceptDescriptionId = DummyConceptDescriptionFactory.createConceptDescription()
				.getId();

		CloseableHttpResponse deletionResponse = deleteConceptDescriptionById(existingPackageId);
		assertEquals(HttpStatus.NO_CONTENT.value(), deletionResponse.getCode());

		CloseableHttpResponse getResponse = requestAasxPackage(existingPackageId);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}
	
	@Test
	public void deleteNonExistingAasxFileServer() throws IOException {
		CloseableHttpResponse deletionResponse = deleteConceptDescriptionById("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), deletionResponse.getCode());
	}
	
	
	
	
	
	protected CloseableHttpResponse createConceptDescription(String conceptDescriptionJSON) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(getURL(), conceptDescriptionJSON);
	}

	protected CloseableHttpResponse deleteConceptDescriptionById(String conceptDescriptionId) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(getURL() + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(conceptDescriptionId));
	}

	private CloseableHttpResponse putConceptDescription(String conceptDescriptionId, String conceptDescriptionJSON) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(BaSyxConceptDescriptionHttpTestUtils.getSpecificConceptDescriptionAccessPath(getURL(), conceptDescriptionId), conceptDescriptionJSON);
	}

	private String requestSpecificConceptDescriptionJSON(String conceptDescriptionId) throws IOException, ParseException {
		CloseableHttpResponse response = requestConceptDescription(conceptDescriptionId);

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private CloseableHttpResponse requestAasxPackage(String packageId) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(getSpecificAasxFileServerAccessPath(getURL(), packageId));
	}

	protected String requestAllConceptDescriptions() throws IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getURL());

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private String getUpdatedConceptDescriptionJSON() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleConceptDescriptionUpdate.json");
	}

	private String getUpdatedConceptDescriptionWithMismatchIdJSON() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleCDUpdateMismatchId.json");
	}

	

}
