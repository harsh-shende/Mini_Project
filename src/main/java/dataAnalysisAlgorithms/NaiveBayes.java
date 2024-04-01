package dataAnalysisAlgorithms;

import java.util.List;
import java.util.Scanner;
import tech.tablesaw.api.*;
import java.util.ArrayList;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public class NaiveBayes {
    private Table inputTable;
    private Table outputTable;
    NaiveBayes() {
        this.inputTable=null;
        this.outputTable=null;
    }
    NaiveBayes(Table inputTable) {
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
    public Table calculateOutputTable() {
        //Creating variables
        Object value;
        Table temporary=null;
        Table originalTable=this.inputTable;
        int originalTableRows=originalTable.rowCount();
        SummaryOfTable summaryOfTable01=new SummaryOfTable(originalTable);
        Table summaryTable=summaryOfTable01.getSummaryTable();
        int summaryTableRows=summaryTable.rowCount();

        //Iterating through the summary table
        for(int i=0;i<summaryTableRows;i++) {
            String targetVariable=summaryTable.stringColumn("Column Name").getString(i);
            String missingFlag=summaryTable.stringColumn("isMissing").getString(i);

            //Checking if the target variable has missing values
            if(missingFlag.equals("True")) {
                //Creating variables
                Scanner sc=new Scanner(System.in);
                List<Table> tempTables=new ArrayList<>();
                List<Object> possibleValues=new ArrayList<>();
                List<Double> probabilityValues=new ArrayList<>();
                List<Integer> missingValuesIndex=new ArrayList<>();

                //Determining the type of the target variable
                Selection selection=originalTable.column(targetVariable).isMissing();
                Table missingValuesTable=originalTable.where(selection);
                Column<?> targetColumn=originalTable.column(targetVariable);
                String targetColumnType=targetColumn.type().name();
                int missingValuesCount=missingValuesTable.rowCount();
                System.out.println("The target variable is: "+targetVariable);
                System.out.println("The type of the target variable is: "+targetColumnType);
                System.out.println("The number of missing values in the target variable is: "+missingValuesCount);

                //Extracting the possible values of the target variable
                if(targetColumnType.equals("INTEGER")) {
                    IntColumn targetIntColumn=originalTable.intColumn(targetVariable);
                    for(int j=0;j<originalTableRows;j++) {
                        value=targetIntColumn.getInt(j);
                        if(value!=null && !possibleValues.contains(value)) {
                            possibleValues.add(value);
                            tempTables.add(originalTable.where(originalTable.intColumn(targetVariable).isEqualTo((int)value)));
                        }
                    }
                }  else if(targetColumnType.equals("DOUBLE")) {
                    DoubleColumn targetDoubleColumn=originalTable.doubleColumn(targetVariable);
                    for(int j=0;j<originalTableRows;j++) {
                        value=targetDoubleColumn.getDouble(j);
                        if(value!=null && !possibleValues.contains(value)) {
                            possibleValues.add(value);
                            tempTables.add(originalTable.where(originalTable.doubleColumn(targetVariable).isEqualTo((double)value)));
                        }
                    }
                }  else if(targetColumnType.equals("FLOAT")) {
                    FloatColumn targetFloatColumn=originalTable.floatColumn(targetVariable);
                    for(int j=0;j<originalTableRows;j++) {
                        value=targetFloatColumn.getFloat(j);
                        if(value!=null && !possibleValues.contains(value)) {
                            possibleValues.add(value);
                            tempTables.add(originalTable.where(originalTable.floatColumn(targetVariable).isEqualTo((float)value)));
                        }
                    }
                }  else if(targetColumnType.equals("LONG")) {
                    LongColumn targetLongColumn=originalTable.longColumn(targetVariable);
                    for(int j=0;j<originalTableRows;j++) {
                        value=targetLongColumn.getLong(j);
                        if(value!=null && !possibleValues.contains(value)) {
                            possibleValues.add(value);
                            tempTables.add(originalTable.where(originalTable.longColumn(targetVariable).isEqualTo((long)value)));
                        }
                    }
                } else if(targetColumnType.equals("STRING")) {
                    StringColumn targetStringColumn=originalTable.stringColumn(targetVariable);
                    for(int j=0;j<originalTableRows;j++) {
                        value=targetStringColumn.getString(j);
                        if(value!="" && !possibleValues.contains(value)) {
                            possibleValues.add(value);
                            tempTables.add(originalTable.where(originalTable.stringColumn(targetVariable).isEqualTo((String)value)));
                        }
                    }
                }
                System.out.println("The number of possible values of the target variable are: "+possibleValues.size());

                //Extracting the missing values index
                for(int j=0;j<originalTableRows;j++) {
                    if(originalTable.column(targetVariable).isMissing(j)) {
                        missingValuesIndex.add(j);
                    }
                }

                //Inputting the info of predictor variables
                System.out.println("Enter the number of predictor variables: ");
                int predictorVariablesCount=sc.nextInt();
                List<Double> predictorRows=new ArrayList<>();
                List<String> predictorVariables=new ArrayList<>();
                System.out.println("Enter the names of the predictor variables: ");
                for(int j=0;j<predictorVariablesCount;j++) {
                    predictorVariables.add(sc.next());
                }

                //Iterating through the rows of the missing values table
                for(int j=0;j<missingValuesCount;j++) {
                    //Creating variables
                    double maximumProbability=0;
                    Object maximumProbabilityValue=null;
                    int possibleValuesCount=possibleValues.size();

                    //Iterating through the possible values of the target variable
                    for(int k=0;k<possibleValuesCount;k++) {
                        //Creating variables
                        double probability=1;
                        int tempTableRows=tempTables.get(k).rowCount();

                        //Iterating through the predictor variables
                        for(int l=0;l<(predictorVariablesCount-1);l++) {
                            //Creating variables
                            double tempProbability=1;
                            String predictorVariable=predictorVariables.get(l);
                            Column<?> predictorColumn=tempTables.get(k).column(predictorVariable);
                            String predictorColumnType=predictorColumn.type().name();

                            //Extracting the value of the predictor variable
                            if(predictorColumnType.equals("INTEGER")) {
                                IntColumn predictorIntColumn=tempTables.get(k).intColumn(predictorVariable);
                                int predictorValue=predictorIntColumn.getInt(j);
                                Table temporaryTable=originalTable.where(originalTable.intColumn(predictorVariable).isEqualTo((int)predictorValue));
                                tempProbability=(double)(temporaryTable.rowCount());
                                tempProbability/=(double)(tempTableRows);
                            } else if(predictorColumnType.equals("DOUBLE")) {
                                DoubleColumn predictorDoubleColumn=tempTables.get(k).doubleColumn(predictorVariable);
                                double predictorValue=predictorDoubleColumn.getDouble(j);
                                Table temporaryTable=originalTable.where(originalTable.doubleColumn(predictorVariable).isEqualTo((double)predictorValue));
                                tempProbability=(double)(temporaryTable.rowCount());
                                tempProbability/=(double)(tempTableRows);
                            } else if(predictorColumnType.equals("FLOAT")) {
                                FloatColumn predictorFloatColumn=tempTables.get(k).floatColumn(predictorVariable);
                                float predictorValue=predictorFloatColumn.getFloat(j);
                                Table temporaryTable=originalTable.where(originalTable.floatColumn(predictorVariable).isEqualTo((float)predictorValue));
                                tempProbability=(double)(temporaryTable.rowCount());
                                tempProbability/=(double)(tempTableRows);
                            } else if(predictorColumnType.equals("LONG")) {
                                LongColumn predictorLongColumn=tempTables.get(k).longColumn(predictorVariable);
                                long predictorValue=predictorLongColumn.getLong(j);
                                Table temporaryTable=originalTable.where(originalTable.longColumn(predictorVariable).isEqualTo((long)predictorValue));
                                tempProbability=(double)(temporaryTable.rowCount());
                                tempProbability/=(double)(tempTableRows);
                            } else if(predictorColumnType.equals("STRING")) {
                                StringColumn predictorStringColumn=tempTables.get(k).stringColumn(predictorVariable);
                                String predictorValue=predictorStringColumn.getString(j);
                                Table temporaryTable=originalTable.where(originalTable.stringColumn(predictorVariable).isEqualTo((String)predictorValue));
                                tempProbability=(double)(temporaryTable.rowCount());
                                tempProbability/=(double)(tempTableRows);
                            } else {
                                System.out.println("Invalid data type");
                                return null;
                            }
                            probability*=tempProbability;
                        }
                        probability*=tempTableRows;
                        probability/=originalTableRows;
                        if(probability>=maximumProbability) {
                            maximumProbability=probability;
                            maximumProbabilityValue=possibleValues.get(k);
                        }
                    }
                    switch(targetColumnType) {
                        case "INTEGER" ->
                                originalTable.intColumn(targetVariable).set((int)(missingValuesIndex.get(j)),(int)(maximumProbabilityValue));
                        case "DOUBLE" ->
                                originalTable.doubleColumn(targetVariable).set((int)(missingValuesIndex.get(j)),(double)(maximumProbabilityValue));
                        case "FLOAT" ->
                                originalTable.floatColumn(targetVariable).set((int)(missingValuesIndex.get(j)),(float)(maximumProbabilityValue));
                        case "LONG" ->
                                originalTable.longColumn(targetVariable).set((int)(missingValuesIndex.get(j)),(long)(maximumProbabilityValue));
                        case "STRING" ->
                                originalTable.stringColumn(targetVariable).set((int)(missingValuesIndex.get(j)),(String)(maximumProbabilityValue));
                    }
                    System.out.println("The predicted value of the target variable is: "+maximumProbabilityValue+" with a probability of: "+maximumProbability);
                }
            }
        }
        originalTable.write().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/newFile.csv");
        return originalTable;
    }
    public static void main(String[] args) {
        NaiveBayes naiveBayes01=new NaiveBayes(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/studentMatData.csv"));
        //System.out.println(naiveBayes01.getOutputTable());
    }
}