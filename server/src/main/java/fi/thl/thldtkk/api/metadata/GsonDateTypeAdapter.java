package fi.thl.thldtkk.api.metadata;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.Date;

/**
 * GSON serializer/de-serializer for Date in ISO format.
 */
public class GsonDateTypeAdapter extends TypeAdapter<Date> {

  private DateTimeZone timeZone = DateTimeZone.forID("Europe/Helsinki");

  @Override
  public void write(JsonWriter out, Date date) throws IOException {
    out.value(new DateTime(date, timeZone).toString());
  }

  @Override
  public Date read(JsonReader in) throws IOException {
    return new DateTime(in.nextString(), timeZone).toDate();
  }

}
