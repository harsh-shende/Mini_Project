package dataAnalysisAlgorithms;

import tech.tablesaw.api.Table;
import java.util.Scanner;

public class eliminatingRowsAndColumns {
    public static void main(String args[]) {
        //Creating variables
        int variRows=0;

        //Importing data
        Table table=Table.read().csv("C:\\Users\\Asus\\OneDrive\\Documents\\Data Science\\Datasets\\sample_corr.csv");
        Table structureOfTable=table.structure();
        Table summaryOfTable=table.summary();
        int totalRows=table.rowCount();

        //Displaying content and structure
        /*System.out.println(table);
        System.out.println();
        System.out.println(structureOfTable);
        System.out.println();
        System.out.println(summaryOfTable);
        System.out.println();*/

        //Storing attributes
        String[] attr=table.columnNames().toArray(new String[0]);
        Scanner sc=new Scanner(System.in);

        //Taking input from user
        System.out.println("********************");
        System.out.println("1) Automatically Eliminate Columns with more than 60% Missing Values");
        System.out.println("2) Automatically Eliminate Rows with more than 60% Missing Values");
        System.out.println("3) Manually Eliminate Columns");
        System.out.print("ENTER YOUR CHOICE: ");
        int choice=sc.nextInt();
        switch(choice) {
            case 1://Automatically eliminate columns with more than 60% missing values
                for(int i=0;i<attr.length;i++) {
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
                System.out.println(table);
                break;
            case 2://Automatically eliminate rows with more than 60% missing values
                for(int i=0;i<totalRows;i++) {
                    int count=0;
                    for(int j=0;j<attr.length;j++) {
                        if(table.column(attr[j]).isMissing(i)) {
                            count+=1;
                        }
                    }
                    if(count>=(0.6*attr.length)) {
                        table=table.dropRows(i);
                        totalRows-=1;
                    }
                }
                System.out.println(table);
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
                System.out.println(table);
                break;
            default://Invalid Input
                System.out.println("Invalid Input!");
                break;
        }
    }
}