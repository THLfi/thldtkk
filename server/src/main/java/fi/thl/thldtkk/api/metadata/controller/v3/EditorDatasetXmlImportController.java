package fi.thl.thldtkk.api.metadata.controller.v3;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Api(description = "Dataset XML API")
@RestController
@RequestMapping("/api/v3/editor/datasets")
public class EditorDatasetXmlImportController {

  private Logger log = LoggerFactory.getLogger(getClass());

  @PostMapping(value = "/xml-import",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Dataset generateDatasetFromXML(MultipartHttpServletRequest request) {
    Iterator<String> itr = request.getFileNames();
    MultipartFile multipartFile = request.getFile(itr.next());
    try {
      return new XmlMapper().readValue(multipartFile.getBytes(), Dataset.class);
    } catch (IOException e) {
      log.error(e.getMessage());
      return new Dataset();
    }
  }

}
