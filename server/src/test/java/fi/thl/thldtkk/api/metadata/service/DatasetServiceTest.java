package fi.thl.thldtkk.api.metadata.service;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.Population;
import fi.thl.thldtkk.api.metadata.domain.Quantity;
import fi.thl.thldtkk.api.metadata.domain.Unit;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.service.termed.EditorDatasetServiceImpl;
import fi.thl.thldtkk.api.metadata.test.a;
import fi.thl.thldtkk.api.metadata.test.an;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.collect.ImmutableMultimap.of;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DatasetServiceTest {

  private EditorDatasetService editorDatasetService;
  
  @Mock
  private Repository<NodeId, Node> mockedNodes;
  
  @Mock
  private UserHelper mockedUserHelper;
  
  @Before
  public void setUp() {
    List<Node> datasets = asList(
        new Node(nameUUIDFromString("DS1"), "DataSet"),
        new Node(nameUUIDFromString("DS2"), "DataSet"));

    MockitoAnnotations.initMocks(this);
    
    when(mockedUserHelper.isCurrentUserAdmin()).thenReturn(true);    
    
    List<Criteria> basicDatasetCriteria = new ArrayList<>();    
    basicDatasetCriteria.add(keyValue("type.id", "DataSet"));
    when(mockedNodes.query(eq(and(basicDatasetCriteria))))
        .thenReturn(datasets.stream());
    
    List<Criteria> labelCriteria = new ArrayList<>(basicDatasetCriteria);
    labelCriteria.add(keyWithAnyValue("properties.prefLabel", tokenizeAndMap("hello", t -> t + "*")));
    when(mockedNodes.query(eq(and(labelCriteria)), eq(-1)))
        .thenReturn(datasets.stream());
    
    when(mockedNodes.get(any(Select.class), any(NodeId.class))).thenReturn(Optional.<Node>empty());
    when(mockedNodes.get(eq(select("id", "type", "properties.*", "references.*",
        "references.inScheme:2",
        "references.conceptsFromScheme:2",
        "references.variable:2",
        "references.quantity:2",
        "references.unit:2",
        "references.codeList:2",
        "references.source:2",
        "references.instanceQuestions:2",
        "references.personInRoles:2",
        "references.person:2",
        "references.role:2")), eq(
        new NodeId(nameUUIDFromString("DS1"), "DataSet"))))
            .thenReturn(Optional.of(datasets.get(0)));;
       
    this.editorDatasetService = new EditorDatasetServiceImpl(mockedNodes, mockedUserHelper);    
  }

  @Test
  public void shouldQueryAllDatasets() {
    assertEquals(2, editorDatasetService.findAll().size());
  }

  @Test
  public void shouldQueryDatasetsByPrefLabel() {
    assertEquals(2, editorDatasetService.find("hello", -1).size());
  }

  @Test
  public void shouldGetDatasetById() {
    assertTrue(editorDatasetService.get(nameUUIDFromString("DS1")).isPresent());
  }

  @Test
  public void shouldSaveSimpleDataset() {
    Dataset dataset = a.dataset()
      .withIdFromString("DS")
      .withPrefLabel("DS")
      .build();

    editorDatasetService.save(dataset);

    Node datasetNode = a.datasetNode()
      .withIdFromString("DS")
      .withProperty("prefLabel", "fi", "DS")
      .build();
    verify(mockedNodes).save(singletonList(datasetNode));
    
  }

  @Test
  public void shouldDeleteDataset() {
    editorDatasetService.delete(nameUUIDFromString("DS1"));
    verify(mockedNodes)
        .delete(singletonList(new NodeId(nameUUIDFromString("DS1"), "DataSet")));
  }

  @Test
  public void shouldSaveDatasetWithInstanceVariables() {
    Dataset ds = new Dataset(nameUUIDFromString("DS"), asList(
        new InstanceVariable(nameUUIDFromString("IV1")),
        new InstanceVariable(nameUUIDFromString("IV2")),
        new InstanceVariable(nameUUIDFromString("IV3"))));

    SetMultimap<String,StrictLangValue> props = LinkedHashMultimap.create(
        ImmutableMultimap.of("published", new StrictLangValue("", "false", "^(true|false)$")));

    editorDatasetService.save(ds);

    verify(mockedNodes).save(asList(
        new Node(nameUUIDFromString("DS"), "DataSet", of(),
            // dataset references
            ImmutableMultimap.of(
                "instanceVariable", new Node(nameUUIDFromString("IV1"), "InstanceVariable", props),
                "instanceVariable", new Node(nameUUIDFromString("IV2"), "InstanceVariable", props),
                "instanceVariable", new Node(nameUUIDFromString("IV3"), "InstanceVariable", props))),
        // actual instance variable nodes
        new Node(nameUUIDFromString("IV1"), "InstanceVariable", props),
        new Node(nameUUIDFromString("IV2"), "InstanceVariable", props),
        new Node(nameUUIDFromString("IV3"), "InstanceVariable", props)));
  }

  @Test(expected = NotFoundException.class)
  public void shouldThrowNotFoundWhenDeletingNonExistingDataset() {
    editorDatasetService.delete(nameUUIDFromString("Does not exist"));
  }

  @Test
  public void datasetShouldHaveIdAfterSaving() {
    Dataset savedDataset = editorDatasetService.save(a.dataset().build());

    assertNotNull(savedDataset.getId());
  }

  @Test
  public void shouldClearQuantityAndUnitAndValueRangeFieldsWhenValueDomainTypeIsNotDescribed() {
    InstanceVariable variable = new InstanceVariable(nameUUIDFromString("IV1"));
    variable.setValueDomainType(InstanceVariable.VALUE_DOMAIN_TYPE_ENUMERATED);
    variable.setQuantity(new Quantity(nameUUIDFromString("Q1")));
    variable.setUnit(new Unit(nameUUIDFromString("U1")));
    variable.setValueRangeMax(new BigDecimal("10"));
    variable.setValueRangeMin(new BigDecimal("11"));
    Dataset dataset = new Dataset(nameUUIDFromString("DS"), asList(variable));

    Dataset savedDataset = editorDatasetService.save(dataset);

    InstanceVariable savedVariable = savedDataset.getInstanceVariables().iterator().next();
    assertFalse(savedVariable.getQuantity().isPresent());
    assertFalse(savedVariable.getUnit().isPresent());
    assertFalse(savedVariable.getValueRangeMax().isPresent());
    assertFalse(savedVariable.getValueRangeMin().isPresent());
  }

  @Test
  public void shouldClearCodeListWhenValueDomainTypeIsNotEnumerated() {
    InstanceVariable variable = new InstanceVariable(nameUUIDFromString("IV1"));
    variable.setValueDomainType(InstanceVariable.VALUE_DOMAIN_TYPE_DESCRIBED);
    variable.setCodeList(new CodeList(nameUUIDFromString("CL1")));
    Dataset dataset = new Dataset(nameUUIDFromString("DS"), asList(variable));

    Dataset savedDataset = editorDatasetService.save(dataset);

    InstanceVariable savedVariable = savedDataset.getInstanceVariables().iterator().next();
    assertFalse(savedVariable.getCodeList().isPresent());
  }

  @Test
  public void populationShouldBeSavedWhenNewDatasetIsSaved() {
    Population p1 = a.population()
      .withPrefLabel("POP1")
      .build();
    Dataset ds1 = a.dataset()
      .withPrefLabel("DS1")
      .withPopulation(p1)
      .build();

    editorDatasetService.save(ds1);

    ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
    verify(mockedNodes).save(captor.capture());
    List<Node> savedNodes = captor.getValue();
    assertThat(savedNodes).hasSize(2);

    Node datasetNode = savedNodes.get(0);
    StrictLangValue datasetNodePrefLabel = datasetNode.getProperties().get("prefLabel").iterator().next();
    assertThat(datasetNodePrefLabel.getValue()).isEqualTo("DS1");

    Node populationNode = savedNodes.get(1);
    assertThat(populationNode.getId()).isNotNull();
    StrictLangValue populationNodePrefLabel = populationNode.getProperties().get("prefLabel").iterator().next();
    assertThat(populationNodePrefLabel.getValue()).isEqualTo("POP1");

    assertThat(datasetNode.getReferences("population")).containsOnly(populationNode);
  }

  @Test
  public void populationShouldBeSavedWhenExistingDatasetIsSaved() {
    Population p1 = a.population()
      .withPrefLabel("POP1")
      .build();
    UUID ds1Id = nameUUIDFromString("DS1");
    Dataset ds1 = a.dataset()
      .withId(ds1Id)
      .withPrefLabel("DS1")
      .withPopulation(p1)
      .build();

    // This has no prior population
    Node existingDatasetNode = a.datasetNode()
      .withId(ds1Id)
      .build();
    when(mockedNodes.get(eq(new NodeId(ds1.getId(), "DataSet"))))
      .thenReturn(Optional.of(existingDatasetNode));

    editorDatasetService.save(ds1);

    ArgumentCaptor<Changeset> captor = ArgumentCaptor.forClass(Changeset.class);
    verify(mockedNodes).post(captor.capture());
    Changeset<UUID, Node> changeset = captor.getValue();
    assertThat(changeset.getSave()).hasSize(2);

    Node datasetNode = changeset.getSave().get(0);
    StrictLangValue datasetNodePrefLabel = datasetNode.getProperties().get("prefLabel").iterator().next();
    assertThat(datasetNodePrefLabel.getValue()).isEqualTo("DS1");

    Node populationNode = changeset.getSave().get(1);
    StrictLangValue populationNodePrefLabel = populationNode.getProperties().get("prefLabel").iterator().next();
    assertThat(populationNodePrefLabel.getValue()).isEqualTo("POP1");

    assertThat(datasetNode.getReferences("population")).containsOnly(populationNode);
  }
  
  @Test
  public void shouldFindDatasetsOnlyFromUserOrganizationWhenNotAdmin() {
    
    when(mockedUserHelper.isCurrentUserAdmin()).thenReturn(false);    
    
    Organization orgA = an.organization()
      .withIdFromString("OrgA")
      .build();
    
    Dataset ds1 = a.dataset()
      .withIdFromString("ds1")
      .withPrefLabel("ds1")
      .withOwner(orgA)
      .build();

    when(mockedUserHelper.getCurrentUserOrganizations()).thenReturn(Arrays.asList(orgA));
    
    List<Criteria> datasetCriteria = new ArrayList<>();    
    datasetCriteria.add(keyValue("type.id", "DataSet"));
    datasetCriteria.add(keyWithAnyValue("references.owner.id", Arrays.asList(orgA.getId().toString())));
    
    when(mockedNodes.query(eq(and(datasetCriteria))))
        .thenReturn(Stream.of(ds1.toNode()));
    
    List<Dataset> actualDatasets = this.editorDatasetService.findAll();
    verify(mockedNodes).query(and(datasetCriteria));    
    assertThat(actualDatasets).hasSize(1);
    
    Organization actualOwner = actualDatasets.get(0).getOwner().get();
    assertThat(actualOwner.getId()).isEqualTo(orgA.getId());
    
  }

  @Test
  public void shouldNotFindAnyDatasetsWhenNotAdminAndNoOwnOrganizationDatasets() {
    when(mockedUserHelper.isCurrentUserAdmin()).thenReturn(false);    
    
    Organization orgA = an.organization()
      .withIdFromString("OrgA")
      .build();
    
    when(mockedUserHelper.getCurrentUserOrganizations()).thenReturn(Arrays.asList(orgA));
    
    List<Criteria> datasetCriteria = new ArrayList<>();    
    datasetCriteria.add(keyValue("type.id", "DataSet"));
    datasetCriteria.add(keyWithAnyValue("references.owner.id", Arrays.asList(orgA.getId().toString())));
    
    when(mockedNodes.query(eq(and(datasetCriteria))))
        .thenReturn(Stream.empty());
    
    List<Dataset> actualDatasets = this.editorDatasetService.findAll();
    verify(mockedNodes).query(and(datasetCriteria));
    assertThat(actualDatasets).hasSize(0);
    
  }
  
}
