package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.StudyForm;

import java.util.function.Predicate;

public class IsStudyFormSamplePredicate implements Predicate<StudyForm> {

  public static final IsStudyFormSamplePredicate INSTANCE = new IsStudyFormSamplePredicate();

  @Override
  public boolean test(StudyForm studyForm) {
    if (studyForm == null || studyForm.getType() == null) {
      return false;
    }

    switch (studyForm.getType()) {
      case HUMAN_SAMPLE:
      case MICROBE_SAMPLE:
      case ENVIRONMENTAL_SAMPLE:
      case ANIMAL_SAMPLE:
      case CELL_SAMPLE:
      case SAMPLE:
        return true;
      default:
        return false;
    }
  }

}
