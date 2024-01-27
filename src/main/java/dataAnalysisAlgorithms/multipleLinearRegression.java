package dataAnalysisAlgorithms;

import java.lang.Math;
import java.util.Objects;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import static dataAnalysisAlgorithms.findingCorrelationMatrix.getCorrelationMatrix;

public class multipleLinearRegression {
    public static void fillMissingValuesUsing2ndMLR(Table table) {
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
        for(int i=0;i<contVariCount;i++) {
            if(table.column(contVariNames[i]).countMissing()!=0) {
                int[] posi=new int[2];
                double[] maxi=new double[2];
                for(int j=0;j<contVariCount;j++) {
                    if(i!=j) {
                        double curr=Math.abs(corrMatr[i][j]);
                        if(curr>maxi[0]) {
                            maxi[1]=maxi[0];
                            posi[1]=posi[0];
                            maxi[0]=corrMatr[i][j];
                            posi[0]=j;
                        }
                        else if(curr>maxi[1]) {
                            maxi[1]=corrMatr[i][j];
                            posi[1]=j;
                        }
                    }
                }
                System.out.println("The variable "+contVariNames[i]+" has maximum correlation with "+contVariNames[posi[0]]+" with correlation value "+maxi[0]);
                System.out.println("The variable "+contVariNames[i]+" has second maximum correlation with "+contVariNames[posi[1]]+" with correlation value "+maxi[1]);

                //Calculating sum required for calculating slope and intercept
                double[] sumiOfXn=new double[2];
                double[] sumiOfXnY=new double[2];
                double[] sumiOfXnSqr=new double[2];
                double variRows=0,sumiOfY=0,sumiOfX1X2=0;
                for(int j=0;j<totalRows;j++) {
                    if(!table.column(contVariNames[i]).isMissing(j) && !table.column(contVariNames[posi[0]]).isMissing(j) && !table.column(contVariNames[posi[1]]).isMissing(j)) {
                        variRows+=1;
                        double yval=0,x1val=0,x2val=0;
                        Column<?> yCol=table.column(contVariNames[i]);
                        Column<?> xCol1=table.column(contVariNames[posi[0]]);
                        Column<?> xCol2=table.column(contVariNames[posi[1]]);
                        String yType=yCol.type().name();
                        String xType1=xCol1.type().name();
                        String xType2=xCol2.type().name();
                        if(yType.equals("INTEGER")) {
                            yval=table.intColumn(contVariNames[i]).getInt(j);
                        }
                        else if(yType.equals("DOUBLE")) {
                            yval=table.doubleColumn(contVariNames[i]).getDouble(j);
                        }
                        if(xType1.equals("INTEGER")) {
                            x1val=table.intColumn(contVariNames[posi[0]]).getInt(j);
                        }
                        else if(xType1.equals("DOUBLE")) {
                            x1val=table.doubleColumn(contVariNames[posi[0]]).getDouble(j);
                        }
                        if(xType2.equals("INTEGER")) {
                            x2val=table.intColumn(contVariNames[posi[1]]).getInt(j);
                        }
                        else if(xType2.equals("DOUBLE")) {
                            x2val=table.doubleColumn(contVariNames[posi[1]]).getDouble(j);
                        }
                        sumiOfY+=yval;
                        sumiOfXn[0]+=x1val;
                        sumiOfXn[1]+=x2val;
                        sumiOfX1X2+=x1val*x2val;
                        sumiOfXnY[0]+=x1val*yval;
                        sumiOfXnY[1]+=x2val*yval;
                        sumiOfXnSqr[0]+=x1val*x1val;
                        sumiOfXnSqr[1]+=x2val*x2val;
                    }
                }

                System.out.println("The sum of "+contVariNames[i]+" is "+sumiOfY);
                System.out.println("The sum of "+contVariNames[posi[0]]+" is "+sumiOfXn[0]);
                System.out.println("The sum of "+contVariNames[posi[1]]+" is "+sumiOfXn[1]);
                System.out.println("The sum of "+contVariNames[posi[0]]+"*"+contVariNames[i]+" is "+sumiOfXnY[0]);
                System.out.println("The sum of "+contVariNames[posi[1]]+"*"+contVariNames[i]+" is "+sumiOfXnY[1]);
                System.out.println("The sum of "+contVariNames[posi[0]]+"*"+contVariNames[posi[0]]+" is "+sumiOfXnSqr[0]);
                System.out.println("The sum of "+contVariNames[posi[1]]+"*"+contVariNames[posi[1]]+" is "+sumiOfXnSqr[1]);
                System.out.println("The sum of "+contVariNames[posi[0]]+"*"+contVariNames[posi[1]]+" is "+sumiOfX1X2);

                //Calculating regression sums
                sumiOfXnSqr[0]-=(sumiOfXn[0]*sumiOfXn[0])/variRows;
                sumiOfXnSqr[1]-=(sumiOfXn[1]*sumiOfXn[1])/variRows;
                sumiOfXnY[0]-=(sumiOfXn[0]*sumiOfY)/variRows;
                sumiOfXnY[1]-=(sumiOfXn[1]*sumiOfY)/variRows;
                sumiOfX1X2-=(sumiOfXn[0]*sumiOfXn[1])/variRows;

                System.out.println("The regression sum of "+contVariNames[posi[0]]+"*"+contVariNames[posi[0]]+" is "+sumiOfXnSqr[0]);
                System.out.println("The regression sum of "+contVariNames[posi[1]]+"*"+contVariNames[posi[1]]+" is "+sumiOfXnSqr[1]);
                System.out.println("The regression sum of "+contVariNames[posi[0]]+"*"+contVariNames[i]+" is "+sumiOfXnY[0]);
                System.out.println("The regression sum of "+contVariNames[posi[1]]+"*"+contVariNames[i]+" is "+sumiOfXnY[1]);
                System.out.println("The regression sum of "+contVariNames[posi[0]]+"*"+contVariNames[posi[1]]+" is "+sumiOfX1X2);

                //Calculating slope and intercept
                double slope1=(((sumiOfXnSqr[1])*(sumiOfXnY[0]))-((sumiOfX1X2)*(sumiOfXnY[1])))/(((sumiOfXnSqr[0])*(sumiOfXnSqr[1]))-((sumiOfX1X2)*(sumiOfX1X2)));
                double slope2=(((sumiOfXnSqr[0])*(sumiOfXnY[1]))-((sumiOfX1X2)*(sumiOfXnY[0])))/(((sumiOfXnSqr[0])*(sumiOfXnSqr[1]))-((sumiOfX1X2)*(sumiOfX1X2)));
                double intercept=(sumiOfY-(slope1*sumiOfXn[0])-(slope2*sumiOfXn[1]))/variRows;

                System.out.println("The slope of "+contVariNames[i]+" with respect to "+contVariNames[posi[0]]+" is "+slope1);
                System.out.println("The slope of "+contVariNames[i]+" with respect to "+contVariNames[posi[1]]+" is "+slope2);
                System.out.println("The intercept of "+contVariNames[i]+" is "+intercept);

                //Filling missing values
                for(int j=0;j<totalRows;j++) {
                    if(table.column(contVariNames[i]).isMissing(j) && !table.column(contVariNames[posi[0]]).isMissing(j) && !table.column(contVariNames[posi[1]]).isMissing(j)) {
                        double x1val=0,x2val=0,yval=0;
                        Column<?> xCol1=table.column(contVariNames[posi[0]]);
                        Column<?> xCol2=table.column(contVariNames[posi[1]]);
                        Column<?> yCol=table.column(contVariNames[i]);
                        String xType1=xCol1.type().name();
                        String xType2=xCol2.type().name();
                        String yType=yCol.type().name();
                        if(xType1.equals("INTEGER")) {
                            x1val=table.intColumn(contVariNames[posi[0]]).getInt(j);
                        }
                        else if(xType1.equals("DOUBLE")) {
                            x1val=table.doubleColumn(contVariNames[posi[0]]).getDouble(j);
                        }
                        if(xType2.equals("INTEGER")) {
                            x2val=table.intColumn(contVariNames[posi[1]]).getInt(j);
                        }
                        else if(xType2.equals("DOUBLE")) {
                            x2val=table.doubleColumn(contVariNames[posi[1]]).getDouble(j);
                        }
                        yval=slope1*x1val+slope2*x2val+intercept;
                        if(yType.equals("INTEGER")) {
                            table.intColumn(contVariNames[i]).set(j,(int)yval);
                        }
                        else if(yType.equals("DOUBLE")) {
                            table.doubleColumn(contVariNames[i]).set(j,yval);
                        }
                        System.out.println("The predicted value of "+contVariNames[i]+" for row "+j+" is "+yval);
                    }
                }
            }
        }
        table.write().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/newFile.csv");
    }

    public static void main(String[] args) {
        Table table=Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv");
        fillMissingValuesUsing2ndMLR(table);
    }
}