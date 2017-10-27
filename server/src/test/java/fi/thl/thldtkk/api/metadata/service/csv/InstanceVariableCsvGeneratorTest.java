package fi.thl.thldtkk.api.metadata.service.csv;

import fi.thl.thldtkk.api.metadata.domain.CodeItem;
import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.Concept;
import fi.thl.thldtkk.api.metadata.domain.ConceptScheme;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Quantity;
import fi.thl.thldtkk.api.metadata.domain.Unit;
import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.domain.Variable;
import fi.thl.thldtkk.api.metadata.test.a;
import fi.thl.thldtkk.api.metadata.test.an;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class InstanceVariableCsvGeneratorTest {

  private static final String DEFAULT_TEST_LANGUAGE = "fi";
  private static final String DEFAULT_ENCODING = "ISO-8859-15";
 
  private static final int ROW_INDEX_HEADER = 0;
  private static final int ROW_INDEX_DATA = 1;
  private static final String QUOTED_CSV_COLUMN_DATA_PATTERN = "^\"(.*)\"$";
     
  @Test
  public void shouldSerializeSimpleInstanceVariableWithIdAndLabel() throws UnsupportedEncodingException {
    InstanceVariable simpleInstanceVariable = an.instanceVariable()
            .withIdFromString("simple-iv")
            .withPrefLabel("Simple Instance Variable")
            .build();
    
    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
            Arrays.asList(simpleInstanceVariable), 
            DEFAULT_TEST_LANGUAGE,
            DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
        
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());
        
    assertTrue(columnDataByHeaders.containsKey("id"));
    assertTrue(columnDataByHeaders.containsKey("prefLabel"));
    
    String actualId = columnDataByHeaders.get("id");
    String expectedId = simpleInstanceVariable.getId().toString();

    String actualPrefLabel = columnDataByHeaders.get("prefLabel");
    String expectedPrefLabel = simpleInstanceVariable.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    
    assertEquals(expectedId, actualId);
    assertEquals(expectedPrefLabel, actualPrefLabel);
    
  }
  
  private Map<String, String> readSingleLineCsvData(byte[] csvBytes) throws UnsupportedEncodingException {
    
    Map<String, String> columnDataByHeaderName = new HashMap<>();
      
    String allCsvLines = new String(csvBytes, DEFAULT_ENCODING);
    
    String[] rows = allCsvLines.split("\n");

    List<String> headerRow = Arrays.asList(rows[ROW_INDEX_HEADER].split(";"));
    List<String> dataRow = Arrays.asList(rows[ROW_INDEX_DATA].split(";"));

    headerRow.stream().forEach(headerName -> {
      int dataIndex = headerRow.indexOf(headerName);
      String data = dataIndex < dataRow.size() ? dataRow.get(headerRow.indexOf(headerName)) : "";

      if(data.startsWith("\"") && data.endsWith("\"")) {
        Pattern pattern = Pattern.compile(QUOTED_CSV_COLUMN_DATA_PATTERN);
        Matcher matcher = pattern.matcher(data);        
        data = matcher.matches() ? matcher.group(1) : data;
      }

      columnDataByHeaderName.put(headerName, data);
    });
          
    return columnDataByHeaderName;  
  
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithBasicProperties() throws UnsupportedEncodingException {
    
    InstanceVariable basicPropertyInstanceVariable = an.instanceVariable()
            .withIdFromString("basicIv")
            .withPrefLabel("Basic Instance Variable")
            .withDescription("A basic description")
            .withPublished(true)
            .withReferencePeriodStart(LocalDate.of(1995, Month.MARCH, 12))
            .withReferencePeriodEnd(LocalDate.of(2007, Month.DECEMBER, 18))
            .withTechnicalName("basic.name.1")
            .withValueDomainType(InstanceVariable.VALUE_DOMAIN_TYPE_DESCRIBED)
            .withQualityStatement("Data for year 1996-1997 were lost due to hard drive corruption.")
            .withMissingValues("Sampling was not successful in certain cases")
            .withDefaultMissingValue("99")
            .withPartOfGroup("Background Information")
            .withDataType("string")
            .withDataFormat("([a-zA-Z]*)")
            .build();
    
    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
        Arrays.asList(basicPropertyInstanceVariable), 
        DEFAULT_TEST_LANGUAGE,
        DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
        
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());
    
    assertTrue(columnDataByHeaders.containsKey("id"));
    assertTrue(columnDataByHeaders.containsKey("prefLabel"));
    assertTrue(columnDataByHeaders.containsKey("description"));
    assertTrue(columnDataByHeaders.containsKey("published"));
    assertTrue(columnDataByHeaders.containsKey("referencePeriodStart"));
    assertTrue(columnDataByHeaders.containsKey("referencePeriodEnd"));
    assertTrue(columnDataByHeaders.containsKey("technicalName"));
    assertTrue(columnDataByHeaders.containsKey("valueDomainType"));
    assertTrue(columnDataByHeaders.containsKey("qualityStatement"));
    assertTrue(columnDataByHeaders.containsKey("missingValues"));
    assertTrue(columnDataByHeaders.containsKey("defaultMissingValue"));
    assertTrue(columnDataByHeaders.containsKey("partOfGroup"));
    assertTrue(columnDataByHeaders.containsKey("dataType"));
    assertTrue(columnDataByHeaders.containsKey("dataFormat"));
    
    String expectedId = basicPropertyInstanceVariable.getId().toString();
    String actualId = columnDataByHeaders.get("id");

    String expectedPrefLabel = basicPropertyInstanceVariable.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualPrefLabel = columnDataByHeaders.get("prefLabel");
    
    String expectedDescription = basicPropertyInstanceVariable.getDescription().get(DEFAULT_TEST_LANGUAGE);
    String actualDescription = columnDataByHeaders.get("description");
    
    String expectedPublished = basicPropertyInstanceVariable.isPublished().get().toString();
    String actualPublished = columnDataByHeaders.get("published");
    
    String expectedTechnicalName = basicPropertyInstanceVariable.getTechnicalName().get();
    String actualTechnicalName = columnDataByHeaders.get("technicalName");

    String expectedReferencePeriodStart = basicPropertyInstanceVariable.getReferencePeriodStart().get().toString();
    String actualReferencePeriodStart = columnDataByHeaders.get("referencePeriodStart");
    
    String expectedReferencePeriodEnd = basicPropertyInstanceVariable.getReferencePeriodEnd().get().toString();
    String actualReferencePeriodEnd = columnDataByHeaders.get("referencePeriodEnd");
    
    String expectedValueDomainType = basicPropertyInstanceVariable.getValueDomainType().get();
    String actualValueDomainType = columnDataByHeaders.get("valueDomainType");

    String expectedQualityStatement = basicPropertyInstanceVariable.getQualityStatement().get(DEFAULT_TEST_LANGUAGE);
    String actualQualityStatement = columnDataByHeaders.get("qualityStatement");
    
    String expectedMissingValues = basicPropertyInstanceVariable.getMissingValues().get(DEFAULT_TEST_LANGUAGE);
    String actualMissingValues = columnDataByHeaders.get("missingValues");

    String expectedDefaultMissingValue = basicPropertyInstanceVariable.getDefaultMissingValue().get();
    String actualDefaultMissingValue = columnDataByHeaders.get("defaultMissingValue");

    String expectedPartOfGroup = basicPropertyInstanceVariable.getPartOfGroup().get(DEFAULT_TEST_LANGUAGE);
    String actualPartOfGroup = columnDataByHeaders.get("partOfGroup");
    
    String expectedDataFormat = basicPropertyInstanceVariable.getDataFormat().get(DEFAULT_TEST_LANGUAGE);
    String actualDataFormat = columnDataByHeaders.get("dataFormat");
    
    String expectedDataType = basicPropertyInstanceVariable.getDataType().get();
    String actualDataType = columnDataByHeaders.get("dataType");
    
    assertEquals(expectedId, actualId);
    assertEquals(expectedPrefLabel, actualPrefLabel);
    assertEquals(expectedDescription, actualDescription);
    assertEquals(expectedPublished, actualPublished);
    assertEquals(expectedTechnicalName, actualTechnicalName);
    assertEquals(expectedReferencePeriodStart, actualReferencePeriodStart);
    assertEquals(expectedReferencePeriodEnd, actualReferencePeriodEnd);
    assertEquals(expectedValueDomainType, actualValueDomainType);
    assertEquals(expectedQualityStatement, actualQualityStatement);
    assertEquals(expectedMissingValues, actualMissingValues);
    assertEquals(expectedDefaultMissingValue, actualDefaultMissingValue);
    assertEquals(expectedPartOfGroup, actualPartOfGroup);
    assertEquals(expectedDataFormat, actualDataFormat);
    assertEquals(expectedDataType, actualDataType);
        
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithUnit() throws UnsupportedEncodingException {
    
    Unit unit = a.unit()
            .withIdFromString("unit-hr")
            .withPrefLabel("Heart Rate")
            .withSymbol("bpm")
            .build();
   
    InstanceVariable unitIv = an.instanceVariable()
       .withIdFromString("unitIv")
       .withUnit(unit)
       .build();
    
    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
            Arrays.asList(unitIv), 
            DEFAULT_TEST_LANGUAGE,
            DEFAULT_ENCODING);
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());
    
    assertTrue(columnDataByHeaders.containsKey("unit.id"));
    assertTrue(columnDataByHeaders.containsKey("unit.prefLabel"));
    assertTrue(columnDataByHeaders.containsKey("unit.symbol"));
    
    String expectedUnitId = unit.getId().toString();
    String actualUnitId = columnDataByHeaders.get("unit.id");
    
    String expectedUnitPrefLabel = unit.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualUnitPrefLabel = columnDataByHeaders.get("unit.prefLabel");
    
    String expectedUnitSymbol = unit.getSymbol().get(DEFAULT_TEST_LANGUAGE);
    String actualUnitSymbol = columnDataByHeaders.get("unit.symbol");

    assertEquals(expectedUnitId, actualUnitId);
    assertEquals(expectedUnitPrefLabel, actualUnitPrefLabel);
    assertEquals(expectedUnitSymbol, actualUnitSymbol);    
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithQuantity() throws UnsupportedEncodingException {
   
    Quantity quantity = a.quantity()
           .withIdFromString("quantity-freq")
           .withPrefLabel("Frequency")
           .build();

    InstanceVariable quantityIv = an.instanceVariable()
           .withIdFromString("unitIv")
           .withQuantity(quantity)
           .build();
   
    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
           Arrays.asList(quantityIv), 
           DEFAULT_TEST_LANGUAGE,
           DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());

    assertTrue(columnDataByHeaders.containsKey("quantity.id"));
    assertTrue(columnDataByHeaders.containsKey("quantity.prefLabel"));
    
    String expectedQuantityId = quantity.getId().toString();
    String actualQuantityId = columnDataByHeaders.get("quantity.id");
    
    String expectedQuantityPrefLabel = quantity.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualQuantityPrefLabel = columnDataByHeaders.get("quantity.prefLabel");
    
    assertEquals(expectedQuantityId, actualQuantityId);
    assertEquals(expectedQuantityPrefLabel, actualQuantityPrefLabel);
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithCodeList() throws UnsupportedEncodingException {
    CodeItem codeItemFrequentCycling = a.codeItem()
            .withIdFromString("codeitem-cycle-every-day")
            .withPrefLabel("Cycling every day")
            .withCode("1")
            .build();
    
    CodeItem codeItemLessFrequentCycling = a.codeItem()
            .withIdFromString("codeitem-cycle-weekly")
            .withPrefLabel("Cycling every week")
            .withCode("2")
            .build();

    CodeItem codeItemInfrequentCycling = a.codeItem()
            .withIdFromString("codeitem-cycle-monthly")
            .withPrefLabel("Cycling a few times a month")
            .withCode("3")
            .build();    
    
    CodeList codeList = a.codeList()
            .withIdFromString("codelist-cycling")
            .withDescription("A codelist for measuring the frequency of cycling")
            .withPrefLabel("Cycling")
            .withOwner("Research organization")
            .withReferenceId("rsco-2017-1")
            .withCodeItems(Arrays.asList(codeItemFrequentCycling, codeItemLessFrequentCycling, codeItemInfrequentCycling))
            .withCodeListType(CodeList.CODE_LIST_TYPE_INTERNAL)
            .build();
    
    InstanceVariable codeListIv = an.instanceVariable()
       .withIdFromString("codeListIv")
       .withCodeList(codeList)
       .build();

    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
       Arrays.asList(codeListIv), 
       DEFAULT_TEST_LANGUAGE,
       DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());

    assertTrue(columnDataByHeaders.containsKey("codeList.id"));
    assertTrue(columnDataByHeaders.containsKey("codeList.prefLabel"));
    assertTrue(columnDataByHeaders.containsKey("codeList.description"));
    assertTrue(columnDataByHeaders.containsKey("codeList.owner"));
    assertTrue(columnDataByHeaders.containsKey("codeList.referenceId"));
    assertTrue(columnDataByHeaders.containsKey("codeList.codeItems"));
    assertTrue(columnDataByHeaders.containsKey("codeList.codeListType"));
    
    String expectedCodeListId = codeList.getId().toString();
    String actualCodeListId = columnDataByHeaders.get("codeList.id");
    
    String expectedCodeListPrefLabel = codeList.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualCodeListPrefLabel = columnDataByHeaders.get("codeList.prefLabel");

    String expectedCodeListDescription = codeList.getDescription().get(DEFAULT_TEST_LANGUAGE);
    String actualCodeListDescription = columnDataByHeaders.get("codeList.description");
    
    String expectedCodeListOwner = codeList.getOwner().get(DEFAULT_TEST_LANGUAGE);
    String actualCodeListOwner = columnDataByHeaders.get("codeList.owner");
    
    String expectedCodeListReferenceId = codeList.getReferenceId().get();
    String actualCodeListReferenceId = columnDataByHeaders.get("codeList.referenceId");
    
    String expectedCodeListCodeListType = codeList.getCodeListType().get();
    String actualCodeListCodeListType = columnDataByHeaders.get("codeList.codeListType");

    String expectedCodeListCodeItems = "'1':'Cycling every day', '2':'Cycling every week', '3':'Cycling a few times a month'";
    String actualCodeListCodeItems = columnDataByHeaders.get("codeList.codeItems");
        
    assertEquals(expectedCodeListId, actualCodeListId);
    assertEquals(expectedCodeListPrefLabel, actualCodeListPrefLabel);
    assertEquals(expectedCodeListDescription, actualCodeListDescription);
    assertEquals(expectedCodeListReferenceId, actualCodeListReferenceId);
    assertEquals(expectedCodeListOwner, actualCodeListOwner);
    assertEquals(expectedCodeListCodeListType, actualCodeListCodeListType);
    assertEquals(expectedCodeListCodeItems, actualCodeListCodeItems);
    
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithValueRanges() throws UnsupportedEncodingException {
    InstanceVariable valueRangeIv = an.instanceVariable()
            .withIdFromString("valueRangeIv")
            .withValueRangeMin(BigDecimal.valueOf(2.543))
            .withValueRangeMax(BigDecimal.valueOf(40))
            .build();

    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
       Arrays.asList(valueRangeIv), 
       DEFAULT_TEST_LANGUAGE,       DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());
    
    assertTrue(columnDataByHeaders.containsKey("valueRangeMin"));
    assertTrue(columnDataByHeaders.containsKey("valueRangeMax"));
    
    String expectedValueRangeMin = valueRangeIv.getValueRangeMin().get().toPlainString();
    String actualValueRangeMin = columnDataByHeaders.get("valueRangeMin");

    String expectedValueRangeMax = valueRangeIv.getValueRangeMax().get().toPlainString();
    String actualValueRangeMax = columnDataByHeaders.get("valueRangeMax");

    assertEquals(expectedValueRangeMin, actualValueRangeMin);
    assertEquals(expectedValueRangeMax, actualValueRangeMax); 
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithUnitType() throws UnsupportedEncodingException {
    UnitType unitType = a.unitType()
            .withIdFromString("unit-type-bacteria")
            .withPrefLabel("Bacteria")
            .withDescription("Bacteria observable in laboratory conditions")
            .build();
            
    InstanceVariable unitTypeIv = an.instanceVariable()
        .withIdFromString("unitTypeIv")
        .withUnitType(unitType)
        .build();

    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
       Arrays.asList(unitTypeIv), 
       DEFAULT_TEST_LANGUAGE,
       DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());
    
    assertTrue(columnDataByHeaders.containsKey("unitType.prefLabel"));
    assertTrue(columnDataByHeaders.containsKey("unitType.id"));
    assertTrue(columnDataByHeaders.containsKey("unitType.description"));

    String expectedUnitTypeId = unitType.getId().toString();
    String actualUnitTypeId = columnDataByHeaders.get("unitType.id");
    
    String expectedUnitTypePrefLabel = unitType.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualUnitTypePrefLabel = columnDataByHeaders.get("unitType.prefLabel");

    String expectedUnitTypeDescription = unitType.getDescription().get(DEFAULT_TEST_LANGUAGE);
    String actualUnitTypeDescription = columnDataByHeaders.get("unitType.description");    
    
    assertEquals(expectedUnitTypeId, actualUnitTypeId);
    assertEquals(expectedUnitTypePrefLabel, actualUnitTypePrefLabel);
    assertEquals(expectedUnitTypeDescription, actualUnitTypeDescription); 
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithVariable() throws UnsupportedEncodingException {
    
    Variable variable = a.variable()
            .withIdFromString("variable-commuting")
            .withPrefLabel("Commuting")
            .withDescription("Commuting signifies in general terms travel between home and work")
            .build();
    
    InstanceVariable variableIv = an.instanceVariable()
            .withIdFromString("variableIv")
            .withVariable(variable)
            .build();

    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
       Arrays.asList(variableIv),
       DEFAULT_TEST_LANGUAGE,
       DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());
    
    assertTrue(columnDataByHeaders.containsKey("variable.prefLabel"));
    assertTrue(columnDataByHeaders.containsKey("variable.id"));
    assertTrue(columnDataByHeaders.containsKey("variable.description"));

    String expectedVariableId = variable.getId().toString();
    String actualVariableId = columnDataByHeaders.get("variable.id");
    
    String expectedVariablePrefLabel = variable.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualVariablePrefLabel = columnDataByHeaders.get("variable.prefLabel");

    String expectedVariableDescription = variable.getDescription().get(DEFAULT_TEST_LANGUAGE);
    String actualVariableDescription = columnDataByHeaders.get("variable.description");    
    
    assertEquals(expectedVariableId, actualVariableId);
    assertEquals(expectedVariablePrefLabel, actualVariablePrefLabel);
    assertEquals(expectedVariableDescription, actualVariableDescription);  
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithSourceDatasetAndSourceDescription() throws UnsupportedEncodingException {
    Dataset sourceDataset = a.dataset()
            .withIdFromString("source-dataset")
            .withPrefLabel("Source Dataset")
            .build();
    
    InstanceVariable sourceIv = an.instanceVariable()
            .withIdFromString("source-iv")
            .withSource(sourceDataset)
            .withSourceDescription("This variable uses registry information from the associated dataset")
            .build();
    
   InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
           Arrays.asList(sourceIv),
           DEFAULT_TEST_LANGUAGE,
           DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());
    
    assertTrue(columnDataByHeaders.containsKey("source.dataset.prefLabel"));
    assertTrue(columnDataByHeaders.containsKey("source.dataset.id"));
    assertTrue(columnDataByHeaders.containsKey("source.description"));

    String expectedSourceDatasetPrefLabel = sourceIv.getSource().get().getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualSourceDatasetPrefLabel = columnDataByHeaders.get("source.dataset.prefLabel");

    String expectedSourceDatasetId = sourceIv.getSource().get().getId().toString();
    String actualSourceDatasetId = columnDataByHeaders.get("source.dataset.id");
    
    String expectedSourceDescription = sourceIv.getSourceDescription().get(DEFAULT_TEST_LANGUAGE);
    String actualSourceDescription = columnDataByHeaders.get("source.description");
    
    assertEquals(expectedSourceDatasetPrefLabel, actualSourceDatasetPrefLabel);
    assertEquals(expectedSourceDatasetId, actualSourceDatasetId);
    assertEquals(expectedSourceDescription, actualSourceDescription);
  }
  
  @Test
  public void shouldSerializeMultipleInstanceVariables() throws UnsupportedEncodingException {

     final int ROW_INDEX_HEADER = 0;
     final int ROW_INDEX_DATA_1 = 1;
     final int ROW_INDEX_DATA_2 = 2;
     
    InstanceVariable multiLineInstanceVariableFirst = an.instanceVariable()
        .withIdFromString("multiline-iv-1")
        .withPrefLabel("First multiline variable")
        .build();

    InstanceVariable multiLineInstanceVariableSecond = an.instanceVariable()
        .withIdFromString("multiline-iv-2")
        .withPrefLabel("Second multiline variable")
        .build();
        
    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
            Arrays.asList(multiLineInstanceVariableFirst, multiLineInstanceVariableSecond), 
            DEFAULT_TEST_LANGUAGE,
            DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    String allCsvLines = new String(result.getData().get(), DEFAULT_ENCODING);
    String[] rows = allCsvLines.split("\n");
    
    assertTrue(rows.length == 3);
    
    List<String> headerRow = Arrays.asList(rows[ROW_INDEX_HEADER].split(";"));
    List<String> dataRow1 = Arrays.asList(rows[ROW_INDEX_DATA_1].split(";"));
    List<String> dataRow2 = Arrays.asList(rows[ROW_INDEX_DATA_2].split(";"));
    
    int idColumnIndex = headerRow.indexOf("id");
    int prefLabelColumnIndex = headerRow.indexOf("prefLabel");
    
    String expectedPrefLabelFirst = multiLineInstanceVariableFirst.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualPrefLabelFirst = dataRow1.get(prefLabelColumnIndex);
    
    String expectedIdFirst = multiLineInstanceVariableFirst.getId().toString();
    String actualIdFirst = dataRow1.get(idColumnIndex);

    String expectedPrefLabelSecond = multiLineInstanceVariableSecond.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualPrefLabelSecond = dataRow2.get(prefLabelColumnIndex);
    
    String expectedIdSecond = multiLineInstanceVariableSecond.getId().toString();
    String actualIdSecond = dataRow2.get(idColumnIndex);

    assertEquals(expectedPrefLabelFirst, actualPrefLabelFirst);
    assertEquals(expectedIdFirst, actualIdFirst);
    assertEquals(expectedPrefLabelSecond, actualPrefLabelSecond);
    assertEquals(expectedIdSecond, actualIdSecond);
  }
  
  @Test
  public void shouldSerializeInstanceVariableWithParentDataset() throws UnsupportedEncodingException {
    Dataset parentDataset = a.dataset()
            .withIdFromString("parent-dataset")
            .withPrefLabel("Parent Dataset")
            .withReferencePeriodStart(LocalDate.of(2011, Month.FEBRUARY, 4))
            .withReferencePeriodEnd(LocalDate.of(2017, Month.DECEMBER, 28))
            .build();
    
    InstanceVariable parentDatasetIv = an.instanceVariable()
            .withIdFromString("parent-dataset-iv")
            .withDataSet(parentDataset)
            .build();
    
   InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
           Arrays.asList(parentDatasetIv),
           DEFAULT_TEST_LANGUAGE,
           DEFAULT_ENCODING);
           
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());
    
    assertTrue(columnDataByHeaders.containsKey("dataset.prefLabel"));
    assertTrue(columnDataByHeaders.containsKey("dataset.id"));
    assertTrue(columnDataByHeaders.containsKey("dataset.referencePeriodStart"));
    assertTrue(columnDataByHeaders.containsKey("dataset.referencePeriodEnd"));
    
    String expectedDatasetPrefLabel = parentDataset.getPrefLabel().get(DEFAULT_TEST_LANGUAGE);
    String actualDatasetPrefLabel = columnDataByHeaders.get("dataset.prefLabel");

    String expectedDatasetId = parentDataset.getId().toString();
    String actualDatasetId = columnDataByHeaders.get("dataset.id");
    
    String expectedDatasetReferencePeriodStart = parentDataset.getReferencePeriodStart().get().toString();
    String actualDatasetReferencePeriodStart = columnDataByHeaders.get("dataset.referencePeriodStart");

    String expectedDatasetReferencePeriodEnd = parentDataset.getReferencePeriodEnd().get().toString();
    String actualDatasetReferencePeriodEnd = columnDataByHeaders.get("dataset.referencePeriodEnd");
    
    assertEquals(expectedDatasetPrefLabel, actualDatasetPrefLabel);
    assertEquals(expectedDatasetId, actualDatasetId);
    assertEquals(expectedDatasetReferencePeriodStart, actualDatasetReferencePeriodStart);
    assertEquals(expectedDatasetReferencePeriodEnd, actualDatasetReferencePeriodEnd);

  }
  
  @Test
  public void shouldSerializeFreeConcepts() throws UnsupportedEncodingException {
    
    final int ROW_INDEX_HEADER = 0;
    final int ROW_INDEX_DATA = 1;
     
    Map<String,String> freeConcepts = new LinkedHashMap<>();
    freeConcepts.put(DEFAULT_TEST_LANGUAGE, "keyword1; another keyword; more keywords");
    
    InstanceVariable freeConceptIv = an.instanceVariable()
            .withIdFromString("free-concept-iv")
            .withFreeConcepts(freeConcepts)
            .build();
    
    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
        Arrays.asList(freeConceptIv),
        DEFAULT_TEST_LANGUAGE,
        DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    
    String allCsvLines = new String(result.getData().get(), DEFAULT_ENCODING);
    String[] rows = allCsvLines.split("\n");
    
    List<String> headerRow = Arrays.asList(rows[ROW_INDEX_HEADER].split(";"));
    
    assertTrue(headerRow.contains("freeConcepts"));
    
    int freeConceptsStartIndex = rows[ROW_INDEX_DATA].indexOf("\"") + 1;
    int freeConceptsEndIndex = rows[ROW_INDEX_DATA].lastIndexOf("\"");

    String actualFreeConcepts = rows[ROW_INDEX_DATA].substring(freeConceptsStartIndex, freeConceptsEndIndex);
    String expectedFreeConcepts = freeConceptIv.getFreeConcepts().get(DEFAULT_TEST_LANGUAGE);
       
    assertEquals(expectedFreeConcepts, actualFreeConcepts);
  }
  
  @Test
  public void shouldSerializeConceptsFromScheme() throws UnsupportedEncodingException{
    
    ConceptScheme ysoScheme = a.conceptScheme()
            .withIdFromString("yso-scheme")
            .withPrefLabel("YSO")
            .build();
    
    ConceptScheme meshScheme = a.conceptScheme()
            .withIdFromString("yso-scheme")
            .withPrefLabel("MeSH")
            .build();
    
    Concept ysoConcept = a.concept()
            .withIdFromString("yso-concept-id")
            .withPrefLabel("Health and Wellness Sector")
            .withConceptScheme(ysoScheme)
            .build();
    
    Concept meshConcept = a.concept()
            .withIdFromString("mesh-concept-id")
            .withPrefLabel("Health Education")
            .withConceptScheme(meshScheme)
            .build();    

    InstanceVariable conceptsFromSchemeIv = an.instanceVariable()
            .withIdFromString("scheme-concept-id")
            .withConceptsFromScheme(Arrays.asList(ysoConcept, meshConcept))
            .build();
    
    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
        Arrays.asList(conceptsFromSchemeIv),
        DEFAULT_TEST_LANGUAGE,
        DEFAULT_ENCODING);
    
    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());

    assertTrue(columnDataByHeaders.containsKey("conceptsFromScheme"));
    
    String expectedConceptsFromScheme = "'Health and Wellness Sector (YSO)', 'Health Education (MeSH)'";
    String actualConceptsFromScheme = columnDataByHeaders.get("conceptsFromScheme");
    
    assertEquals(expectedConceptsFromScheme, actualConceptsFromScheme);    
  }
  
  @Test
  public void shouldSerializeInstanceQuestions() throws UnsupportedEncodingException {
    InstanceQuestion healthQuestion = an.instanceQuestion()
            .withIdFromString("question-health")
            .withPrefLabel("Do you generally feel healthy?")
            .build();
    
    InstanceQuestion livingQuestion = an.instanceQuestion()
            .withIdFromString("question-living")
            .withPrefLabel("How many people live with you in the same household?")
            .build();
    
    InstanceVariable questionIv = an.instanceVariable()
            .withIdFromString("question-iv")
            .withInstanceQuestions(Arrays.asList(healthQuestion, livingQuestion))
            .build();
    
    
    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(
    Arrays.asList(questionIv),
    DEFAULT_TEST_LANGUAGE,
    DEFAULT_ENCODING);

    GeneratorResult result = generator.generate();
    Map<String, String> columnDataByHeaders = readSingleLineCsvData(result.getData().get());

    assertTrue(columnDataByHeaders.containsKey("instanceQuestions"));
    
    String expectedInstanceQuestions = "'Do you generally feel healthy?', 'How many people live with you in the same household?'";
    String actualInstanceQuestions = columnDataByHeaders.get("instanceQuestions");
    
    assertEquals(expectedInstanceQuestions, actualInstanceQuestions);
  }
  
  
}
