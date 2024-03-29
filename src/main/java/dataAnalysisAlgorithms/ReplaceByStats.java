package dataAnalysisAlgorithms;

import java.util.*;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

public class ReplaceByStats {
    private Table inputTable;
    private Table outputTable;
    ReplaceByStats() {
    }
    ReplaceByStats(Table inputTable) {
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
    public static int getContinuousVariableCount(Table table) {
        //Creating variables
        Table structureOfTable=table.structure();
        int continuousVariablesCount=structureOfTable.stringColumn(2).isEqualTo("INTEGER").size();
        continuousVariablesCount+=structureOfTable.stringColumn(2).isEqualTo("DOUBLE").size();
        return continuousVariablesCount;
    }
    public static String[] getContinuousVariableNames(Table table) {
        //Creating variables
        Table structureOfTable=table.structure();
        int continuousVariablesCount=getContinuousVariableCount(table);

        //Storing continuous variable name
        String[] continuousVariablesNames=new String[continuousVariablesCount];
        for(int i=0,j=0;i<table.columnCount() && j<continuousVariablesCount;i++) {
            if(Objects.equals(structureOfTable.column(2).getString(i),"INTEGER")) {
                continuousVariablesNames[j]=structureOfTable.column(1).getString(i);
                j+=1;
            }
            if(Objects.equals(structureOfTable.column(2).getString(i),"DOUBLE")) {
                continuousVariablesNames[j]=structureOfTable.column(1).getString(i);
                j+=1;
            }
        }
        return continuousVariablesNames;
    }
    public static Table replaceByGlobalConstant(Table table,String[] continuousVariableNames) {
        //Creating variables
        Scanner sc=new Scanner(System.in);

        //Iterating over continuous variables
        for(int i=0;i<continuousVariableNames.length;i++) {
            double globalConstant=0;
            if(table.column(continuousVariableNames[i]).countMissing()!=0) {
                System.out.print("Enter Global Constant for "+continuousVariableNames[i]+": ");
                globalConstant=sc.nextDouble();
                Column<?> desiredVariable01=table.column(continuousVariableNames[i]);
                String desiredType=desiredVariable01.type().name();
                if(desiredType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(continuousVariableNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)globalConstant);
                    table.replaceColumn(continuousVariableNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(continuousVariableNames[i]);
                    Column<Double> ci=ic.setMissingTo(globalConstant);
                    table.replaceColumn(continuousVariableNames[i],ci);
                }
            }
        }
        return table;
    }
    public static Table replaceByMaximum(Table table,String[] continuousVariableNames) {
        //Iterating over continuous variables
        for(int i=0;i<continuousVariableNames.length;i++) {
            double desiredMaximum=0;
            if(table.column(continuousVariableNames[i]).countMissing()!=0) {
                NumericColumn<?> desiredVariable=table.nCol(continuousVariableNames[i]);
                desiredMaximum=desiredVariable.max();
                System.out.println(desiredMaximum);
                Column<?> desiredVariable01=table.column(continuousVariableNames[i]);
                String desiredType=desiredVariable01.type().name();
                if(desiredType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(continuousVariableNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiredMaximum);
                    table.replaceColumn(continuousVariableNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(continuousVariableNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiredMaximum);
                    table.replaceColumn(continuousVariableNames[i],ci);
                }
            }
        }
        return table;
    }
    public static Table replaceByMinimum(Table table,String[] continuousVariableNames) {
        //Iterating over continuous variables
        for(int i=0;i<continuousVariableNames.length;i++) {
            double desiredMinimum=0;
            if(table.column(continuousVariableNames[i]).countMissing()!=0) {
                NumericColumn<?> desiredVariable=table.nCol(continuousVariableNames[i]);
                desiredMinimum=desiredVariable.min();
                System.out.println(desiredMinimum);
                Column<?> desiredVariable01=table.column(continuousVariableNames[i]);
                String desiredType=desiredVariable01.type().name();
                if(desiredType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(continuousVariableNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiredMinimum);
                    table.replaceColumn(continuousVariableNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(continuousVariableNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiredMinimum);
                    table.replaceColumn(continuousVariableNames[i],ci);
                }
            }
        }
        return table;
    }
    public static Table replaceByMedian(Table table,String[] continuousVariableNames) {
        //Iterating over continuous variables
        for(int i=0;i<continuousVariableNames.length;i++) {
            double desiredMedian=0;
            if(table.column(continuousVariableNames[i]).countMissing()!=0) {
                NumericColumn<?> desiredVariable=table.nCol(continuousVariableNames[i]);
                desiredMedian=desiredVariable.median();
                System.out.println(desiredMedian);
                Column<?> desiredVariable01=table.column(continuousVariableNames[i]);
                String desiredType=desiredVariable01.type().name();
                if(desiredType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(continuousVariableNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiredMedian);
                    table.replaceColumn(continuousVariableNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(continuousVariableNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiredMedian);
                    table.replaceColumn(continuousVariableNames[i],ci);
                }
            }
        }
        return table;
    }
    public static Table replaceByMode(Table table,String[] cateVariNames,String[] continuousVariableNames) {
        //Creating Variables
        int totalRows=table.rowCount();
        int continuousVariableCount=continuousVariableNames.length;
        int categoricalVariableCount=cateVariNames.length;

        for(int i=0;i<continuousVariableCount;i++) {
            long maxFrequency=0;
            if(table.column(continuousVariableNames[i]).countMissing()!=0) {
                Column<?> desiredVariable01=table.column(continuousVariableNames[i]);
                String desiredType=desiredVariable01.type().name();
                if(desiredType.equals("INTEGER")) {
                    HashMap<Integer,Long> hm=new HashMap<>();
                    for(int j=0;j<totalRows;j++) {
                        if(!table.column(continuousVariableNames[i]).isMissing(j)) {
                            int temp=table.intColumn(continuousVariableNames[i]).getInt(j);
                            if(hm.containsKey(temp)) {
                                hm.put(temp,hm.get(temp)+1);
                            } else {
                                hm.put(temp,1L);
                            }
                        }
                    }
                    int desiredMode=0;
                    for(int l:hm.keySet()) {
                        if(maxFrequency<hm.get(l)) {
                            maxFrequency=hm.get(l);
                            desiredMode=l;
                        }
                    }
                    IntColumn ic=(IntColumn)table.column(continuousVariableNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiredMode);
                    table.replaceColumn(continuousVariableNames[i],ci);
                } else if(desiredType.equals("DOUBLE")) {
                    HashMap<Double,Long> hm=new HashMap<>();
                    for(int j=0;j<totalRows;j++) {
                        if(!table.column(continuousVariableNames[i]).isMissing(j)) {
                            double temp=table.doubleColumn(continuousVariableNames[i]).getDouble(j);
                            if(hm.containsKey(temp)) {
                                hm.put(temp,hm.get(temp)+1);
                            } else {
                                hm.put(temp,1L);
                            }
                        }
                    }
                    double desiredMode=0;
                    for(double l:hm.keySet()) {
                        if(maxFrequency<hm.get(l)) {
                            maxFrequency=hm.get(l);
                            desiredMode=l;
                        }
                    }
                    DoubleColumn ic=(DoubleColumn)table.column(continuousVariableNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiredMode);
                    table.replaceColumn(continuousVariableNames[i],ci);
                }
            }
        }
        for(int i=0;i<categoricalVariableCount;i++) {
            long maxFrequency=0;
            if(table.column(cateVariNames[i]).countMissing()!=0) {
                HashMap<String,Long> hm=new HashMap<>();
                for(int j=0;j<totalRows;j++) {
                    if(!table.column(cateVariNames[i]).isMissing(j)) {
                        String temp=table.stringColumn(cateVariNames[i]).getString(j);
                        if(hm.containsKey(temp)) {
                            hm.put(temp,hm.get(temp)+1);
                        } else {
                            hm.put(temp,1L);
                        }
                    }
                }
                String desiredMode="";
                for(String l:hm.keySet()) {
                    if(maxFrequency<hm.get(l)) {
                        maxFrequency=hm.get(l);
                        desiredMode=l;
                    }
                }
                StringColumn ic=(StringColumn)table.column(cateVariNames[i]);
                Column<String> ci=ic.setMissingTo(desiredMode);
                table.replaceColumn(cateVariNames[i],ci);
            }
        }
        return table;
    }
    public static Table replaceByMean(Table table,String[] continuousVariableNames) {
        //Iterating over continuous variables
        for(int i=0;i<continuousVariableNames.length;i++) {
            double desiredMean=0;
            if(table.column(continuousVariableNames[i]).countMissing()!=0) {
                NumericColumn<?> desiredVariable=table.nCol(continuousVariableNames[i]);
                desiredMean=desiredVariable.mean();
                System.out.println(desiredMean);
                Column<?> desiredVariable01=table.column(continuousVariableNames[i]);
                String desiredType=desiredVariable01.type().name();
                if(desiredType.equals("INTEGER")) {
                    IntColumn ic=(IntColumn)table.column(continuousVariableNames[i]);
                    Column<Integer> ci=ic.setMissingTo((int)desiredMean);
                    table.replaceColumn(continuousVariableNames[i],ci);
                } else {
                    DoubleColumn ic=(DoubleColumn)table.column(continuousVariableNames[i]);
                    Column<Double> ci=ic.setMissingTo(desiredMean);
                    table.replaceColumn(continuousVariableNames[i],ci);
                }
            }
        }
        return table;
    }
    public Table calculateOutputTable() {
        //Creating variables
        Table table=this.inputTable;
        Table structureOfTable=table.structure();

        //Storing attributes
        int continuousVariableCount=getContinuousVariableCount(table);
        Scanner sc=new Scanner(System.in);

        //Storing continuous variable name
        String[] continuousVariableNames=getContinuousVariableNames(table);

        //Storing categorical variable name
        String[] cateVariNames=new String[(structureOfTable.rowCount())-continuousVariableCount];
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
                replaceByMean(table,continuousVariableNames);
                break;
            case 02://Replace by Mode
                replaceByMode(table,cateVariNames,continuousVariableNames);
                break;
            case 03://Replace by Median
                replaceByMedian(table,continuousVariableNames);
                break;
            case 04://Replace by Minimum
                replaceByMinimum(table,continuousVariableNames);
                break;
            case 05://Replace by Maximum
                replaceByMaximum(table,continuousVariableNames);
                break;
            case 06://Replace by Global Constant
                replaceByGlobalConstant(table,continuousVariableNames);
                break;
            default://Invalid Input
                System.out.println("Invalid Input!");
                break;
        }
        return table;
    }
    public static void main(String[] args) {
        ReplaceByStats replaceByStats01=new ReplaceByStats(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(replaceByStats01.getOutputTable());
    }
}