package fi.thl.thldtkk.api.metadata.validator;

import fi.thl.thldtkk.api.metadata.domain.NodeEntity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullAndHasIdValidator implements ConstraintValidator<NotNullAndHasId, NodeEntity> {

  @Override
  public void initialize(NotNullAndHasId constraintAnnotation) {

  }

  @Override
  public boolean isValid(NodeEntity entity, ConstraintValidatorContext context) {
    if (entity != null) {
      if (entity.getId() != null) {
        return true;
      }
    }
    return false;
  }

}
