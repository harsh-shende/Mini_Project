package dataAnalysisAlgorithms;

import java.util.Objects;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class CorrelationMatrix {
    private Table inputTable;
    private double[][] correlationMatrix;
    public CorrelationMatrix() {
        this.inputTable=null;
        this.correlationMatrix=null;
    }
    public CorrelationMatrix(Table inputTable) {
        this.inputTable=inputTable;
        this.correlationMatrix=(this.calculateCorrelationMatrix());
    }
    public Table getInputTable() {
        return inputTable;
    }
    public double[][] getCorrelationMatrix() {
        return correlationMatrix;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    public void setCorrelationMatrix(double[][] correlationMatrix) {
        this.correlationMatrix=correlationMatrix;
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
    public double[][] calculateCorrelationMatrix() {
        //Importing data
        int variRows=0;
        Table table=this.inputTable;
        int totalRows=table.rowCount();

        //Storing attributes
        int continuousVariablesCount=getContinuousVariableCount(table);

        //Storing continuous variable name
        String[] continuousVariablesNames=getContinuousVariableNames(table);

        //Creating array to store stats
        double[] mean=new double[continuousVariablesCount];
        double[] variance=new double[continuousVariablesCount];
        double[] standardDeviation=new double[continuousVariablesCount];
        double[] sumOfSquareOfDeviationFromMean=new double[continuousVariablesCount];
        double[][] deviationFromMean=new double[continuousVariablesCount][totalRows];
        double[][] correlationMatrix=new double[continuousVariablesCount][continuousVariablesCount];

        //Calculating mean and deviation from mean
        for(int i=0;i<continuousVariablesCount;i++) {
            variRows=0;mean[i]=0;
            for(int j=0;j<totalRows;j++) {
                if(!table.column(continuousVariablesNames[i]).isMissing(j)) {
                    Column<?> desiVari=table.column(continuousVariablesNames[i]);
                    String desiType=desiVari.type().name();
                    if(desiType.equals("INTEGER")) {
                        mean[i]+=table.intColumn(continuousVariablesNames[i]).getInt(j);
                    }
                    if(desiType.equals("DOUBLE")) {
                        mean[i]+=table.doubleColumn(continuousVariablesNames[i]).getDouble(j);
                    }
                    variRows+=1;
                }
            }
            mean[i]/=variRows;
            for(int j=0;j<table.rowCount();j++) {
                double temp=0;
                if(!table.column(continuousVariablesNames[i]).isMissing(j)) {
                    Column<?> desiVari=table.column(continuousVariablesNames[i]);
                    String desiType=desiVari.type().name();
                    if(desiType.equals("INTEGER")) {
                        temp=table.intColumn(continuousVariablesNames[i]).getInt(j);
                    }
                    if(desiType.equals("DOUBLE")) {
                        temp=table.doubleColumn(continuousVariablesNames[i]).getDouble(j);
                    }
                    deviationFromMean[i][j]=mean[i]-temp;
                    variance[i]+=Math.pow(deviationFromMean[i][j],2);
                }
            }
            sumOfSquareOfDeviationFromMean[i]=variance[i];
            variance[i]/=(continuousVariablesCount-1);
            standardDeviation[i]=Math.sqrt(variance[i]);
        }

        //Calculating cross product of deviation
        for(int i=0;i<continuousVariablesCount;i++) {
            for(int j=0;j<continuousVariablesCount;j++) {
                for(int k=0;k<table.rowCount();k++) {
                    correlationMatrix[i][j]+=deviationFromMean[i][k]*deviationFromMean[j][k];
                }
            }
        }

        //Calculating and displaying correlation matrix
        for(int i=0;i<continuousVariablesCount;i++) {
            for(int j=0;j<continuousVariablesCount;j++) {
                correlationMatrix[i][j]/=Math.sqrt(sumOfSquareOfDeviationFromMean[i]*sumOfSquareOfDeviationFromMean[j]);
            }
        }
        return correlationMatrix;
    }
    public void printCorrelationMatrix() {
        for(int i=0;i<(correlationMatrix.length);i++) {
            for(int j=0;j<(correlationMatrix[i].length);j++) {
                System.out.print(String.format("%.2f    ",(this.correlationMatrix[i][j])));
            }
            System.out.println();
        }
    }
    public static void main(String args[]) {
        long startTime01=System.currentTimeMillis();
        CorrelationMatrix correlationMatrix01=new CorrelationMatrix(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        correlationMatrix01.printCorrelationMatrix();
        long endTime01=System.currentTimeMillis();
        long executionTime01=endTime01-startTime01;
        System.out.println("Execution time: "+executionTime01+" milliseconds");
    }
}