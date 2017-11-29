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
 * <p>Validates NodeEntity instances. Valid value must not be<code>null</code>
 * and its <code>id</code> must also not be <code>null</code>.</p>
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NotNullAndHasIdValidator.class)
public @interface NotNullAndHasId {

  String message() default "{javax.validation.constraints.NotNull.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
