package dataAnalysisAlgorithms;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class SimpleLinearRegression {
    private List<String> predictorVariableNames;
    private List<String> targetVariableNames;
    private List<Table> outputTables;
    private List<Double> intercepts;
    private List<Double> slopes;
    private Table finalTable;
    private Table inputTable;
    SimpleLinearRegression() {
        this.inputTable=null;
        this.finalTable=null;
    }
    public SimpleLinearRegression(Table inputTable) {
        this.inputTable=inputTable;
        this.finalTable=(this.calculateFinalTable());
    }
    public List<String> getPredictorVariableNames() {
        return predictorVariableNames;
    }
    public List<String> getTargetVariableNames() {
        return targetVariableNames;
    }
    public List<Table> getOutputTables() {
        return outputTables;
    }
    public List<Double> getIntercepts() {
        return intercepts;
    }
    public Table getFinalTable() {
        return finalTable;
    }
    public Table getInputTable() {
        return inputTable;
    }
    public List<Double> getSlopes() {
        return slopes;
    }
    public void setPredictorVariableNames(List<String> predictorVariableNames) {
        this.predictorVariableNames=predictorVariableNames;
    }
    public void setTargetVariableNames(List<String> targetVariableNames) {
        this.targetVariableNames=targetVariableNames;
    }
    public void setOutputTables(List<Table> outputTables) {
        this.outputTables=outputTables;
    }
    public void setIntercepts(List<Double> intercepts) {
        this.intercepts=intercepts;
    }
    public void setFinalTable(Table finalTable) {
        this.finalTable=finalTable;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    public void setSlopes(List<Double> slopes) {
        this.slopes=slopes;
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
    public Table calculateFinalTable() {
        //Creating variables
        Table table=this.inputTable;
        Table newTable=table.copy();
        CorrelationMatrix correlationMatrix01=new CorrelationMatrix(table);
        double[][] correlationMatrix=correlationMatrix01.getCorrelationMatrix();
        slopes=new ArrayList<>();
        intercepts=new ArrayList<>();
        outputTables=new ArrayList<>();
        targetVariableNames=new ArrayList<>();
        predictorVariableNames=new ArrayList<>();
        int totalRows=table.rowCount();

        //Storing attributes
        int continuousVariablesCount=getContinuousVariableCount(table);

        //Storing continuous variable name
        String[] continuousVariablesNames=getContinuousVariableNames(table);

        //Iterating through each and every variable
        for(int i=0;i<continuousVariablesCount;i++) {
            if(table.column(continuousVariablesNames[i]).countMissing()!=0) {
                int posi=0;
                double maxi=0;
                for(int j=0;j<continuousVariablesCount;j++) {
                    if(i!=j) {
                        double curr=Math.abs(correlationMatrix[i][j]);
                        if(curr>maxi) {
                            maxi=correlationMatrix[i][j];
                            posi=j;
                        }
                    }
                }
                System.out.println("The variable "+continuousVariablesNames[i]+" has maximum correlation with "+continuousVariablesNames[posi]+" with correlation value "+maxi);

                //Calculating sum required for calculating slope and intercept
                double variRows=0,sumOfX=0,sumOfY=0,sumOfXY=0,sumOfXSquare=0;
                for(int j=0;j<totalRows;j++) {
                    if(!table.column(continuousVariablesNames[i]).isMissing(j) && !table.column(continuousVariablesNames[posi]).isMissing(j)) {
                        variRows+=1;
                        double xValue=0,yValue=0;
                        Column<?> yColumn=table.column(continuousVariablesNames[i]);
                        Column<?> xColumn=table.column(continuousVariablesNames[posi]);
                        String yType=yColumn.type().name();
                        String xType=xColumn.type().name();
                        if(yType.equals("INTEGER")) {
                            yValue=table.intColumn(continuousVariablesNames[i]).getInt(j);
                        } else {
                            yValue=table.doubleColumn(continuousVariablesNames[i]).getDouble(j);
                        }
                        if(xType.equals("INTEGER")) {
                            xValue=table.intColumn(continuousVariablesNames[posi]).getInt(j);
                        } else {
                            xValue=table.doubleColumn(continuousVariablesNames[posi]).getDouble(j);
                        }
                        sumOfX+=xValue;
                        sumOfY+=yValue;
                        sumOfXY+=xValue*yValue;
                        sumOfXSquare+=xValue*xValue;
                    }
                }

                System.out.println("The sum of "+continuousVariablesNames[i]+" is "+sumOfX);
                System.out.println("The sum of "+continuousVariablesNames[posi]+" is "+sumOfY);
                System.out.println("The sum of "+continuousVariablesNames[posi]+"*"+continuousVariablesNames[i]+" is "+sumOfXY);
                System.out.println("The sum of "+continuousVariablesNames[posi]+"*"+continuousVariablesNames[posi]+" is "+sumOfXSquare);

                //Calculating slope and intercept
                double slope=(variRows*sumOfXY-sumOfX*sumOfY)/(variRows*sumOfXSquare-sumOfX*sumOfX);
                double intercept=(sumOfY-slope*sumOfX)/variRows;
                this.slopes.add(slope); this.intercepts.add(intercept);
                this.targetVariableNames.add(continuousVariablesNames[i]);
                this.predictorVariableNames.add(continuousVariablesNames[posi]);
                System.out.println("The linear regression equation for "+continuousVariablesNames[i]+" and "+continuousVariablesNames[posi]+" is "+continuousVariablesNames[i]+"="+slope+"*"+continuousVariablesNames[posi]+"+"+intercept);

                //Predicting missing values
                for(int j=0;j<totalRows;j++) {
                    if(table.column(continuousVariablesNames[i]).isMissing(j) && !table.column(continuousVariablesNames[posi]).isMissing(j)) {
                        double xValue=0,yValue=0;
                        Column<?> xColumn=table.column(continuousVariablesNames[posi]);
                        Column<?> yColumn=table.column(continuousVariablesNames[i]);
                        String xType=xColumn.type().name();
                        String yType=yColumn.type().name();
                        if(xType.equals("INTEGER")) {
                            xValue=table.intColumn(continuousVariablesNames[posi]).getInt(j);
                        } else {
                            xValue=table.doubleColumn(continuousVariablesNames[posi]).getDouble(j);
                        }
                        yValue=slope*xValue+intercept;
                        if(yType.equals("INTEGER")) {
                            table.intColumn(continuousVariablesNames[i]).set(j,(int)yValue);
                            newTable.intColumn(continuousVariablesNames[i]).set(j,(int)yValue);
                        } else {
                            table.doubleColumn(continuousVariablesNames[i]).set(j,yValue);
                            newTable.doubleColumn(continuousVariablesNames[i]).set(j,yValue);
                        }
                        System.out.println("The predicted value of "+continuousVariablesNames[i]+" for row "+j+" is "+yValue);
                    }
                }
                this.outputTables.add(newTable);
            }
        }
        return table;
    }

    public static void main(String args[]) {
        SimpleLinearRegression simpleLinearRegression01=new SimpleLinearRegression(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(simpleLinearRegression01.getFinalTable());
    }
}