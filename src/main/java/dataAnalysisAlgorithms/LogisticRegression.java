package dataAnalysisAlgorithms;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import java.util.*;

public class LogisticRegression {
    private Table inputTable;
    private Table outputTable;
    static HashMap<String,HashMap<String,Integer>> encodingData=new HashMap<>();
    LogisticRegression() {
    }
    LogisticRegression(Table inputTable) {
        this.inputTable=inputTable;
        this.outputTable=(this.calculateOutputTable());
    }
    public Table getInputTable() {
        return this.inputTable;
    }
    public Table getOutputTable() {
        return this.outputTable;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    public void setOutputTable(Table outputTable) {
        this.outputTable=outputTable;
    }
    public static String keyOf(HashMap<String,Integer> map,int value) {
        for(Map.Entry<String,Integer> entry:map.entrySet()) {
            if(Objects.equals(value,entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static Table performOrdinalEncoding(Table table) {
        //Creating variables
        Table newTable=table.copy();
        List<StringColumn> categoricalColumns=new ArrayList<>();
        for(StringColumn column:table.stringColumns()) {
            if(column.countUnique()<=(table.rowCount()/2)) {
                categoricalColumns.add(column);
            }
        }
        for(StringColumn column:categoricalColumns) {
            int count=0;
            HashMap<String,Integer> encoding=new HashMap<>();
            Set<String> uniqueValues=new LinkedHashSet<>();
            for(String value:column) {
                if(value!="") {
                    uniqueValues.add(value);
                }
            }
            for(String value:uniqueValues) {
                encoding.put(value,count);
                count+=1;
            }
            IntColumn newColumn=IntColumn.create(column.name());
            for(int i=0;i<table.rowCount();i++) {
                if(column.getString(i)=="") {
                    newColumn.appendMissing();
                } else {
                    newColumn.append(encoding.get(column.getString(i)));
                }
            }
            encodingData.put(column.name(),encoding);
            newTable.replaceColumn((column.name()),newColumn);
            System.out.println(encoding);
        }
        return newTable;
    }
    private static double[] getSlopeAndIntercept(Table newTable,String target) {
        SimpleLinearRegression simpleLinearRegression01=new SimpleLinearRegression(newTable);
        List<Double> slopes=simpleLinearRegression01.getSlopes();
        List<Double> intercepts=simpleLinearRegression01.getIntercepts();
        List<String> targetColumns=simpleLinearRegression01.getTargetVariableNames();
        int requiredIndex=targetColumns.indexOf(target);
        double[] slopeAndIntercept=new double[]{slopes.get(requiredIndex),intercepts.get(requiredIndex)};
        return slopeAndIntercept;
    }
    public Table calculateOutputTable() {
        //Creating variables
        Table table=this.inputTable;
        Table newTable=performOrdinalEncoding(table);
        CorrelationMatrix correlationMatrix01=new CorrelationMatrix(newTable);
        double[][] correlationMatrix=correlationMatrix01.getCorrelationMatrix();
        SummaryOfTable summaryOfTable01=new SummaryOfTable(newTable);
        Table summary=summaryOfTable01.getSummaryTable();
        int totalColumns=newTable.columnCount();
        int totalRows=newTable.rowCount();
        for(int i=0;i<(correlationMatrix.length);i++) {
            int uniqueCount=Integer.parseInt(summary.column("Unique").getString(i).split("/")[0])-1;
            int missingCount=Integer.parseInt(summary.column("Missing Count").getString(i).split("/")[0]);
            if(summary.column("IsMissing").getString(i).equals("True") && uniqueCount==2) {
                int posi=0;
                double maxi=0;
                for(int j=0;j<(correlationMatrix.length);j++) {
                    if(i!=j) {
                        double curr=Math.abs(correlationMatrix[i][j]);
                        if(curr>maxi) {
                            maxi=correlationMatrix[i][j];
                            posi=j;
                        }
                    }
                }
                String target=newTable.columnNames().get(i);
                String predictor=newTable.columnNames().get(posi);
                System.out.println("The variable "+target+" has maximum correlation with "+predictor+" with correlation value "+maxi);
                double[] slopeAndIntercept=getSlopeAndIntercept(newTable,target);
                System.out.println("The slope is "+slopeAndIntercept[0]+" and the intercept is "+slopeAndIntercept[1]);
                for(int j=0;j<totalRows;j++) {
                    if(newTable.column(target).isMissing(j) && !newTable.column(predictor).isMissing(j)) {
                        double value=0;
                        int predValu=0;
                        Object answer=null;
                        Column<?> xColumn=table.column(predictor);
                        String xType=xColumn.type().name();
                        if(xType.equals("INTEGER")) {
                            value=newTable.intColumn(predictor).getInt(j);
                        } else {
                            value=newTable.doubleColumn(predictor).getDouble(j);
                        }
                        value*=slopeAndIntercept[0];
                        value+=slopeAndIntercept[1];
                        if(value>=0) {
                            double numerator01=1;
                            double denominator01=(1+Math.exp(-value));
                            predValu=(int)(Math.round(numerator01/denominator01));
                        } else {
                            double numerator02=Math.exp(value);
                            double denominator02=(1+Math.exp(value));
                            predValu=(int)(Math.round(numerator02/denominator02));
                        }
                        answer=keyOf(encodingData.get(target),predValu);
                        System.out.println(value+" "+target+" "+answer);
                        table.stringColumn(newTable.columnNames().get(i)).set(j,(String)answer);
                    }
                }
            }
        }
        return table;
    }

    public static void main(String[] args) {
        LogisticRegression logisticRegression01=new LogisticRegression(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(logisticRegression01.getOutputTable());
    }
}
