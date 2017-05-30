package fi.thl.thldtkk.api.metadata.service;

import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class NodeRequestBuilder {
  private String query;
  private Set<String> includedAttributes = new LinkedHashSet<>();
  private Set<String> sortProperties = new LinkedHashSet<>();
  private int maxResults = -1;

  public NodeRequestBuilder withQuery(String query) {
    this.query = query;
    return this;
  }

  public NodeRequestBuilder addDefaultIncludedAttributes() {
    addIncludedAttribute("id");
    addIncludedAttribute("type");
    addIncludedAttribute("properties.*");
    return this;
  }

  public NodeRequestBuilder addIncludedAttribute(String attribute) {
    includedAttributes.add(attribute);
    return this;
  }

  public NodeRequestBuilder addSort(String property) {
    return addSort(property, null);
  }

  public NodeRequestBuilder addSort(String property, String lang) {
    if (StringUtils.hasText(property)) {
      StringBuilder newSortProperty = new StringBuilder("properties.");
      newSortProperty.append(property);

      if (StringUtils.hasText(lang)) {
        newSortProperty.append(".");
        newSortProperty.append(lang);
      }

      newSortProperty.append(".sortable");

      this.sortProperties.add(newSortProperty.toString());
    }

    return this;
  }

  public NodeRequestBuilder withMaxResults(int maxResults) {
    this.maxResults = maxResults;
    return this;
  }

  public NodeRequest build() {
    if (includedAttributes.isEmpty()) {
      addDefaultIncludedAttributes();
    }

    return new NodeRequest(
      query,
      String.join(",", includedAttributes),
      String.join(",", sortProperties),
      maxResults);
  }
}
