package dataAnalysisAlgorithms;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.api.Table;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class TypeConvertor {
    private List<String> columnsToBeConverted;
    private Table outputTable;
    private Table inputTable;
    public List<String> getColumnsToBeConverted() {
        return columnsToBeConverted;
    }
    public Table getOutputTable() {
        return outputTable;
    }
    public Table getInputTable() {
        return inputTable;
    }
    public void setColumnsToBeConverted(List<String> columnsToBeConverted) {
        this.columnsToBeConverted=columnsToBeConverted;
    }
    public void setOutputTable(Table outputTable) {
        this.outputTable=outputTable;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    TypeConvertor() {
        this.columnsToBeConverted=null;
        this.outputTable=null;
        this.inputTable=null;
    }
    TypeConvertor(Table inputTable,List<String> columnsToBeConverted) {
        this.inputTable=inputTable;
        this.columnsToBeConverted=columnsToBeConverted;
        this.outputTable=(this.calculateOutputTable());
    }
    public Table calculateOutputTable() {
        //Creating variables
        Table table=(this.inputTable);
        for(String columnName:(this.columnsToBeConverted)) {
            Column<?> column=table.column(columnName);
            ColumnType columnType=column.type();
            System.out.println(columnName+" "+columnType);
            if(String.valueOf(columnType).equals("DOUBLE")) {
                String value=String.valueOf(table.doubleColumn(columnName).getDouble(0));
                int lastIndex=(-1);
                int size=value.length();
                for(int i=0;i<size;i++) {
                    if((i-3)>=0 && value.charAt(i-3)=='.') {
                        lastIndex=i;
                        break;
                    }
                }
                System.out.println(size+" "+lastIndex+" "+value);
            } else if(String.valueOf(columnType).equals("STRING")) {
                String value=table.stringColumn(columnName).getString(0);
                System.out.println(value);
            }
        }
        return table;
    }
    public static void main(String[] args) {
        Table inputTable=Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/numericData.csv");
        List<String> columnsToBeRemoved=new ArrayList<>();
        System.out.println(inputTable.structure());
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the number of columns to be removed: ");
        int numberOfColumnsToBeRemoved=sc.nextInt();
        System.out.println("Enter the names of columns to be removed: ");
        for(int i=0;i<numberOfColumnsToBeRemoved;i++) {
            columnsToBeRemoved.add(sc.next());
        }
        TypeConvertor typeConvertor01=new TypeConvertor(inputTable,columnsToBeRemoved);
        System.out.println(typeConvertor01.getOutputTable());
    }
}
