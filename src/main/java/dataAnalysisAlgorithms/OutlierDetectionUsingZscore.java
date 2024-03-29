package dataAnalysisAlgorithms;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import java.util.Objects;

public class OutlierDetectionUsingZscore {
    private Table inputTable;
    private Table outputTable;
    public OutlierDetectionUsingZscore() {
        this.inputTable=null;
        this.outputTable=null;
    }
    public OutlierDetectionUsingZscore(Table inputTable) {
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
            double mean=0,standardDeviation=0;
            Column<?> desiredVariable=table.column(continuousVariablesNames[i]);
            String desiredType=desiredVariable.type().name();
            System.out.println();
            System.out.println("Variable: "+continuousVariablesNames[i]);
            if(desiredType.equals("INTEGER")) {
                IntColumn ic=(IntColumn)table.column(continuousVariablesNames[i]);
                mean=((IntColumn)table.column(continuousVariablesNames[i])).mean();
                standardDeviation=((IntColumn)table.column(continuousVariablesNames[i])).standardDeviation();
            } else {
                DoubleColumn ic=(DoubleColumn)table.column(continuousVariablesNames[i]);
                mean=((DoubleColumn)table.column(continuousVariablesNames[i])).mean();
                standardDeviation=((DoubleColumn)table.column(continuousVariablesNames[i])).standardDeviation();
            }

            //Iterating over rows
            for(int j=0;j<totalRows;j++) {
                if(!table.column(continuousVariablesNames[i]).isMissing(j)) {
                    double zScore=(((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j)-mean)/standardDeviation;
                    if(zScore<(-3) || zScore>(3)) {
                        System.out.println("Outlier Found");
                        System.out.println("Value: "+((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j));
                        System.out.println("Z-Score: "+zScore);
                        table.column(continuousVariablesNames[i]).setMissing(j);
                    }
                }
            }
        }
        return table;
    }
    public static void main(String args[]) {
        OutlierDetectionUsingZscore outlierDetectionUsingZscore01=new OutlierDetectionUsingZscore(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(outlierDetectionUsingZscore01.getOutputTable());
    }
}
