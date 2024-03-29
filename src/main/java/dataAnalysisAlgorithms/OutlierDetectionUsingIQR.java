package dataAnalysisAlgorithms;

import java.util.Objects;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

public class OutlierDetectionUsingIQR {
    private Table inputTable;
    private Table outputTable;
    public OutlierDetectionUsingIQR() {
        this.inputTable=null;
        this.outputTable=null;
    }
    public OutlierDetectionUsingIQR(Table inputTable) {
        this.inputTable=inputTable;
        this.outputTable=(this.calculateOutputTable());
    }
    public Table getInputTable() {
        return inputTable;
    }
    public Table getOutputTable() {
        return outputTable;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    public void setOutputTable(Table outputTable) {
        this.outputTable=outputTable;
    }
    public static int getContinuousVariableCount(Table table) {
        //Creating variables
        Table structureOfTable=table.structure();
        int continuousVariablesCount=structureOfTable.stringColumn(2).isEqualTo("INTEGER").size();
        continuousVariablesCount+=structureOfTable.stringColumn(2).isEqualTo("DOUBLE").size();
        return continuousVariablesCount;
    }
    public static String[] getContinuousVariableNames(Table table) {
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
        return continuousVariablesNames;
    }
    public Table calculateOutputTable() {
        //Creating variables
        Table table=this.inputTable;
        int totalRows=table.rowCount();
        
        //Storing attributes
        int continuousVariablesCount=getContinuousVariableCount(table);
        
        //Storing continuous variable name
        String[] continuousVariablesNames=getContinuousVariableNames(table);
        
        //Iterating over continuous variables
        for(int i=0;i<continuousVariablesCount;i++) {
            double quartile01=0,quartile03=0;
            Column<?> desiredVariable=table.column(continuousVariablesNames[i]);
            String desiredVType=desiredVariable.type().name();
            if(desiredVType.equals("INTEGER")) {
                quartile01=((IntColumn)table.column(continuousVariablesNames[i])).quartile1();
                quartile03=((IntColumn)table.column(continuousVariablesNames[i])).quartile3();
            } else {
                quartile01=((DoubleColumn)table.column(continuousVariablesNames[i])).quartile1();
                quartile03=((DoubleColumn)table.column(continuousVariablesNames[i])).quartile3();
            }
            double interQuartileRange=quartile03-quartile01;
            double lowerBound=quartile01-(1.5*interQuartileRange);
            double upperBound=quartile03+(1.5*interQuartileRange);
            System.out.println();
            System.out.println("Variable: "+continuousVariablesNames[i]);
            System.out.println("Lower Bound: "+lowerBound);
            System.out.println("Upper Bound: "+upperBound);
            System.out.println("Inter Quartile Range: "+interQuartileRange);
            //Iterating over rows
            for(int j=0;j<totalRows;j++) {
                if(!table.column(continuousVariablesNames[i]).isMissing(j)) {
                    if(((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j)<lowerBound || ((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j)>upperBound) {
                        System.out.println("Outlier Found");
                        System.out.println("Value: "+((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j));
                        table.column(continuousVariablesNames[i]).setMissing(j);
                    }
                }
            }
        }
        return table;
    }
    public static void main(String args[]) {
        OutlierDetectionUsingIQR outlierDetectionUsingIQR01=new OutlierDetectionUsingIQR(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(outlierDetectionUsingIQR01.getOutputTable());
    }
}