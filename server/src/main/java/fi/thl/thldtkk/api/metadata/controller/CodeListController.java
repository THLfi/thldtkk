package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/api/v2/codeLists")
public class CodeListController {

  @Autowired
  private Service<UUID, CodeList> codeListService;

  @GetJsonMapping("/{codeListId}")
  public CodeList getById(@PathVariable("codeListId") UUID codeListId) {
    return codeListService.get(codeListId)
      .orElseThrow(NotFoundException::new);
  }

  @GetJsonMapping
  public List<CodeList> query(
    @RequestParam(value = "query", required = false, defaultValue = "") String query) {
    return codeListService.query(query).collect(toList());
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public CodeList save(@RequestBody @Valid CodeList codeList) {
    return codeListService.save(codeList);
  }

}
