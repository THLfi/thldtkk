package fi.thl.thldtkk.api.metadata.service;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Population;
import fi.thl.thldtkk.api.metadata.domain.Quantity;
import fi.thl.thldtkk.api.metadata.domain.Unit;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import fi.thl.thldtkk.api.metadata.test.a;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.ImmutableMultimap.of;
import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DatasetServiceTest {

  private DatasetService datasetService;
  private TermedNodeService mockedNodeService;

  @Before
  public void setUp() {
    List<Node> datasets = asList(
        new Node(nameUUIDFromString("DS1"), "DataSet"),
        new Node(nameUUIDFromString("DS2"), "DataSet"));

    this.mockedNodeService = Mockito.mock(TermedNodeService.class);
    when(mockedNodeService.query(eq(new NodeRequest("type.id:DataSet",
      "id,type,properties.*", "properties.prefLabel.sortable", -1))))
        .thenReturn(datasets.stream());
    when(mockedNodeService.query(eq(new NodeRequest("type.id:DataSet AND (properties.prefLabel:hello*)",
      "id,type,properties.*", "properties.prefLabel.sortable", -1))))
        .thenReturn(datasets.stream());
    when(mockedNodeService.get(any(NodeId.class), any())).thenReturn(Optional.<Node>empty());
    when(mockedNodeService.get(eq(new NodeId(nameUUIDFromString("DS1"), "DataSet")), any()))
        .thenReturn(Optional.of(datasets.get(0)));

    this.datasetService = new DatasetService(mockedNodeService);
  }

  @Test
  public void shouldQueryAllDatasets() {
    assertEquals(2, datasetService.query().count());
  }

  @Test
  public void shouldQueryDatasetsByPrefLabel() {
    assertEquals(2, datasetService.query("properties.prefLabel:hello*").count());
  }

  @Test
  public void shouldGetDatasetById() {
    assertTrue(datasetService.get(nameUUIDFromString("DS1")).isPresent());
  }

  @Test
  public void shouldSaveSimpleDataset() {
    Dataset dataset = a.dataset()
      .withIdFromString("DS")
      .withPrefLabel("DS")
      .build();

    datasetService.save(dataset);

    Node datasetNode = a.datasetNode()
      .withIdFromString("DS")
      .withProperty("prefLabel", "fi", "DS")
      .build();
    verify(mockedNodeService).save(singletonList(datasetNode));
  }

  @Test
  public void shouldDeleteDataset() {
    datasetService.delete(nameUUIDFromString("DS1"));
    verify(mockedNodeService)
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

    datasetService.save(ds);

    verify(mockedNodeService).save(asList(
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
    datasetService.delete(nameUUIDFromString("Does not exist"));
  }

  @Test
  public void datasetShouldHaveIdAfterSaving() {
    Dataset savedDataset = datasetService.save(a.dataset().build());

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

    Dataset savedDataset = datasetService.save(dataset);

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

    Dataset savedDataset = datasetService.save(dataset);

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

    datasetService.save(ds1);

    ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
    verify(mockedNodeService).save(captor.capture());
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
    when(mockedNodeService.get(eq(new NodeId(ds1.getId(), "DataSet")), anyString()))
      .thenReturn(Optional.of(existingDatasetNode));

    datasetService.save(ds1);

    ArgumentCaptor<Changeset> captor = ArgumentCaptor.forClass(Changeset.class);
    verify(mockedNodeService).post(captor.capture());
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

}
