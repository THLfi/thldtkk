package fi.thl.thldtkk.api.metadata.controller;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/virtu")
public class VirtuFunctionsController {

  @Autowired
  private Environment environment;

  @Value(value = "${virtu.idpDiscoveryServiceUrl}")
  private String idpDiscoveryServiceUrl;

  @Value("${virtu.entityId}")
  private String entityId;

  @Value("${virtu.entityBaseUrl}")
  private String entityBaseUrl;

  @RequestMapping(value = "/idpDirectoryServiceUrl", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public String getVirtuIdpDirectoryServiceUrl() {
    if (ArrayUtils.contains(environment.getActiveProfiles(), "virtu")) {
      return new StringBuilder()
        .append(idpDiscoveryServiceUrl)
        .append("?entityID=")
        .append(entityId)
        .append("&return=")
        .append(entityBaseUrl)
        .append("/saml/login&returnIDParam=idp")
        .toString();
    }
    else {
      return "";
    }
  }

}
