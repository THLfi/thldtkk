package fi.thl.thldtkk.api.metadata.service.report.processingactivities;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.service.report.processingactivities.model.ProcessingActivitiesModel;

@Component
public class ProcessingActivitiesModelFactory {

    @Autowired
    EditorStudyService editorStudyService;

    @Autowired
    OrganizationService organizationService;

    public ProcessingActivitiesModel build(UUID organizationId) {
        ProcessingActivitiesModel processingActivitiesModel = new ProcessingActivitiesModel();

        processingActivitiesModel.setOrganization(organizationService.get(organizationId));
        List<Study> organizationStudies = findStudies(organizationId);
        processingActivitiesModel.setStudies(organizationStudies);
        return processingActivitiesModel;
    }

    private List<Study> findStudies(UUID organizationId) {
        List<String> fields = Stream.of(
            "id",
            "type",
            "properties.*",
            "references.personInRoles",
            "references.role:2",
            "references.person:2",
            "references.associatedOrganizations",
            "references.links",
            "references.organization:2"
        ).collect(Collectors.toList());

        return editorStudyService.getOrganizationStudies(organizationId, fields);
    }
}