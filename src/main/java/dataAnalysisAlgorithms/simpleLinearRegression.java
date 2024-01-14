package dataAnalysisAlgorithms;

import java.lang.Math;
import java.util.Objects;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import static dataAnalysisAlgorithms.findingCorrelationMatrix.getCorrelationMatrix;

public class simpleLinearRegression {
    public static void fillMissingValuesUsingSLR(Table table) {
        //Creating variables
        double[][] corrMatr=getCorrelationMatrix(table);
        Table structureOfTable=table.structure();
        int totalRows=table.rowCount();

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

        //Iterating through each and every variable
        for(int i=0;i<contVariNames.length;i++) {
            if(table.column(contVariNames[i]).countMissing()!=0) {
                int posi=0;
                double maxi=0;
                for(int j=0;j<contVariNames.length;j++) {
                    if(i!=j) {
                        double curr=Math.abs(corrMatr[i][j]);
                        if(curr>maxi) {
                            maxi=corrMatr[i][j];
                            posi=j;
                        }
                    }
                }
                System.out.println("The variable "+contVariNames[i]+" has maximum correlation with "+contVariNames[posi]+" with correlation value "+maxi);

                //Calculating sum required for calculating slope and intercept
                double variRows=0,sumiOfX=0,sumiOfY=0,sumiOfXY=0,sumiOfXSqr=0;
                for(int j=0;j<totalRows;j++) {
                    if(!table.column(contVariNames[i]).isMissing(j) && !table.column(contVariNames[posi]).isMissing(j)) {
                        variRows+=1;
                        double xval=0,yval=0;
                        Column<?> yCol=table.column(contVariNames[i]);
                        Column<?> xCol=table.column(contVariNames[posi]);
                        String yType=yCol.type().name();
                        String xType=xCol.type().name();
                        if(yType.equals("INTEGER")) {
                            yval=table.intColumn(contVariNames[i]).getInt(j);
                        } else {
                            yval=table.doubleColumn(contVariNames[i]).getDouble(j);
                        }
                        if(xType.equals("INTEGER")) {
                            xval=table.intColumn(contVariNames[posi]).getInt(j);
                        } else {
                            xval=table.doubleColumn(contVariNames[posi]).getDouble(j);
                        }
                        sumiOfX+=xval;
                        sumiOfY+=yval;
                        sumiOfXY+=xval*yval;
                        sumiOfXSqr+=xval*xval;
                    }
                }

                System.out.println("The sum of "+contVariNames[i]+" is "+sumiOfX);
                System.out.println("The sum of "+contVariNames[posi]+" is "+sumiOfY);
                System.out.println("The sum of "+contVariNames[posi]+"*"+contVariNames[i]+" is "+sumiOfXY);
                System.out.println("The sum of "+contVariNames[posi]+"*"+contVariNames[posi]+" is "+sumiOfXSqr);

                //Calculating slope and intercept
                double slope=(variRows*sumiOfXY-sumiOfX*sumiOfY)/(variRows*sumiOfXSqr-sumiOfX*sumiOfX);
                double intercept=(sumiOfY-slope*sumiOfX)/variRows;
                System.out.println("The linear regression equation for "+contVariNames[i]+" and "+contVariNames[posi]+" is "+contVariNames[i]+"="+slope+"*"+contVariNames[posi]+"+"+intercept);

                //Predicting missing values
                for(int j=0;j<totalRows;j++) {
                    if(table.column(contVariNames[i]).isMissing(j) && !table.column(contVariNames[posi]).isMissing(j)) {
                        double xval=0,yval=0;
                        Column<?> xCol=table.column(contVariNames[posi]);
                        Column<?> yCol=table.column(contVariNames[i]);
                        String xType=xCol.type().name();
                        String yType=yCol.type().name();
                        if(xType.equals("INTEGER")) {
                            xval=table.intColumn(contVariNames[posi]).getInt(j);
                        } else {
                            xval=table.doubleColumn(contVariNames[posi]).getDouble(j);
                        }
                        yval=slope*xval+intercept;
                        if(yType.equals("INTEGER")) {
                            table.intColumn(contVariNames[i]).set(j,(int)yval);
                        } else {
                            table.doubleColumn(contVariNames[i]).set(j,yval);
                        }
                        System.out.println("The predicted value of "+contVariNames[i]+" for row "+j+" is "+yval);
                    }
                }
            }
        }
        table.write().toFile("/home/tendopain/IdeaProjects/Mini_Project/Datasets/newFile.csv");
    }

    public static void main(String args[]) {
        //Importing data
        Table table=Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/sample_corr.csv");
        double[][] corrMatr=getCorrelationMatrix(table);
        Table structureOfTable=table.structure();
        int totalRows=table.rowCount();

        //Displaying content and structure
        /*System.out.println(table);
        System.out.println();
        System.out.println(structureOfTable);
        System.out.println();
        System.out.println(summaryOfTable);
        System.out.println();*/

        //Iterating through each and every variable
        fillMissingValuesUsingSLR(table);
    }
}