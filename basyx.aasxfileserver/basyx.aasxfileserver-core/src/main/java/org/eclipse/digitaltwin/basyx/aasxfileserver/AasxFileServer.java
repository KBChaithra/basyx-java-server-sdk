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

package org.eclipse.digitaltwin.basyx.aasxfileserver;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Specifies the overall AasxFileServer API
 * 
 * @author chaithra
 *
 */
public interface AasxFileServer {

	/**
	 * Retrieves all AASXPackageIds from the repository
	 * 
	 * @return a list of available AASX packages at the server
	 */
	public Collection<PackageDescription> getAllAASXPackageIds();
		
	/**
	 * Retrieves a specific AASX package from the server
	 * 
	 * @param packageId
	 * @return a specific AASX package from the server
	 * @throws ElementDoesNotExistException
	 */
	public InputStream getAASXByPackageId(String packageId) throws ElementDoesNotExistException;

	/**
	 * Updates an existing AASX package at the server
	 * 
	 * @param packageId
	 * @param aasIds
	 * @param filename
	 * @param file 
	 * @throws ElementDoesNotExistException
	 */
	public void updateAASXByPackageId(String packageId, List<String> aasIds, InputStream file, String filename) throws ElementDoesNotExistException;

	/**
	 * Creates a new AASXPackage
	 * 
	 * @param aasIds
	 * @param filename
	 * @param file 
	 * @throws CollidingIdentifierException
	 */
	public PackageDescription createAASXPackage(List<String> aasIds, InputStream file, String fileName) throws CollidingIdentifierException;

	/**
	 * Deletes a AASXPackage	 
	 * @param packageId
	 * @throws ElementDoesNotExistException
	 */
	public void deleteAASXPackageById(String packageId) throws ElementDoesNotExistException;
	
}
