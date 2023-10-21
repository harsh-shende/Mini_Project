package dataAnalysisAlgorithms;

import java.util.*;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class naiveBayes {
    public static void fillMissingValuesByNaiveBayes(Table orgiTable,Table missTable,String attr) {
        //Creating variables
        long totaRows=orgiTable.rowCount();
        long missRows=missTable.rowCount();
        Scanner sc=new Scanner(System.in);
        Table tempPredTable;
        String targName=attr;

        //Determing datatype of target variable
        Column<?> targVari=orgiTable.column(targName);
        String targType=targVari.type().name();
        System.out.println("Target variable datatype: "+targType);

        //Determing the all possible values of target variable
        List<Object> possValues=new ArrayList<>();
        List<Double> probabilty=new ArrayList<>();
        List<Table> tempTable=new ArrayList<>();
        for(int i=0;i<totaRows;i++) {
            Table temporary;
            Object value;
            if(targType.equals("INTEGER")) {
                value=orgiTable.intColumn(targName).getInt(i);
                temporary=orgiTable.where(orgiTable.intColumn(targName).isEqualTo((int)value));
            } else if(targType.equals("DOUBLE")) {
                value=orgiTable.doubleColumn(targName).getDouble(i);
                temporary=orgiTable.where(orgiTable.doubleColumn(targName).isEqualTo((double)value));
            } else if(targType.equals("STRING")) {
                value=orgiTable.stringColumn(targName).getString(i);
                temporary=orgiTable.where(orgiTable.stringColumn(targName).isEqualTo((String)value));
            } else {
                return;
            }
            if(value!=null && value!="" && !possValues.contains(value)) {
                possValues.add(value);
                tempTable.add(temporary);
            }
        }

        //Inputting predictor variables number
        System.out.print("Enter the number of predictor variable: ");
        int predCoun=sc.nextInt();
        //pred[] to store predictor variables name
        String[] pred=new String[predCoun];
        //predRows[] to store predictor variables rows
        double[] predRows=new double[predCoun];

        //Inputting name of predictor variables
        System.out.println("Enter the names of predictor variables: ");
        for(int i=0;i<predCoun;i++) {
            System.out.print("Enter the predictor variable name: ");
            pred[i]=sc.next();
        }

        //Iterating over columns having target variable as missing
        for(int i=0;i<missRows;i++) {
            double maxiprob=0;
            Object desiredValue=null;
            for(int j=0;j<(possValues.size());j++) {
                double finalProb=1;
                long targRows=tempTable.get(j).rowCount();
                for(int k=0;k<predCoun;k++) {
                    String predictorName=pred[k];
                    Column<?> predColumn=missTable.column(predictorName);
                    String predType=predColumn.type().name();
                    //System.out.println("Predictor variable datatype: "+predType);
                    if(predType.equals("INTEGER")) {
                        long predValu=missTable.intColumn(pred[k]).getInt(i);
                        tempPredTable=orgiTable.where(tempTable.get(j).intColumn(predictorName).isEqualTo(predValu));
                    } else if(predType.equals("DOUBLE")) {
                        double predValu=missTable.doubleColumn(pred[k]).getDouble(i);
                        tempPredTable=orgiTable.where(tempTable.get(j).doubleColumn(predictorName).isEqualTo(predValu));
                    } else if(predType.equals("STRING")) {
                        String predValu=missTable.stringColumn(pred[k]).getString(i);
                        tempPredTable=orgiTable.where(tempTable.get(j).stringColumn(predictorName).isEqualTo(predValu));
                    } else {
                        return;
                    }
                    //Finding the probability that target value happens provided predictor value has happened
                    predRows[k]=tempPredTable.rowCount();
                    predRows[k]/=targRows;
                    finalProb*=predRows[k];
                }
                finalProb*=targRows;
                finalProb/=totaRows;
                probabilty.add(finalProb);
                if(maxiprob<finalProb) {
                    maxiprob=finalProb;
                    desiredValue=possValues.get(j);
                }
            }
            System.out.println("DesiredValue: "+desiredValue+"    MaximumProbability: "+maxiprob);
        }
        /*for(Object o:possValues) {
            System.out.println(o+" "+probabilty.get(possValues.indexOf(o)));
        }*/
    }
    public static void main(String args[]) {
        //Creating variables
        Table tempTable,tempPredTable;
        long totaRows,targRows;
        double finalProb=1;

        //Importing data
        Table table=Table.read().csv("C:\\Users\\Asus\\OneDrive\\Documents\\Data Science\\Datasets\\student-mat.csv");
        Table structureOfTable=table.structure();
        totaRows=table.rowCount();

        //Storing attributes
        String[] attr=table.columnNames().toArray(new String[0]);
        Scanner sc=new Scanner(System.in);

        //Displaying content and structure
        System.out.println("The content of tables is: ");
        System.out.println(table);
        System.out.println(structureOfTable);
        System.out.println("The total number of rows in table: ");
        System.out.println(totaRows);

        //Inputting target variable name
        System.out.print("Enter the target variable name: ");
        String targName=sc.next();

        //Creating a column of ? type and ? will be replaced by the datatype of target variable
        Column<?> targVari=table.column(targName);
        if(targVari==null) {
            System.out.println("Target variable not found in the table.");
            return;
        }
        String targType=targVari.type().name();
        System.out.println("Target variable datatype: "+targType);

        //Inputting target variable desired value
        System.out.print("Enter the value of target variable: ");
        if(targType.equals("INTEGER")) {
            long targValu=sc.nextLong();
            tempTable=table.where(table.intColumn(targName).isEqualTo(targValu));
        } else if(targType.equals("DOUBLE")) {
            double targValu=sc.nextDouble();
            tempTable=table.where(table.doubleColumn(targName).isEqualTo(targValu));
        } else {
            String targValu=sc.next();
            tempTable=table.where(table.stringColumn(targName).isEqualTo(targValu));
        }
        targRows=tempTable.rowCount();

        //Inputting predictor variables number
        System.out.print("Enter the number of predictor variable: ");
        int predCoun=sc.nextInt();
        //pred[] to store predictor variables name
        String[] pred=new String[predCoun];
        //predRows[] to store predictor variables rows
        double[] predRows=new double[predCoun];

        //Inputting name and value of predictor variables
        System.out.println("Enter the names of predictor variables: ");
        for(int i=0;i<predCoun;i++) {
            System.out.print("Enter the predictor variable name: ");
            pred[i]=sc.next();
            String predictorName=pred[i];
            Column<?> predColumn=table.column(predictorName);
            String predType=predColumn.type().name();
            System.out.println("Predictor variable datatype: "+predType);
            System.out.print("Enter the value of predictor variable: ");
            if(predColumn!=null) {
                if(predType.equals("INTEGER")) {
                    long predValu=sc.nextLong();
                    tempPredTable=table.where(tempTable.intColumn(predictorName).isEqualTo(predValu));
                } else if(predType.equals("DOUBLE")) {
                    double predValu=sc.nextDouble();
                    tempPredTable=table.where(tempTable.doubleColumn(predictorName).isEqualTo(predValu));
                } else {
                    String predValu=sc.next();
                    tempPredTable=table.where(tempTable.stringColumn(predictorName).isEqualTo(predValu));
                }

                //Finding the probability that target value happens provided predictor value has happened
                predRows[i]=tempPredTable.rowCount();
                System.out.println(targRows+" "+predRows[i]);
                predRows[i]/=targRows;
                finalProb*=predRows[i];
            } else {
                System.out.println("Predictor variable "+predictorName+" not found in the table.");
            }
        }

        //Calculating final probability
        finalProb*=targRows;
        finalProb/=totaRows;
        System.out.println("The probabilty that target variable happens provided predictor variables happened is: ");
        System.out.println(finalProb);
    }
}