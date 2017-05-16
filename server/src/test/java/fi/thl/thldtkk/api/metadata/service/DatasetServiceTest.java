package fi.thl.thldtkk.api.metadata.service;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DatasetServiceTest {

  private DatasetService datasetService;
  private Service<NodeId, Node> mockedNodeService;

  @Before
  public void setUp() {
    List<Node> datasets = Arrays.asList(
      new Node(nameUUIDFromString("DS1"), "DataSet"),
      new Node(nameUUIDFromString("DS2"), "DataSet"));

    this.mockedNodeService = Mockito.mock(Service.class);
    when(mockedNodeService.query("type.id:DataSet")).thenReturn(datasets.stream());
    when(mockedNodeService.query("type.id:DataSet AND properties.prefLabel:hello*"))
      .thenReturn(datasets.stream());
    when(mockedNodeService.get(any(NodeId.class))).thenReturn(Optional.<Node>empty());
    when(mockedNodeService.get(new NodeId(nameUUIDFromString("DS1"), "DataSet")))
      .thenReturn(Optional.of(datasets.get(0)));

    this.datasetService = new DatasetService(mockedNodeService);
  }

  @Test
  public void shouldQueryAllDatasets() {
    assertEquals(2, datasetService.query().count());
  }

  @Test
  public void shouldQueryDatasetsByPrefLabel() {
    assertEquals(2, datasetService.query("hello").count());
  }

  @Test
  public void shouldGetDatasetById() {
    assertTrue(datasetService.get(nameUUIDFromString("DS1")).isPresent());
  }

  @Test
  public void shouldSaveSimpleDataset() {
    datasetService.save(new Dataset(nameUUIDFromString("DS")));
    verify(mockedNodeService).save(singletonList(new Node(nameUUIDFromString("DS"), "DataSet")));
  }

  @Test
  public void shouldDeleteDataset() {
    datasetService.delete(nameUUIDFromString("DS1"));
    verify(mockedNodeService)
      .delete(singletonList(new NodeId(nameUUIDFromString("DS1"), "DataSet")));
  }

  @Test(expected = NotFoundException.class)
  public void shouldThrowNotFoundWhenDeletingNonExistingDataset() {
    datasetService.delete(nameUUIDFromString("Does not exist"));
  }

}
