package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.StudyPublishingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Api(description = "Editor operations API for studys")
@RestController
@RequestMapping("/api/v3/editor/study-functions")
public class EditorStudyFunctionsController {

  @Autowired
  private StudyPublishingService studyPublishingService;

  @ApiOperation("Publish given study")
  @PostMapping("/publish")
  public Study publishStudy(@RequestParam("studyId") UUID studyId) {
    return studyPublishingService.publish(studyId);
  }

  @ApiOperation("Withdraw (un-publish) given study")
  @PostMapping("/withdraw")
  public Study withdrawStudy(@RequestParam("studyId") UUID studyId) {
    return studyPublishingService.withdraw(studyId);
  }

}
