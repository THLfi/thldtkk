package fi.thl.thldtkk.api.metadata;

import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.util.ResourceUtils.resourceToString;
import static fi.thl.thldtkk.api.metadata.util.json.GsonJsonElementFactory.array;
import static fi.thl.thldtkk.api.metadata.util.json.GsonJsonElementFactory.concat;
import static fi.thl.thldtkk.api.metadata.util.json.GsonJsonElementFactory.object;
import static fi.thl.thldtkk.api.metadata.util.json.GsonJsonElementFactory.primitive;
import static org.springframework.http.HttpMethod.POST;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;

import java.util.*;
import java.util.stream.Collectors;

import fi.thl.thldtkk.api.metadata.domain.*;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TermedInitializer implements ApplicationListener<ContextRefreshedEvent> {

  private Logger log = LoggerFactory.getLogger(getClass());

  @Value("${termed.apiUrl}")
  private String apiUrl;
  @Value("${termed.username}")
  private String username;
  @Value("${termed.password}")
  private String password;

  @Value("${termed.commonGraphId}")
  private UUID commonGraphId;
  @Value("${termed.editorGraphId}")
  private UUID editorGraphId;
  @Value("${termed.publicGraphId}")
  private UUID publicGraphId;

  @Autowired
  private Repository<NodeId, Node> editorNodeRepository;

  @Autowired
  private Repository<NodeId, Node> publicNodeRepository;

  @Autowired
  private Gson gson;

  private JsonParser jsonParser = new JsonParser();

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    initializeTermed();
  }

  private void initializeTermed() {
    log.info("Initializing Termed");

    JsonObject commonGraph = object("id", primitive(commonGraphId.toString()), "properties",
        object("prefLabel", array(object("value", primitive("Metadata API - Common Graph")))));
    JsonArray commonTypes = jsonParser.parse(
        resourceToString("termed/common-types.json")).getAsJsonArray();
    commonTypes.forEach(e -> e.getAsJsonObject().add("graph", commonGraph));
    JsonArray commonNodes = jsonParser.parse(
        resourceToString("termed/common-nodes.json")).getAsJsonArray();
    commonNodes.forEach(e -> e.getAsJsonObject().getAsJsonObject("type").add("graph", commonGraph));

    String editorTypesJson = resolveReferenceAttributeRangeGraphId(
        resourceToString("termed/editor-types.json"));
    JsonObject editorGraph = object("id", primitive(editorGraphId.toString()), "properties",
        object("prefLabel", array(object("value", primitive("Metadata API - Editor Graph")))));
    JsonArray editorTypes = jsonParser.parse(editorTypesJson).getAsJsonArray();
    editorTypes.forEach(e -> e.getAsJsonObject().add("graph", editorGraph));
    JsonArray editorNodes = jsonParser.parse(resourceToString("termed/editor-nodes.json"))
        .getAsJsonArray();
    editorNodes.forEach(e -> e.getAsJsonObject().getAsJsonObject("type").add("graph", editorGraph));

    // use editor types as basis for public types, just put them to a different graph
    JsonArray publicTypes = jsonParser.parse(editorTypesJson).getAsJsonArray();
    JsonObject publicGraph = object("id", primitive(publicGraphId.toString()), "properties",
        object("prefLabel", array(object("value", primitive("Metadata API - Public Graph")))));
    publicTypes.forEach(e -> e.getAsJsonObject().add("graph", publicGraph));

    JsonObject dump = object(
        "graphs", array(commonGraph, editorGraph, publicGraph),
        "types", concat(commonTypes, editorTypes, publicTypes),
        "nodes", concat(commonNodes, editorNodes));

    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    gsonHttpMessageConverter.setGson(gson);

    RestTemplate termedRestTemplate = new RestTemplateBuilder()
        .rootUri(apiUrl)
        .messageConverters(gsonHttpMessageConverter)
        .basicAuthorization(username, password)
        .build();

    termedRestTemplate.exchange("/restore", POST, new HttpEntity<>(dump), JsonObject.class);


    // Data migrations are currently handled here
    runMigrations();
  }

  // convert each ref attr range graph code to an actual UUID
  private String resolveReferenceAttributeRangeGraphId(String json) {
    return JsonPath.parse(json).set(
        "$[*].referenceAttributes[*].range.graph[?(@.code == 'common-graph')]",
        ImmutableMap.of("id", commonGraphId.toString())).jsonString();
  }

  private void runMigrations() {
    this.migrateOldExistenceFormsToStudyForms(this.editorNodeRepository);
    this.migrateOldExistenceFormsToStudyForms(this.publicNodeRepository);
  }

  private void migrateOldExistenceFormsToStudyForms(Repository<NodeId, Node> repository) {
    List<Study> studies = repository.query(
      select(
        "id",
        "type",
        "properties.*",
        "references.*"
      ),
      keyValue("type.id", "Study")
    )
      .map(Study::new)
      .filter(study -> study.getExistenceForms().size() > 0)
      .collect(Collectors.toList());

    for (Study study : studies) {
      List<StudyForm> forms = study.getExistenceForms().stream()
        .map(exForm -> {
          StudyForm form = new StudyForm(UUID.randomUUID());

          switch (exForm) {
            case PAPER:
              form.setType(StudyFormType.PAPER);
              break;
            case DIGITAL:
              form.setType(StudyFormType.DIGITAL);
              break;
            case SAMPLE:
              form.setType(StudyFormType.SAMPLE);
              break;
          }

          form.setTypeSpecifier(StudyFormTypeSpecifier.NONE);

          return form;
        })
        .collect(Collectors.toList());

      study.setExistenceForms(Collections.emptyList());

      List<StudyForm> allForms = study.getStudyForms().stream()
        .peek(form -> form.setTypeSpecifier(StudyFormTypeSpecifier.NONE))
        .collect(Collectors.toList());

      study.setStudyForms(allForms);

      List<Node> saves = forms.stream()
        .map(StudyForm::toNode)
        .collect(Collectors.toList());

      saves.add(study.toNode());

      this.editorNodeRepository.save(saves);
    }
  }
}
