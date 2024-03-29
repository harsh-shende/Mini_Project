package dataAnalysisAlgorithms;

import java.util.Scanner;
import tech.tablesaw.api.Table;

public class EliminateRowsAndColumns {
    private Table inputTable;
    private Table outputTable;
    public EliminateRowsAndColumns() {
        this.inputTable=null;
        this.outputTable=null;
    }
    public EliminateRowsAndColumns(Table inputTable) {
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
        //Importing data
        Table table=this.inputTable;
        int totalRows=table.rowCount();

        //Storing attributes
        String[] attr=table.columnNames().toArray(new String[0]);
        Scanner sc=new Scanner(System.in);
        int size=attr.length;

        //Taking input from user
        System.out.println("********************");
        System.out.println("1) Automatically Eliminate Columns with more than 60% Missing Values");
        System.out.println("2) Automatically Eliminate Rows with more than 60% Missing Values");
        System.out.println("3) Manually Eliminate Columns");
        System.out.print("ENTER YOUR CHOICE: ");
        int choice=sc.nextInt();
        switch(choice) {
            case 1://Automatically eliminate columns with more than 60% missing values
                for(int i=0;i<size;i++) {
                    int count=0;
                    for(int j=0;j<totalRows;j++) {
                        if(table.column(attr[i]).isMissing(j)) {
                            count+=1;
                        }
                    }
                    if(count>=(0.6*totalRows)) {
                        table=table.removeColumns(attr[i]);
                    }
                }
                break;
            case 2://Automatically eliminate rows with more than 60% missing values
                for(int i=0;i<totalRows;i++) {
                    int count=0;
                    for(int j=0;j<size;j++) {
                        if(table.column(attr[j]).isMissing(i)) {
                            count+=1;
                        }
                    }
                    if(count>=(0.6*size)) {
                        table=table.dropRows(i);
                        i-=1;
                        totalRows-=1;
                    }
                }
                break;
            case 3://Manually eliminate columns
                System.out.print("Enter the number of columns you want to eliminate: ");
                int num=sc.nextInt();
                String[] attrToBeRemoved=new String[num];
                System.out.println("Enter the names of column you want to eliminate: ");
                for(int i=0;i<num;i++) {
                    attrToBeRemoved[i]=sc.next();
                }
                table=table.removeColumns(attrToBeRemoved);
                break;
            default://Invalid Input
                System.out.println("Invalid Input!");
                break;
        }
        return table;
    }
    public static void main(String[] args) {
        EliminateRowsAndColumns eliminateRowsAndColumns01=new EliminateRowsAndColumns(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(eliminateRowsAndColumns01.getOutputTable());
    }
}