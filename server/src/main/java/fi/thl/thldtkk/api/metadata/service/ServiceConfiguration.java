package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.service.termed.EditorInstanceVariableServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.PublicDatasetServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.CodeListServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.ConceptServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.DatasetPublishingServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.EditorDatasetServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.DatasetTypeServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.InstanceQuestionServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.PublicInstanceVariableServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.LifecyclePhaseServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.NodeHttpRepository;
import fi.thl.thldtkk.api.metadata.service.termed.OrganizationServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.OrganizationUnitServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.PersonServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.QuantityServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.RoleServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.UnitServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.UnitTypeServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.UniverseServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.UsageConditionServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.UserProfileServiceImpl;
import fi.thl.thldtkk.api.metadata.service.termed.VariableServiceImpl;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

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
  private UserHelper userHelper;

  // public services

  @Bean
  public PublicInstanceVariableService publicInstanceVariableService() {
    return new PublicInstanceVariableServiceImpl(publicNodeRepository(), publicDatasetService());
  }

  @Bean
  public PublicDatasetService publicDatasetService() {
    return new PublicDatasetServiceImpl(publicNodeRepository());
  }

  // editor services

  @Bean
  public EditorInstanceVariableService editorInstanceVariableService() {
    return new EditorInstanceVariableServiceImpl(editorNodeRepository(), editorDatasetService());
  }

  @Bean
  public EditorDatasetService editorDatasetService() {
    return new EditorDatasetServiceImpl(editorNodeRepository(), userHelper);
  }

  @Bean
  public DatasetPublishingService datasetPublishingService() {
    return new DatasetPublishingServiceImpl(editorDatasetService(), publicDatasetService());
  }

  @Bean
  public UserProfileService editorUserProfileService() {
    return new UserProfileServiceImpl(editorNodeRepository());
  }

  // common services

  // Work in progress, remove '' suffix from bean names after refactoring is complete

  @Bean
  public CodeListService codeListService() {
    return new CodeListServiceImpl(commonNodeRepository());
  }

  @Bean
  public ConceptService conceptService() {
    return new ConceptServiceImpl(commonNodeRepository());
  }

  @Bean
  public DatasetTypeService datasetTypeService() {
    return new DatasetTypeServiceImpl(commonNodeRepository());
  }

  @Bean
  public InstanceQuestionService instanceQuestionService() {
    return new InstanceQuestionServiceImpl(
      editorDatasetService(),
      editorNodeRepository()
    );
  }

  @Bean
  public LifecyclePhaseService lifecyclePhaseService() {
    return new LifecyclePhaseServiceImpl(commonNodeRepository());
  }

  @Bean
  public OrganizationService organizationService() {
    return new OrganizationServiceImpl(commonNodeRepository());
  }

  @Bean
  public OrganizationUnitService organizationUnitService() {
    return new OrganizationUnitServiceImpl(commonNodeRepository());
  }

  @Bean
  public PersonService personService() {
    return new PersonServiceImpl(commonNodeRepository());
  }

  @Bean
  public QuantityService quantityService() {
    return new QuantityServiceImpl(commonNodeRepository());
  }

  @Bean
  public RoleService roleService() {
    return new RoleServiceImpl(commonNodeRepository());
  }

  @Bean
  public UnitService unitService() {
    return new UnitServiceImpl(commonNodeRepository());
  }

  @Bean
  public UnitTypeService unitTypeService() {
    return new UnitTypeServiceImpl(commonNodeRepository());
  }

  @Bean
  public UniverseService universeService() {
    return new UniverseServiceImpl(commonNodeRepository());
  }

  @Bean
  public UsageConditionService usageConditionService() {
    return new UsageConditionServiceImpl(commonNodeRepository());
  }

  @Bean
  public VariableService variableService() {
    return new VariableServiceImpl(commonNodeRepository());
  }

  // lower level termed HTTP APIs

  @Bean
  public Repository<NodeId, Node> editorNodeRepository() {
    return new NodeHttpRepository(apiUrl, editorGraphId, username, password);
  }

  @Bean
  public Repository<NodeId, Node> publicNodeRepository() {
    return new NodeHttpRepository(apiUrl, publicGraphId, username, password);
  }

  @Bean
  public Repository<NodeId, Node> commonNodeRepository() {
    return new NodeHttpRepository(apiUrl, commonGraphId, username, password);
  }

}
