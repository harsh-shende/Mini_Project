package dataAnalysisAlgorithms;

import java.util.*;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class naiveBayes {
    static List<Object> possValues=new ArrayList<>();
    static List<Double> probabilty=new ArrayList<>();
    static List<Table> tempTable=new ArrayList<>();
    public static void calculatingMaximumProbabilty(Table orgiTable,Table missTable,String[] pred,double[] predRows) {
        //Creating variables
        long totaRows=orgiTable.rowCount();
        long missRows=missTable.rowCount();

        //Iterating over columns having target variable as missing
        for(int i=0;i<missRows;i++) {
            double maxiprob=0;
            Object desiredValue=null;
            for (int j=0;j<(possValues.size());j++) {
                double finalProb=1;
                long targRows=tempTable.get(j).rowCount();
                for(int k=0;k<pred.length-1;k++) {
                    String predictorName=pred[k];
                    Column<?> predColumn=missTable.column(predictorName);
                    String predType=predColumn.type().name();
                    //System.out.println("Predictor variable datatype: "+predType);
                    if(predType.equals("INTEGER")) {
                        long predValu=missTable.intColumn(pred[k]).getInt(i);
                        Table tempPredTable=orgiTable.where(tempTable.get(j).intColumn(predictorName).isEqualTo(predValu));
                        //Finding the probability that target value happens provided predictor value has happened
                        predRows[k]=tempPredTable.rowCount();
                        predRows[k]/=targRows;
                        finalProb*=predRows[k];
                    } else if(predType.equals("DOUBLE")) {
                        double predValu=missTable.doubleColumn(pred[k]).getDouble(i);
                        Table tempPredTable=orgiTable.where(tempTable.get(j).doubleColumn(predictorName).isEqualTo(predValu));
                        //Finding the probability that target value happens provided predictor value has happened
                        predRows[k]=tempPredTable.rowCount();
                        predRows[k]/=targRows;
                        finalProb*=predRows[k];
                    } else if(predType.equals("STRING")) {
                        String predValu=missTable.stringColumn(pred[k]).getString(i);
                        Table tempPredTable=orgiTable.where(tempTable.get(j).stringColumn(predictorName).isEqualTo(predValu));
                        //Finding the probability that target value happens provided predictor value has happened
                        predRows[k]=tempPredTable.rowCount();
                        predRows[k]/=targRows;
                        finalProb*=predRows[k];
                    } else {
                        return;
                    }
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
    }
    public static void determineMissingValues(Table orgiTable,String targName) {
        //Determing datatype of target variable
        int totaRows=orgiTable.rowCount();
        Column<?> targVari=orgiTable.column(targName);
        String targType=targVari.type().name();
        System.out.println("Target variable datatype: "+targType);

        //Determing the all possible values of target variable
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
    }
    public static void fillMissingValuesByNaiveBayes(Table orgiTable,Table missTable,String attr) {
        //Creating variables
        Scanner sc=new Scanner(System.in);
        String targName=attr;

        //Determing the all possible values of target variable
        determineMissingValues(orgiTable,targName);

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
        calculatingMaximumProbabilty(orgiTable,missTable,pred,predRows);

        /*for(Object o:possValues) {
            System.out.println(o+" "+probabilty.get(possValues.indexOf(o)));
        }*/
    }
}