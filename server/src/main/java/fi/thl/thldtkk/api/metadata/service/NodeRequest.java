package fi.thl.thldtkk.api.metadata.service;

import java.util.Objects;

public class NodeRequest {

  private String query;
  private String includedAttributes;
  private String sort;
  private int maxResults;

  public NodeRequest(String query, String includedAttributes, String sort, int maxResults) {
    this.query = query;
    this.includedAttributes = includedAttributes;
    this.sort = sort;
    this.maxResults = maxResults;
  }

  public String getQuery() {
    return query;
  }

  public String getIncludedAttributes() {
    return includedAttributes;
  }

  public String getSort() {
    return sort;
  }

  public int getMaxResults() {
    return maxResults;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NodeRequest that = (NodeRequest) o;
    return Objects.equals(query, that.query)
      && Objects.equals(includedAttributes, that.includedAttributes)
      && Objects.equals(sort, that.sort)
      && Objects.equals(maxResults, that.maxResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(query, includedAttributes, sort, maxResults);
  }

}
