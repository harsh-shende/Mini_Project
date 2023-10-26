package dataAnalysisAlgorithms;

import java.util.Objects;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

public class outlierDetectionUsingIQR {
    public static void main(String args[]) {
        //Importing data
        Table table=Table.read().csv("C:\\Users\\Asus\\OneDrive\\Documents\\Data Science\\Datasets\\student-mat.csv");
        Table structureOfTable=table.structure();
        Table summaryOfTable=table.summary();
        int totalRows=table.rowCount();

        System.out.println(summaryOfTable);

        //Storing attributes
        String[] attr=table.columnNames().toArray(new String[0]);
        int contVariCount=structureOfTable.stringColumn(2).isEqualTo("INTEGER").size();
        contVariCount+=structureOfTable.stringColumn(2).isEqualTo("DOUBLE").size();

        //Storing continuous variable name
        String[] contVariNames=new String[contVariCount];
        for(int i=0,j=0;i<table.columnCount() && j<contVariCount;i++) {
            if(Objects.equals(structureOfTable.column(2).getString(i),"INTEGER")) {
                contVariNames[j]=structureOfTable.column(1).getString(i);
                j+=1;
            }
            if(Objects.equals(structureOfTable.column(2).getString(i),"DOUBLE")) {
                contVariNames[j]=structureOfTable.column(1).getString(i);
                j+=1;
            }
        }

        //Iterating over continuous variables
        for(int i=0;i<contVariCount;i++) {
            double q1=0,q3=0;
            Column<?> desiVari=table.column(contVariNames[i]);
            String desiType=desiVari.type().name();
            if(desiType.equals("INTEGER")) {
                IntColumn ic=(IntColumn)table.column(contVariNames[i]);
                ic.sortAscending();
                q1=((IntColumn)table.column(contVariNames[i])).quartile1();
                q3=((IntColumn)table.column(contVariNames[i])).quartile3();
            } else {
                DoubleColumn ic=(DoubleColumn)table.column(contVariNames[i]);
                ic.sortAscending();
                q1=((DoubleColumn)table.column(contVariNames[i])).quartile1();
                q3=((DoubleColumn)table.column(contVariNames[i])).quartile3();
            }
            double iqr=q3-q1;
            double lowerBound=q1-(1.5*iqr);
            double upperBound=q3+(1.5*iqr);

            //Iterating over rows
            for(int j=0;j<totalRows;j++) {
                if(!table.column(contVariNames[i]).isMissing(j)) {
                    if(((NumberColumn<?,?>)table.column(contVariNames[i])).getDouble(j)<lowerBound || ((NumberColumn<?,?>)table.column(contVariNames[i])).getDouble(j)>upperBound) {
                        table=table.dropRows(j);
                        totalRows-=1;
                    }
                }
            }
        }
        System.out.println(table.summary());
        table.write().toFile("C:\\Users\\Asus\\OneDrive\\Desktop\\newFile.csv");
    }
}