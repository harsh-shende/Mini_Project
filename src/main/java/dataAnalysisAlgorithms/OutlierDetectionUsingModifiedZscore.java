package dataAnalysisAlgorithms;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import java.util.Objects;

public class OutlierDetectionUsingModifiedZscore {
    private Table inputTable;
    private Table outputTable;
    public OutlierDetectionUsingModifiedZscore() {
        this.inputTable=null;
        this.outputTable=null;
    }
    public OutlierDetectionUsingModifiedZscore(Table inputTable) {
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
            int tempRows=0;
            double median=0,meanAbsoluteDeviation=0;
            Column<?> desiredVariable=table.column(continuousVariablesNames[i]);
            String desiredType=desiredVariable.type().name();
            System.out.println();
            System.out.println("Variable: "+continuousVariablesNames[i]);
            if(desiredType.equals("INTEGER")) {
                IntColumn ic=(IntColumn)table.column(continuousVariablesNames[i]);
                median=((IntColumn)table.column(continuousVariablesNames[i])).median();
            } else {
                DoubleColumn ic=(DoubleColumn)table.column(continuousVariablesNames[i]);
                median=((DoubleColumn)table.column(continuousVariablesNames[i])).median();
            }

            //Iterating over rows
            for(int j=0;j<totalRows;j++) {
                if(!table.column(continuousVariablesNames[i]).isMissing(j)) {
                    tempRows+=1;
                    meanAbsoluteDeviation+=Math.abs((((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j))-median);
                }
            }
            meanAbsoluteDeviation/=tempRows;

            //Iterating over rows
            for(int j=0;j<totalRows;j++) {
                if(!table.column(continuousVariablesNames[i]).isMissing(j)) {
                    double modifiedZScore=0.6745;
                    if(desiredType.equals("INTEGER")) {
                        modifiedZScore*=(((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j)-median)/meanAbsoluteDeviation;
                    } else {
                        modifiedZScore*=(((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j)-median)/meanAbsoluteDeviation;
                    }
                    if(modifiedZScore<(-3) || modifiedZScore>(3)) {
                        System.out.println("Outlier Found");
                        System.out.println("Value: "+((NumberColumn<?,?>)table.column(continuousVariablesNames[i])).getDouble(j));
                        System.out.println("Modified Z-Score: "+modifiedZScore);
                        table.column(continuousVariablesNames[i]).setMissing(j);
                    }
                }
            }
        }
        return table;
    }
    public static void main(String args[]) {
        OutlierDetectionUsingModifiedZscore outlierDetectionUsingModifiedZscore01=new OutlierDetectionUsingModifiedZscore(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(outlierDetectionUsingModifiedZscore01.getOutputTable());
    }
}
