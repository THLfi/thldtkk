package fi.thl.thldtkk.api.metadata.service.csv;

import java.util.Optional;

public class GeneratorResult {

  private Optional<byte[]> data;
  
  public GeneratorResult(Optional<byte []> data) {
    this.data = data;
  }
  
  public Optional<byte[]> getData() {
    return this.data;
  }
  
}
