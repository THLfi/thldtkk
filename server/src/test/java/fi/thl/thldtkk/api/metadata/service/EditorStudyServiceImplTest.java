package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.service.termed.EditorStudyServiceImpl;
import fi.thl.thldtkk.api.metadata.test.a;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EditorStudyServiceImplTest {

  EditorStudyService service;

  @Mock
  Repository<NodeId, Node> nodes;
  @Mock
  UserHelper userHelper;
  @Captor
  ArgumentCaptor<Changeset<NodeId, Node>> changesetArgumentCaptor;

  @Before
  public void initServiceAndMocks() {
    initMocks(this);

    when(nodes.get(any(Select.class), any(NodeId.class)))
      .thenReturn(Optional.empty());
    when(userHelper.isCurrentUserAdmin())
      .thenReturn(true);

    service = new EditorStudyServiceImpl(nodes, userHelper);
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

}
