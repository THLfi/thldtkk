package fi.thl.thldtkk.api.metadata.service;

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
}
