package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.service.termed.EditorStudyServiceImpl;
import fi.thl.thldtkk.api.metadata.test.a;
import fi.thl.thldtkk.api.metadata.test.an;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EditorStudyServiceImplTest {

  EditorStudyService service;

  @Mock
  Repository<NodeId, Node> nodes;
  @Mock
  UserHelper userHelper;
  @Mock
  EditorDatasetService datasetService;
  @Captor
  ArgumentCaptor<Changeset<NodeId, Node>> changesetArgumentCaptor;

  @Before
  public void initServiceAndMocks() {
    initMocks(this);

    when(nodes.get(any(Select.class), any(NodeId.class))).thenReturn(Optional.empty());
    when(datasetService.get(any(UUID.class))).thenReturn(Optional.empty());
    when(userHelper.isCurrentUserAdmin()).thenReturn(true);

    service = new EditorStudyServiceImpl(nodes, userHelper, datasetService);
  }

  @Test(expected = IllegalArgumentException.class)
  public void studyAsItsOwnPredecessorShouldPreventSaving() {
    Study one = a.study()
      .withIdFromString("S1")
      .build();
    Study two = a.study()
      .withIdFromString("S1")
      .withPredecessors(one)
      .build();

    service.save(two);
  }

  @Test
  public void newStudyWithDatasetAndInstanceVariableShouldBeSaved() {
    InstanceVariable instanceVariable = an.instanceVariable()
      .build();
    Dataset dataset = a.dataset()
      .withInstanceVariables(instanceVariable)
      .build();
    Study study = a.study()
      .withDatasets(dataset)
      .build();

    service.save(study);

    verify(nodes).post(changesetArgumentCaptor.capture());

    Changeset<NodeId, Node> changeset = changesetArgumentCaptor.getValue();
    assertThat(changeset.getDelete()).isEmpty();
    assertThat(changeset.getSave()).hasSize(3);

    Node studyNode = changeset.getSave().get(0);
    assertThat(studyNode.getTypeId()).isEqualTo(Study.TERMED_NODE_CLASS);
    Node datasetNode = changeset.getSave().get(1);
    assertThat(datasetNode.getTypeId()).isEqualTo(Dataset.TERMED_NODE_CLASS);
    Node instanceVariableNode = changeset.getSave().get(2);
    assertThat(instanceVariableNode.getTypeId()).isEqualTo(InstanceVariable.TERMED_NODE_CLASS);

    assertThat(studyNode.getReferencesFirst("dataSets").get().getId())
      .isEqualTo(datasetNode.getId());
    assertThat(datasetNode.getReferencesFirst("instanceVariable").get().getId())
      .isEqualTo(instanceVariableNode.getId());
  }

  @Test
  public void existingStudysNewDatasetsAndIntanceVariablesShouldBeSaved() {
    InstanceVariable instanceVariable = an.instanceVariable()
      .withIdFromString("IV1")
      .build();
    Dataset dataset = a.dataset()
      .withIdFromString("D1")
      .withInstanceVariables(instanceVariable)
      .build();
    Study study = a.study()
      .withIdFromString("S1")
      .withDatasets(dataset)
      .build();

    Node existingStudyNode = a.studyNode()
      .withIdFromString("S1")
      .build();

    when(nodes.get(
      any(Select.class),
      eq(a.studyNodeId().withId(study.getId()).build())))
      .thenReturn(Optional.of(existingStudyNode));

    service.save(study);

    verify(nodes).post(changesetArgumentCaptor.capture());

    Changeset<NodeId, Node> changeset = changesetArgumentCaptor.getValue();
    assertThat(changeset.getDelete()).isEmpty();
    assertThat(changeset.getSave()).hasSize(3);

    Node studyNode = changeset.getSave().get(0);
    Node datasetNode = changeset.getSave().get(1);
    assertThat(studyNode.getReferencesFirst("dataSets").get().getId())
      .isEqualTo(datasetNode.getId());
    Node instanceVariableNode = changeset.getSave().get(2);
    assertThat(datasetNode.getReferencesFirst("instanceVariable").get().getId())
      .isEqualTo(instanceVariableNode.getId());
  }

  @Test
  public void datasetAndItsInstanceVariablesShouldBeDeleted() {
    Study study = a.study()
      .withIdFromString("S1")
      .build();

    Node existingInstanceVariableNode = an.instanceVariableNode()
      .withIdFromString("IV1")
      .build();
    Node existingDatasetNode = a.datasetNode()
      .withIdFromString("D1")
      .withReference("instanceVariable", existingInstanceVariableNode)
      .build();
    Node existingStudyNode = a.studyNode()
      .withIdFromString("S1")
      .withReference("dataSets", existingDatasetNode)
      .build();
    when(nodes.get(
      any(Select.class),
      eq(a.studyNodeId().withId(study.getId()).build())))
      .thenReturn(Optional.of(existingStudyNode));

    service.save(study);

    verify(nodes).post(changesetArgumentCaptor.capture());

    Changeset<NodeId, Node> changeset = changesetArgumentCaptor.getValue();
    assertThat(changeset.getDelete()).hasSize(2);

    assertThat(changeset.getDelete().get(0))
      .isEqualTo(a.datasetNodeId().withIdFromString("D1").build());
    assertThat(changeset.getDelete().get(1))
      .isEqualTo(an.instanceVariableNodeId().withIdFromString("IV1").build());
  }

  @Test
  public void intanceVariableShouldBeDeleted() {
    Dataset dataset = a.dataset()
      .withIdFromString("D1")
      .build();
    Study study = a.study()
      .withIdFromString("S1")
      .withDatasets(dataset)
      .build();

    Node existingDatasetNode = a.datasetNode()
      .withIdFromString("D1")
      .build();
    Node existingStudyNode = a.studyNode()
      .withIdFromString("S1")
      .withReference("dataSets", existingDatasetNode)
      .build();
    when(nodes.get(
      any(Select.class),
      eq(a.studyNodeId().withId(study.getId()).build())))
      .thenReturn(Optional.of(existingStudyNode));

    InstanceVariable existingInstanceVariable = an.instanceVariable()
      .withIdFromString("IV1")
      .build();
    Dataset existingDataset = a.dataset()
      .withIdFromString("D1")
      .withInstanceVariables(existingInstanceVariable)
      .build();
    when(datasetService.get(eq(existingDataset.getId())))
      .thenReturn(Optional.of(existingDataset));

    service.save(study);

    verify(nodes).post(changesetArgumentCaptor.capture());

    Changeset<NodeId, Node> changeset = changesetArgumentCaptor.getValue();
    assertThat(changeset.getDelete()).hasSize(1);

    assertThat(changeset.getDelete().get(0))
      .isEqualTo(an.instanceVariableNodeId().withIdFromString("IV1").build());
  }

}
