package fi.thl.thldtkk.api.metadata.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControllerUtils {
  public static List<String> parseSelect(String selectString) throws IOException {
    ArrayList<String> select = null;
    if (selectString != null) {
      select = new ObjectMapper().readValue(selectString, new TypeReference<ArrayList<String>>() {});
    }

    return select;
  }
}
