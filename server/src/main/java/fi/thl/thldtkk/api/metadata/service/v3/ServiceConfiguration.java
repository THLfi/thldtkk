package fi.thl.thldtkk.api.metadata.service.v3;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.termed.PublicDatasetServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.CodeListServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.ConceptServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.DatasetPublishingServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.EditorDatasetServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.DatasetTypeServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.InstanceQuestionServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.InstanceVariableServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.LifecyclePhaseServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.NodeHttpRepository;
import fi.thl.thldtkk.api.metadata.service.v3.termed.OrganizationServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.OrganizationUnitServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.PersonServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.QuantityServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.RoleServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.UnitServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.UnitTypeServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.UniverseServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.UsageConditionServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.UserProfileServiceImpl;
import fi.thl.thldtkk.api.metadata.service.v3.termed.VariableServiceImpl;
import java.util.UUID;
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

  // public services

  @Bean
  public InstanceVariableService publicInstanceVariableService() {
    return new InstanceVariableServiceImpl(publicNodeRepository());
  }

  @Bean
  public PublicDatasetService publicDatasetService() {
    return new PublicDatasetServiceImpl(publicNodeRepository());
  }

  // editor services

  @Bean
  public InstanceVariableService editorInstanceVariableService() {
    return new InstanceVariableServiceImpl(editorNodeRepository());
  }

  @Bean
  public EditorDatasetService editorDatasetService() {
    return new EditorDatasetServiceImpl(editorNodeRepository());
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

  // Work in progress, remove '_3' suffix from bean names after refactoring is complete

  @Bean
  public CodeListService codeListService_3() {
    return new CodeListServiceImpl(commonNodeRepository());
  }

  @Bean
  public ConceptService conceptService_3() {
    return new ConceptServiceImpl(commonNodeRepository());
  }

  @Bean
  public DatasetTypeService datasetTypeService_3() {
    return new DatasetTypeServiceImpl(commonNodeRepository());
  }

  @Bean
  public InstanceQuestionService instanceQuestionService_3() {
    return new InstanceQuestionServiceImpl(
      editorDatasetService(),
      editorNodeRepository()
    );
  }

  @Bean
  public LifecyclePhaseService lifecyclePhaseService_3() {
    return new LifecyclePhaseServiceImpl(commonNodeRepository());
  }

  @Bean
  public OrganizationService organizationService_3() {
    return new OrganizationServiceImpl(commonNodeRepository());
  }

  @Bean
  public OrganizationUnitService organizationUnitService_3() {
    return new OrganizationUnitServiceImpl(commonNodeRepository());
  }

  @Bean
  public PersonService personService_3() {
    return new PersonServiceImpl(commonNodeRepository());
  }

  @Bean
  public QuantityService quantityService_3() {
    return new QuantityServiceImpl(commonNodeRepository());
  }

  @Bean
  public RoleService roleService_3() {
    return new RoleServiceImpl(commonNodeRepository());
  }

  @Bean
  public UnitService unitService_3() {
    return new UnitServiceImpl(commonNodeRepository());
  }

  @Bean
  public UnitTypeService unitTypeService_3() {
    return new UnitTypeServiceImpl(commonNodeRepository());
  }

  @Bean
  public UniverseService universeService_3() {
    return new UniverseServiceImpl(commonNodeRepository());
  }

  @Bean
  public UsageConditionService usageConditionService_3() {
    return new UsageConditionServiceImpl(commonNodeRepository());
  }

  @Bean
  public VariableService variableService_3() {
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
