package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.service.NodeRefCountService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(API.PATH_WITH_VERSION)
public class NodeRefCountController {

  @Autowired
  private NodeRefCountService nodeRefCountService;

  @GetJsonMapping("/referrers/{nodeId}")
  public Integer getReferrerCount(@PathVariable("nodeId") UUID nodeId) {
    return nodeRefCountService.getReferrerCount(nodeId);
  }

  @GetJsonMapping("/references/{nodeId}")
  public Integer getReferenceCount(@PathVariable("nodeId") UUID nodeId) {
    return nodeRefCountService.getReferenceCount(nodeId);
  }
}
