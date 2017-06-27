package fi.thl.thldtkk.api.metadata.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>The annotated map must have strings as values and at least one of those values
 * must be not blank (i.e. a string which is not <code>null</code> and contains at least one
 * non-whitespace character).</p>
 * <p><code>null</code> or empty maps are considered invalid.</p>
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ContainsAtLeastOneNonBlankValueValidator.class)
public @interface ContainsAtLeastOneNonBlankValue {

  String message() default "fi.thl.thldtkk.api.metadata.ContainsAtLeastOneNonBlankValue.message";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
