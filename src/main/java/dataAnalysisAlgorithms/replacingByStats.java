package dataAnalysisAlgorithms;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class replacingByStats {
    public static void main(String args[]) {
        //Importing data
        Scanner sc=new Scanner(System.in);
        Table table=Table.read().csv("C:\\Users\\Asus\\OneDrive\\Documents\\Data Science\\Datasets\\student-mat.csv");
        NumericColumn<?>[] numericColumn=table.numericColumns().toArray(new NumericColumn[0]);
        for(int i=0;i<numericColumn.length;i++) {
            System.out.println(numericColumn[i]);
        }

        //Taking input
        System.out.println("********************");
        System.out.println("1) Replace by Mean");
        System.out.println("2) Replace by Mode");
        System.out.println("3) Replace by Median");
        System.out.println("4) Replace by Minimum");
        System.out.println("5) Replace by Maximum");
        System.out.println("6) Replace by Global Constant");
        System.out.print("ENTER YOUR CHOICE: ");
        int choice=sc.nextInt();
        switch(choice) {
            case 01://Replace by Mean
                List<Double> mean=new ArrayList<>();
                for(NumericColumn<?> nc:numericColumn) {
                    mean.add(nc.mean());
                    System.out.println(nc.name()+"    "+nc.mean());
                }
                break;
            case 02://Replace by Mode
                List<Double> mode=new ArrayList<>();
                for(NumericColumn<?> nc:numericColumn) {

                }
                break;
            case 03://Replace by Median
                List<Double> median=new ArrayList<>();
                for(NumericColumn<?> nc:numericColumn) {
                    median.add(nc.median());
                    System.out.println(nc.name()+"    "+nc.median());
                }
                break;
            case 04://Replace by Minimum
                List<Double> mini=new ArrayList<>();
                for(NumericColumn<?> nc:numericColumn) {
                    mini.add(nc.min());
                    System.out.println(nc.name()+"    "+nc.min());
                }
                break;
            case 05://Replace by Maximum
                List<Double> maxi=new ArrayList<>();
                for(NumericColumn<?> nc:numericColumn) {
                    maxi.add(nc.max());
                    System.out.println(nc.name()+"    "+nc.max());
                }
                break;
            case 06://Replace by Global Constant
                for(NumericColumn<?> nc:numericColumn) {
                    System.out.print("Enter global constant for "+nc.name()+": ");
                    double gc=sc.nextDouble();

                }
                break;
            default://Invalid Input
                System.out.println("Invalid Input!");
                break;
        }
    }
}