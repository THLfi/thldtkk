package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static java.util.stream.Collectors.toList;

public class EditorDatasetServiceImpl implements EditorDatasetService {

  private Repository<NodeId, Node> nodes;
  private EditorStudyService editorStudyService;
  private UserHelper userHelper;

  private static final int COMPARE_UNDETERMINED = 0;
  private static final String DEFAULT_LANGUAGE = "fi";
  
  public EditorDatasetServiceImpl(Repository<NodeId, Node> nodes, EditorStudyService editorStudyService,
                                  UserHelper userHelper) {
    this.nodes = nodes;
    this.editorStudyService = editorStudyService;
    this.userHelper = userHelper;
  }

  @Override
  public List<Dataset> findAll() {
    return findAll(DEFAULT_LANGUAGE);
  }

  @Override
  public List<Dataset> findAll(String language) {
    return editorStudyService.findAll()
            .stream()
            .map(study -> study.getDatasets())
            .flatMap(datasets -> datasets.stream())
            .sorted(Comparator.comparing(Dataset::getPrefLabel, (aPrefLabel, anotherPrefLabel) -> {
              boolean areLabelsComparable = StringUtils.hasText(language) 
                      && aPrefLabel.containsKey(language) 
                      && anotherPrefLabel.containsKey(language);
              
              return areLabelsComparable
                      ? aPrefLabel.get(language).toLowerCase().compareTo(anotherPrefLabel.get(language).toLowerCase())
                      : COMPARE_UNDETERMINED;
            }))
            .collect(Collectors.toList());
  }
  
  @AdminOnly
  @Override
  public List<Dataset> getDatasetsByUnitType(UUID unitTypeId) {
    return nodes.query(
      select("id", "type", "properties.*", "references.*"),
      and(keyValue("type.id", Dataset.TERMED_NODE_CLASS),
        keyValue("references.unitType.id", unitTypeId.toString())))
      .map(Dataset::new).collect(Collectors.toList());
  }

  @AdminOnly
  @Override
  public List<Dataset> getUniverseDatasets(UUID universeId){
    List<Dataset> list =  nodes.query(
            select("id", "type", "properties.*", "references.*", "referrers.*"),
            and(keyValue("type.id", Dataset.TERMED_NODE_CLASS),
                    keyValue("references.universe.id", universeId.toString())))
            .map(Dataset::new)
            .collect(toList());
    return list;
  }

  @Override
  public List<Dataset> find(String query, int max) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Dataset> get(UUID id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Dataset> findByPrefLabel(String prefLabel) {
    if (prefLabel == null || prefLabel.isEmpty()) {
      return Optional.empty();
    }

    prefLabel = "\"" + prefLabel + "\"";

    return nodes.query(
            KeyValueCriteria.keyValue(
                    "properties.prefLabel",
                    prefLabel),
            1)
            .map(Dataset::new)
            .findFirst();
  }

  @Override
  public Optional<Dataset> getDatasetWithPredecessorsSuccessors(UUID studyId, UUID datasetId) {
    if (studyId == null || datasetId == null) {
      return Optional.empty();
    }

    Optional<Dataset> dataset = nodes.query(
            select("id", "type", "properties.*", "references.*", "referrers.*",
                    "references.personInRoles:3",
                    "references.person:4",
                    "references.role:4",
                    "referrers.dataSets:2"
            ),
            KeyValueCriteria.keyValue(
                    "id",
                    datasetId.toString()),
            1)
            .map(Dataset::new)
            .findFirst();

    if (dataset.isPresent()) {
      if (dataset.get().getStudy().isPresent()) {
        Study study = getStudyIfAllowed(dataset.get().getStudy().get());
        if (study == null) {
          throwDatasetAccessException(study, "and its datasets");
        }
      }

      List<Dataset> linkedDatasets = Stream.concat(dataset.get().getPredecessors().stream(), dataset.get().getSuccessors().stream())
              .collect(Collectors.toList());

      for (Dataset linkedDataset : linkedDatasets) {
        if (linkedDataset.getStudy().isPresent()) {
          linkedDataset.setStudy(getStudyIfAllowed(linkedDataset.getStudy().get()));
        }
      }
    }

    return dataset;
  }

  private Study getStudyIfAllowed(Study study) {
    if (userHelper.isCurrentUserAdmin()) {
      // Admins can view and edit studies of any organization
      return study;
    }

    if (!study.getOwnerOrganization().isPresent()) {
      return null;
    }

    UUID studyOrganizationId = study.getOwnerOrganization().get().getId();
    Set<UUID> userOrganizationIds = userHelper.getCurrentUserOrganizations()
            .stream()
            .map(org -> org.getId())
            .collect(Collectors.toSet());

    if (!userOrganizationIds.contains(studyOrganizationId)) {
      return null;
    }
    return study;
  }

  private void throwDatasetAccessException(Study study, String cause) {
    throw new AccessDeniedException(
            new StringBuilder()
                    .append("User '")
                    .append(userHelper.getCurrentUser().get().getUsername())
                    .append("' is not allowed to view/save/delete study '")
                    .append(study.getId())
                    .append("' ")
                    .append(cause)
                    .toString()
    );
  }
}
