package fi.thl.thldtkk.api.metadata.validator;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

public class ContainsAtLeastOneNonBlankValueValidator implements ConstraintValidator<ContainsAtLeastOneNonBlankValue, Map<?, String>> {

  @Override
  public void initialize(ContainsAtLeastOneNonBlankValue constraintAnnotation) {
    // Nothing to initialize
  }

  @Override
  public boolean isValid(Map<?, String> map, ConstraintValidatorContext context) {
    if (map == null) {
      return false;
    }
    else {
      return map.values().stream().anyMatch(v -> StringUtils.hasText(v));
    }
  }

}
