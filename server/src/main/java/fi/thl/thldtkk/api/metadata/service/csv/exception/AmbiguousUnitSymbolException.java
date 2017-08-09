package fi.thl.thldtkk.api.metadata.service.csv.exception;

import java.text.MessageFormat;

public class AmbiguousUnitSymbolException extends Exception {
 
  private String symbol;
  private static final String MESSAGE_PREFIX = "Unit symbol '{0}' represents more than one known unit.";
  
  public AmbiguousUnitSymbolException(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String getMessage() {
    return MessageFormat.format(MESSAGE_PREFIX, symbol) + super.getMessage();
  }
  
}
    

