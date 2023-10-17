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

package org.eclipse.digitaltwin.basyx.aasxfileserver.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.PackageDescription;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Test;

/**
 * Testsuite for implementations of the {@link AASXFileServer} interface
 * 
 * @author chaithra
 *
 */
public abstract class AASXFileServerSuite {

	protected abstract AASXFileServer getAASXFileServer();

	@Test
	public void getAllAASXPackageIds() {

		AASXFileServer server = getAASXFileServer();
		Collection<PackageDescription> packageDescriptions = DummyAASXFileServerFactory.getAllDummyAASXPackages(server);

		assertGetAllAASXPackageIds(packageDescriptions);
	}

	@Test
	public void getAllAASXPackageIdsEmpty() {

		AASXFileServer server = getAASXFileServer();
		Collection<PackageDescription> packageDescriptions = server.getAllAASXPackageIds();

		assertTrue(packageDescriptions.isEmpty());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSpecificNonExistingPackageId() {

		AASXFileServer server = getAASXFileServer();
		server.getAASXByPackageId("doesNotExist");
	}

	@Test
	public void updateExistingAASXByPackageId() {

		AASXFileServer server = getAASXFileServer();

		PackageDescription expectedPackageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackage(server);

		updateAasxPackage(server, expectedPackageDescription.getPackageId(), DummyAASXFileServerFactory.SECOND_AAS_IDS,
				DummyAASXFileServerFactory.SECOND_FILE, DummyAASXFileServerFactory.SECOND_FILENAME);

		Collection<PackageDescription> actualPackageDescription = server.getAllAASXPackageIds();

		assertUpdatedAASXPackageId(expectedPackageDescription, actualPackageDescription, server);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAASXByPackageId() {

		String packageId = "notExisting";
		
		AASXFileServer server = getAASXFileServer();

		server.updateAASXByPackageId(packageId, DummyAASXFileServerFactory.FIRST_AAS_IDS, DummyAASXFileServerFactory.FIRST_FILE, DummyAASXFileServerFactory.FIRST_FILENAME);
	}

	@Test
	public void getAASXByPackageId() throws ElementDoesNotExistException, IOException {

		AASXFileServer server = getAASXFileServer();

		PackageDescription packageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackage(server);

		InputStream actualValue = server.getAASXByPackageId(packageDescription.getPackageId());
		InputStream expectedValue = DummyAASXFileServerFactory.FIRST_FILE;

		assertTrue(IOUtils.contentEquals(expectedValue, actualValue));
	}

	@Test
	public void deleteAASXByPackageId() {

		AASXFileServer server = getAASXFileServer();

		PackageDescription packageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackage(server);

		server.deleteAASXByPackageId(packageDescription.getPackageId());

		try {
			server.getAASXByPackageId(packageDescription.getPackageId());
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingAasxFileServer() {

		AASXFileServer server = getAASXFileServer();
		server.deleteAASXByPackageId("nonExisting");
	}

	private void updateAasxPackage(AASXFileServer server, String packageId, List<String> expectedAasIds,
			InputStream secondFile, String secondFilename) {

		server.updateAASXByPackageId(packageId, expectedAasIds, DummyAASXFileServerFactory.SECOND_FILE, DummyAASXFileServerFactory.SECOND_FILENAME);
	}

	private void assertGetAllAASXPackageIds(Collection<PackageDescription> packageDescriptions) {
		assertEquals(2, packageDescriptions.size());

		Iterator<PackageDescription> iterator = packageDescriptions.iterator();

		PackageDescription expectedFirstPackage = iterator.next();
		PackageDescription expectedSecondPackage = iterator.next();

		assertTrue(packageDescriptions.containsAll(Arrays.asList(expectedFirstPackage, expectedSecondPackage)));
	}

	private void assertUpdatedAASXPackageId(PackageDescription expectedPackageDescription,
			Collection<PackageDescription> actualPackageDescriptions, AASXFileServer server) {
		
		assertEquals(1, actualPackageDescriptions.size());
		assertTrue(actualPackageDescriptions.contains(expectedPackageDescription));

		InputStream actualAASXFile = server.getAASXByPackageId("1");
		InputStream expectedAASXFile = DummyAASXFileServerFactory.SECOND_FILE;

		assertEquals(expectedAASXFile, actualAASXFile);
	}

}
