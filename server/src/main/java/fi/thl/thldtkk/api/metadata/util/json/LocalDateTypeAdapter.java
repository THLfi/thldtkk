package fi.thl.thldtkk.api.metadata.util.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * GSON serializer/de-serializer for Date in ISO format.
 */
public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

  public LocalDateTypeAdapter() {
  }

  @Override
  public void write(JsonWriter out, LocalDate date) throws IOException {
    out.value(date.toString());
  }

  @Override
  public LocalDate read(JsonReader in) throws IOException {
      return LocalDate.parse(in.nextString());
  }

}
