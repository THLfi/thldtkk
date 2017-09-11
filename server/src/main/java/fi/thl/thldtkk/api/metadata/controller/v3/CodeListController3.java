package fi.thl.thldtkk.api.metadata.controller.v3;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.service.v3.CodeListService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/codeLists")
public class CodeListController3 {

  @Autowired
  private CodeListService codeListService;

  @GetJsonMapping("/{codeListId}")
  public CodeList getById(@PathVariable("codeListId") UUID codeListId) {
    return codeListService.get(codeListId).orElseThrow(NotFoundException::new);
  }

  @GetJsonMapping
  public List<CodeList> query(@RequestParam(value = "query", defaultValue = "") String query) {
    return codeListService.find(query, -1);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public CodeList save(@RequestBody @Valid CodeList codeList) {
    return codeListService.save(codeList);
  }

}
