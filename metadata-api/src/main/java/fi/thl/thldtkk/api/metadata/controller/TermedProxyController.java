package fi.thl.thldtkk.api.metadata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping(path = "/termed")
public class TermedProxyController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${termed.graphId}")
    private String rootGraph;

    @RequestMapping(path = "/datasets",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> getDataSets(
            @RequestParam(name = "query", defaultValue = "", required = false) String query,
            @RequestParam(name = "max", defaultValue = "", required = false) String maxResults
    ) {
        String url = "/graphs/"
                + rootGraph
                + "/nodes?query="
                + query
                + "&max="
                + maxResults
                + "&sort=properties.prefLabel.fi.sortable";

        return restTemplate.getForEntity(url, String.class);
    }

    @RequestMapping(path = "/types/{typeId}/nodes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> getTypeNodes(
            @PathVariable String typeId
    ) {
        return getTypeNode(typeId, null);
    }

    @RequestMapping(path = "/types/{typeId}/nodes/{nodeId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> getTypeNode(
            @PathVariable String typeId,
            @PathVariable String nodeId
    ) {
        StringBuilder url = new StringBuilder("/graphs/");
        url.append(rootGraph);
        url.append("/types/");
        url.append(typeId);
        url.append("/nodes");
        if (StringUtils.hasText(nodeId)) {
            url.append("/");
            url.append(nodeId);
        }
        return restTemplate.getForEntity(url.toString(), String.class);
    }
}
