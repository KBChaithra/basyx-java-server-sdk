package org.eclipse.digitaltwin.basyx.aasxfileserver.http;

import io.swagger.api.PackagesApi;
import io.swagger.model.GetPackageDescriptionsResult;
import io.swagger.model.PackageDescription;
import org.springframework.core.io.Resource;
import io.swagger.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackagesBody;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescription;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-06-15T09:20:20.691035144Z[GMT]")
@RestController
public class AasxFileServerApiHTTPController implements AasxFileServerHTTPApi {

	private static final Logger log = LoggerFactory.getLogger(AasxFileServerApiHTTPController.class);

	private final ObjectMapper objectMapper;

	private Map<String, Package> packageMap = new LinkedHashMap<>();
	private AtomicInteger packageId = new AtomicInteger(0);

	private final HttpServletRequest request;
	private AASXFileServer server;

	@org.springframework.beans.factory.annotation.Autowired
	public AasxFileServerApiHTTPController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	public ResponseEntity<Void> deleteAASXByPackageId(
			@Parameter(in = ParameterIn.PATH, description = "The AASX File Server's unique packageId (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("packageId") Base64UrlEncodedIdentifier packageId) {
		server.deleteAASXByPackageId(packageId.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<InputStream> getAASXByPackageId(
			@Parameter(in = ParameterIn.PATH, description = "The AASX File Server's unique packageId (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("packageId") Base64UrlEncodedIdentifier packageId) {
		return new ResponseEntity<InputStream>(server.getAASXByPackageId(packageId.getIdentifier()), HttpStatus.OK);
	}

	public ResponseEntity<PackageDescription> getAllAASXPackageIds(
			@Parameter(in = ParameterIn.QUERY, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", schema = @Schema()) @Valid @RequestParam(value = "aasId", required = false) byte[] aasId,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor) {
		
		 return packageMap.values()
		            .stream()
		            .map(Package::getPackageDescription)
		            .collect(Collectors.toList());
		
	}	

	public ResponseEntity<PackageDescription> postAASXPackage(
			@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @RequestParam(value = "aasIds", required = true) List<String> aasIds,
			@Parameter(description = "file detail") @Valid @RequestPart("file") MultipartFile file,
			@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @RequestParam(value = "fileName", required = true) String fileName) {
		try {
			
			String newpackageId = String.valueOf(packageId.incrementAndGet());
			
			PackageDescription packageDescription = createPackageDescription(aasIds, newpackageId);	
			
			createPackage(aasIds, file, fileName, newpackageId, packageDescription);			

			return new ResponseEntity<PackageDescription>(packageDescription, HttpStatus.CREATED);
		} catch (CollidingIdentifierException e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Void> putAASXByPackageId(
			@Parameter(in = ParameterIn.PATH, description = "The AASX File Server's unique packageId (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("packageId") Base64UrlEncodedIdentifier packageId,
			@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @RequestParam(value = "aasIds", required = true) List<String> aasIds,
			@Parameter(description = "file detail") @Valid @RequestPart("file") MultipartFile file,
			@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @RequestParam(value = "fileName", required = true) String fileName) {
		try {
			Package aasxPackage = this.packageMap.get(packageId);
			
			updatePackagesBody(aasIds, file, fileName, aasxPackage.getPackagesBody());
			
			aasxPackage.getPackageDescription().setAasIds(aasIds);			
		
		} catch (ElementDoesNotExistException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}	
	
	private PackageDescription createPackageDescription(List<String> aasIds, String newPackageId) {		
		PackageDescription packageDescription = new PackageDescription();
		packageDescription.packageId(newPackageId);
		packageDescription.aasIds(aasIds);
		
		return packageDescription;
	}
	
	private void createPackage(List<String> aasIds, InputStream file, String fileName, String newPackageId, PackageDescription packageDescription) {
		PackagesBody packagesBody = createPackagesBody(aasIds, file, fileName);
		
		Package aasxPackage = new Package(newPackageId, packageDescription, packagesBody);	
		
		packageMap.put(newPackageId, aasxPackage);
	}
	
	private PackagesBody createPackagesBody(List<String> aasIds, InputStream file, String fileName) {		
		PackagesBody packagesBody = new PackagesBody();
		packagesBody.aasIds(aasIds);
		packagesBody.file(file);
		packagesBody.fileName(fileName);
		
		return packagesBody;
	}	

	private void updatePackagesBody(List<String> aasIds, InputStream file, String filename, PackagesBody packagesBody) {
		packagesBody.setAasIds(aasIds);
		packagesBody.setFileName(filename);
		packagesBody.setFile(file);
	}

		
	
}
