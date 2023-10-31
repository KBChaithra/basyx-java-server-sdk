package org.eclipse.digitaltwin.basyx.aasxfileserver.http.pagination;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

/**
 * GetPackageDescriptionsResult
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-06-15T09:20:20.691035144Z[GMT]")


public class GetPackageDescriptionsResult extends PagedResult  {
  @JsonProperty("result")
  @Valid
  private List<PackageDescription> result = null;

  public GetPackageDescriptionsResult result(List<PackageDescription> result) {
    this.result = result;
    return this;
  }

  public GetPackageDescriptionsResult addResultItem(PackageDescription resultItem) {
    if (this.result == null) {
      this.result = new ArrayList<PackageDescription>();
    }
    this.result.add(resultItem);
    return this;
  }

  /**
   * Get result
   * @return result
   **/
  @Schema(description = "")
      @Valid
    public List<PackageDescription> getResult() {
    return result;
  }

  public void setResult(List<PackageDescription> result) {
    this.result = result;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetPackageDescriptionsResult getPackageDescriptionsResult = (GetPackageDescriptionsResult) o;
    return Objects.equals(this.result, getPackageDescriptionsResult.result) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetPackageDescriptionsResult {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
