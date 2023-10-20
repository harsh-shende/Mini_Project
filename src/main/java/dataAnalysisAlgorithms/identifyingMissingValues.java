package dataAnalysisAlgorithms;

import java.util.HashMap;
import tech.tablesaw.api.Table;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tech.tablesaw.selection.Selection;

public class identifyingMissingValues extends naiveBayes {
    public static void main(String args[]) {
        long startTime01=System.currentTimeMillis();
        //Creating Variables
        int i=0;
        String regex="\\[([^\\]]+)\\]";
        Pattern pattern=Pattern.compile(regex);
        HashMap<String,Table> hm=new HashMap<>();

        //Importing data
        Table table=Table.read().csv("C:\\Users\\Asus\\OneDrive\\Documents\\Data Science\\student-mat.csv");
        Table structureOfTable=table.structure();

        //Storing attributes
        Object[] missingValues=new Object[table.columnCount()];
        String[] attr=table.columnNames().toArray(new String[0]);

        //Displaying content and structure
        System.out.println(table);
        System.out.println();
        System.out.println(structureOfTable);

        //Number of missing values
        Table missingTable=table.missingValueCounts();
        System.out.println(missingTable);
        System.out.println();
        System.out.println(missingTable.structure());

        //Iterate over columns and create separate tables for each missing attribute
        for(String columnName:missingTable.columnNames()) {
            double missingCount=missingTable.doubleColumn(columnName).getDouble(0);
            missingValues[i]=missingCount;
            if(missingCount>0) {
                Matcher matcher=pattern.matcher(columnName);
                if(matcher.find()) {
                    columnName=matcher.group(1);
                } else {
                    System.out.println("Pattern not found in the input string.");
                }
                Selection missingRows=table.column(columnName).isMissing();
                Table missingDataTable=table.where(missingRows);
                hm.put(columnName,missingDataTable);
            }
            i+=1;
        }

        //Displaying Missing Value Table
        for(Object s:hm.keySet()) {
            System.out.println();
            fillMissingValues(table,hm.get(s),String.valueOf(s));
        }
        long endTime01=System.currentTimeMillis();
        long executionTime01=endTime01-startTime01;
        System.out.println();
        System.out.println("Execution time: "+executionTime01+" milliseconds");
    }
}