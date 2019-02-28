package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.Concept;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.join;

public class ConceptSerializer extends JsonSerializer<List<Concept>> {

  public static final String SEPARATOR = "; ";

  @Override
  public void serialize(List<Concept> concepts, JsonGenerator generator, SerializerProvider sp) throws IOException {
    if (concepts == null || concepts.isEmpty()) {
      generator.writeNull();
    } else {
      String language = sp.getLocale().getLanguage();

      List<String> conceptPrefLabels = concepts.stream()
        .filter(concept -> concept.getPrefLabel().containsKey(language))
        .map(concept -> concept.getPrefLabel().get(language))
        .filter(StringUtils::isNotBlank)
        .collect(toList());

      // Concept's scheme is not included in CSV export because it currently cannot be efficiently fetched from Termed, see issue THLDTKK-700

      generator.writeString(join(conceptPrefLabels, SEPARATOR));
    }
  }

}
