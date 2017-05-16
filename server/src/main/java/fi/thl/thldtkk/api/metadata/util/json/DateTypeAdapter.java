package fi.thl.thldtkk.api.metadata.util.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * GSON serializer/de-serializer for Date in ISO format.
 */
public class DateTypeAdapter extends TypeAdapter<Date> {

  private DateTimeZone timeZone;

  public DateTypeAdapter(DateTimeZone timeZone) {
    this.timeZone = timeZone;
  }

  public DateTypeAdapter() {
  }

  @Override
  public void write(JsonWriter out, Date date) throws IOException {
    out.value(new DateTime(date, timeZone).toString());
  }

  @Override
  public Date read(JsonReader in) throws IOException {
    return new DateTime(in.nextString(), timeZone).toDate();
  }

}
