package fi.thl.thldtkk.api.metadata.security.annotation;

import fi.thl.thldtkk.api.metadata.security.UserRoles;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "','" + UserRoles.ORG_ADMIN + "')")
public @interface AdminOnly {
}
