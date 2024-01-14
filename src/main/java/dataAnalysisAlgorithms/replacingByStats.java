package dataAnalysisAlgorithms;

import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import java.util.*;

public class replacingByStats {
    public static void replaceByGlobalConstant(Table table,String[] contVariNames) {
        //Creating variables
        Scanner sc=new Scanner(System.in);

        //Iterating over continuous variables
        for(int i=0;i<contVariNames.length;i++) {
            double globCons=0;
            if(table.column(contVariNames[i]).countMissing()!=0) {
                System.out.print("Enter Global Constant for "+contVariNames[i]+": ");
                globCons=sc.nextDouble();
                Column<?> desiVari01=table.column(contVariNames[i]);
                String desiType=desiVari01.type().name();
                if(desiType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(contVariNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)globCons);
                    table.replaceColumn(contVariNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(contVariNames[i]);
                    Column<Double> ci=ic.setMissingTo(globCons);
                    table.replaceColumn(contVariNames[i],ci);
                }
            }
        }
    }
    public static void replaceByMaximum(Table table,String[] contVariNames) {
        //Iterating over continuous variables
        for(int i=0;i<contVariNames.length;i++) {
            double desiMaxi=0;
            if(table.column(contVariNames[i]).countMissing()!=0) {
                NumericColumn<?> desiVari=table.nCol(contVariNames[i]);
                desiMaxi=desiVari.max();
                System.out.println(desiMaxi);
                Column<?> desiVari01=table.column(contVariNames[i]);
                String desiType=desiVari01.type().name();
                if(desiType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(contVariNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiMaxi);
                    table.replaceColumn(contVariNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(contVariNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiMaxi);
                    table.replaceColumn(contVariNames[i],ci);
                }
            }
        }
    }
    public static void replaceByMinimum(Table table,String[] contVariNames) {
        //Iterating over continuous variables
        for(int i=0;i<contVariNames.length;i++) {
            double desiMini=0;
            if(table.column(contVariNames[i]).countMissing()!=0) {
                NumericColumn<?> desiVari=table.nCol(contVariNames[i]);
                desiMini=desiVari.min();
                System.out.println(desiMini);
                Column<?> desiVari01=table.column(contVariNames[i]);
                String desiType=desiVari01.type().name();
                if(desiType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(contVariNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiMini);
                    table.replaceColumn(contVariNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(contVariNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiMini);
                    table.replaceColumn(contVariNames[i],ci);
                }
            }
        }
    }
    public static void replaceByMedian(Table table,String[] contVariNames) {
        //Iterating over continuous variables
        for(int i=0;i<contVariNames.length;i++) {
            double desiMedi=0;
            if(table.column(contVariNames[i]).countMissing()!=0) {
                NumericColumn<?> desiVari=table.nCol(contVariNames[i]);
                desiMedi=desiVari.median();
                System.out.println(desiMedi);
                Column<?> desiVari01=table.column(contVariNames[i]);
                String desiType=desiVari01.type().name();
                if(desiType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(contVariNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiMedi);
                    table.replaceColumn(contVariNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(contVariNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiMedi);
                    table.replaceColumn(contVariNames[i],ci);
                }
            }
        }
    }
    public static void replaceByMode(Table table,String[] cateVariNames,String[] contVariNames) {
        //Creating Variables
        int totaRows=table.rowCount();
        int contVariCount=contVariNames.length;
        int cateVariCount=cateVariNames.length;

        for(int i=0;i<contVariCount;i++) {
            long maxiFreq=0;
            if(table.column(contVariNames[i]).countMissing()!=0) {
                Column<?> desiVari01=table.column(contVariNames[i]);
                String desiType=desiVari01.type().name();
                if(desiType.equals("INTEGER")) {
                    HashMap<Integer,Long> hm=new HashMap<>();
                    for(int j=0;j<totaRows;j++) {
                        if(!table.column(contVariNames[i]).isMissing(j)) {
                            int temp=table.intColumn(contVariNames[i]).getInt(j);
                            if(hm.containsKey(temp)) {
                                hm.put(temp,hm.get(temp)+1);
                            } else {
                                hm.put(temp,1L);
                            }
                        }
                    }
                    int desiMode=0;
                    for(int l:hm.keySet()) {
                        if(maxiFreq<hm.get(l)) {
                            maxiFreq=hm.get(l);
                            desiMode=l;
                        }
                    }
                    IntColumn ic=(IntColumn)table.column(contVariNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiMode);
                    table.replaceColumn(contVariNames[i],ci);
                } else if(desiType.equals("DOUBLE")) {
                    HashMap<Double,Long> hm=new HashMap<>();
                    for(int j=0;j<totaRows;j++) {
                        if(!table.column(contVariNames[i]).isMissing(j)) {
                            double temp=table.doubleColumn(contVariNames[i]).getDouble(j);
                            if(hm.containsKey(temp)) {
                                hm.put(temp,hm.get(temp)+1);
                            } else {
                                hm.put(temp,1L);
                            }
                        }
                    }
                    double desiMode=0;
                    for(double l:hm.keySet()) {
                        if(maxiFreq<hm.get(l)) {
                            maxiFreq=hm.get(l);
                            desiMode=l;
                        }
                    }
                    DoubleColumn ic=(DoubleColumn)table.column(contVariNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiMode);
                    table.replaceColumn(contVariNames[i],ci);
                }
            }
        }
        for(int i=0;i<cateVariCount;i++) {
            long maxiFreq=0;
            if(table.column(cateVariNames[i]).countMissing()!=0) {
                HashMap<String,Long> hm=new HashMap<>();
                for(int j=0;j<totaRows;j++) {
                    if(!table.column(cateVariNames[i]).isMissing(j)) {
                        String temp=table.stringColumn(cateVariNames[i]).getString(j);
                        if(hm.containsKey(temp)) {
                            hm.put(temp,hm.get(temp)+1);
                        } else {
                            hm.put(temp,1L);
                        }
                    }
                }
                String desiMode="";
                for(String l:hm.keySet()) {
                    if(maxiFreq<hm.get(l)) {
                        maxiFreq=hm.get(l);
                        desiMode=l;
                    }
                }
                StringColumn ic=(StringColumn)table.column(cateVariNames[i]);
                Column<String> ci=ic.setMissingTo(desiMode);
                table.replaceColumn(cateVariNames[i],ci);
            }
        }
    }
    public static void replaceByMean(Table table,String[] contVariNames) {
        //Iterating over continuous variables
        for(int i=0;i<contVariNames.length;i++) {
            double desiMean=0;
            if(table.column(contVariNames[i]).countMissing()!=0) {
                NumericColumn<?> desiVari=table.nCol(contVariNames[i]);
                desiMean=desiVari.mean();
                System.out.println(desiMean);
                Column<?> desiVari01=table.column(contVariNames[i]);
                String desiType=desiVari01.type().name();
                if(desiType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(contVariNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiMean);
                    table.replaceColumn(contVariNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(contVariNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiMean);
                    table.replaceColumn(contVariNames[i],ci);
                }
            }
        }
    }
    public static void main(String args[]) {
        //Creating variables
        long totaRows;

        //Importing data
        Table table=Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/sample_corr_01.csv");
        Table structureOfTable=table.structure();
        totaRows=table.rowCount();
        System.out.println(table);

        //Storing attributes
        String[] attr=table.columnNames().toArray(new String[0]);
        int contVariCount=structureOfTable.stringColumn(2).isEqualTo("INTEGER").size();
        contVariCount+=structureOfTable.stringColumn(2).isEqualTo("DOUBLE").size();
        Scanner sc=new Scanner(System.in);

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

        //Storing categorical variable name
        String[] cateVariNames=new String[structureOfTable.rowCount()-contVariCount];
        for(int i=0,j=0;i<table.columnCount() && j<cateVariNames.length;i++) {
            if(Objects.equals(structureOfTable.column(2).getString(i),"STRING")) {
                cateVariNames[j]=structureOfTable.column(1).getString(i);
                j+=1;
            }
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
                replaceByMean(table,contVariNames);
                break;
            case 02://Replace by Mode
                replaceByMode(table,cateVariNames,contVariNames);
                break;
            case 03://Replace by Median
                replaceByMedian(table,contVariNames);
                break;
            case 04://Replace by Minimum
                replaceByMinimum(table,contVariNames);
                break;
            case 05://Replace by Maximum
                replaceByMaximum(table,contVariNames);
                break;
            case 06://Replace by Global Constant
                replaceByGlobalConstant(table,contVariNames);
                break;
            default://Invalid Input
                System.out.println("Invalid Input!");
                break;
        }
        System.out.println(table);
        table.write().toFile("C:\\Users\\Asus\\OneDrive\\Desktop\\newFile.csv");
    }
}