package dataAnalysisAlgorithms;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import tech.tablesaw.api.*;

public class SummaryOfTable {
    private Table inputTable;
    private Table summaryTable;
    SummaryOfTable() {
        this.inputTable=null;
        this.summaryTable=null;
    }
    public SummaryOfTable(Table inputTable) {
        this.inputTable=inputTable;
        this.summaryTable=(this.calculateSummaryTable());
    }
    public Table getInputTable() {
        return inputTable;
    }
    public Table getSummaryTable() {
        return summaryTable;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    public void setSummaryTable(Table summaryTable) {
        this.summaryTable=summaryTable;
    }
    public Table calculateSummaryTable() {
        Table table=this.inputTable.structure();
        int row_count=this.inputTable.rowCount();
        List<String> columnNames=this.inputTable.columnNames();
        StringColumn nanCount=StringColumn.create("Missing Count");
        StringColumn isMissing=StringColumn.create("IsMissing");
        StringColumn mean=StringColumn.create("Mean");
        StringColumn median=StringColumn.create("Median (Q2)");
        StringColumn mode_column=StringColumn.create("Mode");
        StringColumn unique=StringColumn.create("Unique");
        StringColumn range=StringColumn.create("Range");
        StringColumn mini=StringColumn.create("Minimum");
        StringColumn maxi=StringColumn.create("Maximum");
        StringColumn quartile1=StringColumn.create("Quartile 1 (Q1)");
        StringColumn quartile3=StringColumn.create("Quartile 3 (Q3)");
        StringColumn stdDevi=StringColumn.create("Std. Deviation");
        StringColumn variance=StringColumn.create("Variance");

        NumericColumn<?>[] numericColumns=this.inputTable.numericColumns().toArray(new NumericColumn[0]);
        for(int i=0;i<(columnNames.size());i++)  {
            int n=this.inputTable.column(columnNames.get(i)).countMissing();
            nanCount.append(n+"/"+row_count);
            if(n>0) {
                isMissing.append("True");
            } else {
                isMissing.append("False");
            }
            unique.append(this.inputTable.column(columnNames.get(i)).countUnique()+"/"+row_count);
            mean.append("Type Mismatch");
            median.append("Type Mismatch");
            mode_column.append("Type Mismatch");
            range.append("Type Mismatch");
            mini.append("Type Mismatch");
            maxi.append("Type Mismatch");
            quartile1.append("Type Mismatch");
            quartile3.append("Type Mismatch");
            stdDevi.append("Type Mismatch");
            variance.append("Type Mismatch");
        }

        for(NumericColumn<?> column:numericColumns) {
            mean.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.mean()));
            median.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.median()));
            range.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.range()));
            mini.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.min()));
            maxi.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.max()));
            quartile1.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.quartile1()));
            quartile3.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.quartile3()));
            stdDevi.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.standardDeviation()));
            variance.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f",column.variance()));
            Map<Object,Integer> valueCounts=new HashMap<>();
            int maxCount=0;
            if(column.type().toString().equals("INTEGER")) {
                int mode1=0;
                for(Object value:column) {
                    int a=(int)value;
                    if(a!=-2147483648) {
                        int count=valueCounts.getOrDefault(value,0)+1;
                        valueCounts.put(value,count);
                        if(count>maxCount) {
                            maxCount=count;
                            mode1=(int)value;
                        }
                    }
                }
                mode_column.set(table.column("Column Name").indexOf(column.name()), String.valueOf(mode1));
            } else {
                double mode2=0;
                for(Object value:column) {
                    mode2=0;
                    double a=(double) value;
                    if(a!=(-2147483648)) {
                        int count=valueCounts.getOrDefault(value,0)+1;
                        valueCounts.put(value,count);
                        if(count>maxCount) {
                            maxCount=count;
                            mode2=(double)value;
                        }
                    }
                }
                mode_column.set(table.column("Column Name").indexOf(column.name()),String.format("%.3f", mode2));
            }
        }
        table.addColumns(isMissing,unique,nanCount,mean,median,mode_column,mini,maxi,range,quartile1,quartile3,stdDevi,variance);
        return table;
    }
    public static void main(String[] args) {
        SummaryOfTable sot=new SummaryOfTable(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(sot.getSummaryTable());
    }
}
