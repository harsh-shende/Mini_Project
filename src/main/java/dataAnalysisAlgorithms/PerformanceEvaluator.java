package dataAnalysisAlgorithms;

import java.util.*;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class PerformanceEvaluator {
    Table inputTable;
    List<Column<?>> originalColumn;
    List<Column<?>> predictedColumn;
    PerformanceEvaluator() {
        this.inputTable=null;
        this.originalColumn=null;
        this.predictedColumn=null;
    }
    public PerformanceEvaluator(Table inputTable) {
        this.inputTable=inputTable;
        this.originalColumn=(this.calculateOriginalColumn());
        this.predictedColumn=(this.calculatePredictedColumn());
    }
    public Table getInputTable() {
        return inputTable;
    }
    public List<Column<?>> getOriginalColumn() {
        return originalColumn;
    }
    public List<Column<?>> getPredictedColumn() {
        return predictedColumn;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    public void setOriginalColumn(List<Column<?>> originalColumn) {
        this.originalColumn=originalColumn;
    }
    public void setPredictedColumn(List<Column<?>> predictedColumn) {
        this.predictedColumn=predictedColumn;
    }
    public List<Column<?>> calculateOriginalColumn() {
        return inputTable.columns();
    }
    public static int getContinuousVariableCount(Table table) {
        //Creating variables
        Table structureOfTable=table.structure();
        int continuousVariablesCount=structureOfTable.stringColumn(2).isEqualTo("INTEGER").size();
        continuousVariablesCount+=structureOfTable.stringColumn(2).isEqualTo("DOUBLE").size();
        return continuousVariablesCount;
    }
    public static List<String> getContinuousVariableNames(Table table) {
        //Creating variables
        Table structureOfTable=table.structure();
        int continuousVariablesCount=getContinuousVariableCount(table);

        //Storing continuous variable name
        String[] continuousVariablesNames=new String[continuousVariablesCount];
        for(int i=0,j=0;i<table.columnCount() && j<continuousVariablesCount;i++) {
            if(Objects.equals(structureOfTable.column(2).getString(i),"INTEGER")) {
                continuousVariablesNames[j]=structureOfTable.column(1).getString(i);
                j+=1;
            }
            if(Objects.equals(structureOfTable.column(2).getString(i),"DOUBLE")) {
                continuousVariablesNames[j]=structureOfTable.column(1).getString(i);
                j+=1;
            }
        }
        return Arrays.stream(continuousVariablesNames).toList();
    }
    public static int getCategoricalVariableCount(Table table) {
        //Creating variables
        Table structureOfTable=table.structure();
        int categoricalVariablesCount=structureOfTable.stringColumn(2).isEqualTo("STRING").size();
        return categoricalVariablesCount;
    }
    public static List<String> getCategoricalVariableName(Table table) {
        //Creating variables
        Table structureOfTable=table.structure();
        int categoricalVariablesCount=getCategoricalVariableCount(table);

        //Storing categorical variable name
        String[] categoricalVariablesNames=new String[categoricalVariablesCount];
        for(int i=0,j=0;i<table.columnCount() && j<categoricalVariablesCount;i++) {
            if(Objects.equals(structureOfTable.column(2).getString(i),"STRING")) {
                categoricalVariablesNames[j]=structureOfTable.column(1).getString(i);
                j+=1;
            }
        }
        return Arrays.stream(categoricalVariablesNames).toList();
    }
    public List<Column<?>> calculatePredictedColumn() {
        //Creating Variables
        predictedColumn=new ArrayList<>();
        Scanner sc=new Scanner(System.in);
        int columnCount=(this.originalColumn.size());
        SplitData splitData01=new SplitData(inputTable);
        Table testData=splitData01.getTestData();
        Table trainData=splitData01.getTrainData();
        Table validationData=splitData01.getValidationTable();
        int continuousVariablesCount=getContinuousVariableCount(inputTable);
        List<String> continuousVariablesNames=getContinuousVariableNames(inputTable);
        int categoricalVariablesCount=getCategoricalVariableCount(inputTable);
        List<String> categoricalVariablesNames=getCategoricalVariableName(inputTable);

        //Iterating over each column
        for(int i=0;i<columnCount;i++) {
            Column<?> column=(this.originalColumn.get(i));
            String columnName=(this.originalColumn.get(i).name());
            if(continuousVariablesNames.contains(columnName)) {
                //Continuous Variable
                System.out.println("Variable Name: "+(columnName));
                System.out.println("Type: Continuous Variable");
                System.out.println("Enter the algorithm you want to use: 01) SLR, 02) MLR");
                int choice01=sc.nextInt();
                if(choice01==1) {
                    //Simple Linear Regression
                } else if(choice01==2) {
                    //Multiple Linear Regression
                } else {
                    //Invalid Choice
                }
            } else {
                //Categorical Variable
            }
        }
        return predictedColumn;
    }
}
