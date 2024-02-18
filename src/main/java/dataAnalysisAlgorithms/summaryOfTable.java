package dataAnalysisAlgorithms;


import tech.tablesaw.api.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class summaryOfTable {
    public Table getSummary(Table data) {
        Table table=data.structure();
        int row_count=data.rowCount();
        List<String> columnNames=data.columnNames();
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

        NumericColumn<?>[] numericColumns=data.numericColumns().toArray(new NumericColumn[0]);
        for(int i=0;i<(columnNames.size());i++)  {
            int n=data.column(columnNames.get(i)).countMissing();
            nanCount.append(n+"/"+row_count);
            if(n>0) {
                isMissing.append("True");
            } else {
                isMissing.append("False");
            }
            unique.append(data.column(columnNames.get(i)).countUnique()+"/"+row_count);
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
        Table data=Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/numericData.csv");
        summaryOfTable sot=new summaryOfTable();
        System.out.println(sot.getSummary(data));
    }
}
