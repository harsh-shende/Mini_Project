package dataAnalysisAlgorithms;

import java.lang.Math;
import java.util.Objects;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import static dataAnalysisAlgorithms.findingCorrelationMatrix.getCorrelationMatrix;

public class multipleLinearRegression {
    public static void getCofactorMatrix(double[][] matr,double[][] temp,int p,int q,int n) {
        int i=0,j=0;
        for(int row=0;row<n;row++) {
            for(int col=0;col<n;col++) {
                if(row!=p && col!=q) {
                    temp[i][j++]=matr[row][col];
                    if(j==n-1) {
                        j=0;
                        i++;
                    }
                }
            }
        }
    }
    public static double determinant(double[][] matr,int n) {
        if(n==1) {
            return matr[0][0];
        } else {
            double dete=0;
            double[][] temp=new double[n][n];
            int sign=1;
            for(int f=0;f<n;f++) {
                getCofactorMatrix(matr,temp,0,f,n);
                dete+=sign*matr[0][f]*determinant(temp,n-1);
                sign=-sign;
            }
            return dete;
        }
    }
    public static double[][] getAdjointMatrix(double[][] matr) {
        int n=matr.length;
        double[][] adjo=new double[n][n];
        double[][] temp=new double[n][n];
        int sign=1;
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                getCofactorMatrix(matr,temp,i,j,n);
                sign=((i+j)%2==0)?1:-1;
                adjo[j][i]=(sign)*(determinant(temp,n-1));
            }
        }
        return adjo;
    }
    public static double[] getSlopeAndIntercepts(double[][] inve,double[][] valu) {
        double[] slopeAndIntercept=new double[valu[0].length];
        for(int i=0;i<valu[0].length;i++) {
            for(int j=0;j<valu[0].length;j++) {
                slopeAndIntercept[i]+=inve[i][j]*valu[0][j];
            }
        }
        return slopeAndIntercept;
    }
    public static void fillMissingValuesUsing3rdMLR(Table table) {
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
                int[] posi=new int[3];
                double[] maxi=new double[3];
                for(int j=0;j<contVariCount;j++) {
                    if(i!=j) {
                        double curr=Math.abs(corrMatr[i][j]);
                        if(curr>maxi[0]) {
                            maxi[2]=maxi[1];
                            posi[2]=posi[1];
                            maxi[1]=maxi[0];
                            posi[1]=posi[0];
                            maxi[0]=corrMatr[i][j];
                            posi[0]=j;
                        }
                        else if(curr>maxi[1]) {
                            maxi[2]=maxi[1];
                            posi[2]=posi[1];
                            maxi[1]=corrMatr[i][j];
                            posi[1]=j;
                        }
                        else if(curr>maxi[2]) {
                            maxi[2]=corrMatr[i][j];
                            posi[2]=j;
                        }
                    }
                }
                System.out.println("The variable "+contVariNames[i]+" has maximum correlation with "+contVariNames[posi[0]]+" with correlation value "+maxi[0]);
                System.out.println("The variable "+contVariNames[i]+" has second maximum correlation with "+contVariNames[posi[1]]+" with correlation value "+maxi[1]);
                System.out.println("The variable "+contVariNames[i]+" has third maximum correlation with "+contVariNames[posi[2]]+" with correlation value "+maxi[2]);

                //Calculating sum required for calculating slope and intercept
                double[] sumiOfXn=new double[3];
                double[] sumiOfXnY=new double[3];
                double[] sumiOfXnSqr=new double[3];
                double variRows=0,sumiOfY=0,sumiOfX1X2=0,sumiOfX1X3=0,sumiOfX2X3=0;
                for(int j=0;j<totalRows;j++) {
                    if(!table.column(contVariNames[i]).isMissing(j) && !table.column(contVariNames[posi[0]]).isMissing(j) && !table.column(contVariNames[posi[1]]).isMissing(j) && !table.column(contVariNames[posi[2]]).isMissing(j)) {
                        variRows+=1;
                        double yval=0,x1val=0,x2val=0,x3val=0;
                        Column<?> yCol=table.column(contVariNames[i]);
                        Column<?> xCol1=table.column(contVariNames[posi[0]]);
                        Column<?> xCol2=table.column(contVariNames[posi[1]]);
                        Column<?> xCol3=table.column(contVariNames[posi[2]]);
                        String yType=yCol.type().name();
                        String xType1=xCol1.type().name();
                        String xType2=xCol2.type().name();
                        String xType3=xCol3.type().name();
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
                        if(xType3.equals("INTEGER")) {
                            x3val=table.intColumn(contVariNames[posi[2]]).getInt(j);
                        }
                        else if(xType3.equals("DOUBLE")) {
                            x3val=table.doubleColumn(contVariNames[posi[2]]).getDouble(j);
                        }
                        sumiOfY+=yval;
                        sumiOfXn[0]+=x1val;
                        sumiOfXn[1]+=x2val;
                        sumiOfXn[2]+=x3val;
                        sumiOfX1X2+=x1val*x2val;
                        sumiOfX1X3+=x1val*x3val;
                        sumiOfX2X3+=x2val*x3val;
                        sumiOfXnY[0]+=x1val*yval;
                        sumiOfXnY[1]+=x2val*yval;
                        sumiOfXnY[2]+=x3val*yval;
                        sumiOfXnSqr[0]+=x1val*x1val;
                        sumiOfXnSqr[1]+=x2val*x2val;
                        sumiOfXnSqr[2]+=x3val*x3val;
                    }
                }

                System.out.println("The sum of "+contVariNames[i]+" is "+sumiOfY);
                System.out.println("The sum of "+contVariNames[posi[0]]+" is "+sumiOfXn[0]);
                System.out.println("The sum of "+contVariNames[posi[1]]+" is "+sumiOfXn[1]);
                System.out.println("The sum of "+contVariNames[posi[2]]+" is "+sumiOfXn[2]);
                System.out.println("The sum of "+contVariNames[posi[0]]+"*"+contVariNames[i]+" is "+sumiOfXnY[0]);
                System.out.println("The sum of "+contVariNames[posi[1]]+"*"+contVariNames[i]+" is "+sumiOfXnY[1]);
                System.out.println("The sum of "+contVariNames[posi[2]]+"*"+contVariNames[i]+" is "+sumiOfXnY[2]);
                System.out.println("The sum of "+contVariNames[posi[0]]+"*"+contVariNames[posi[0]]+" is "+sumiOfXnSqr[0]);
                System.out.println("The sum of "+contVariNames[posi[1]]+"*"+contVariNames[posi[1]]+" is "+sumiOfXnSqr[1]);
                System.out.println("The sum of "+contVariNames[posi[2]]+"*"+contVariNames[posi[2]]+" is "+sumiOfXnSqr[2]);
                System.out.println("The sum of "+contVariNames[posi[0]]+"*"+contVariNames[posi[1]]+" is "+sumiOfX1X2);
                System.out.println("The sum of "+contVariNames[posi[0]]+"*"+contVariNames[posi[2]]+" is "+sumiOfX1X3);
                System.out.println("The sum of "+contVariNames[posi[1]]+"*"+contVariNames[posi[2]]+" is "+sumiOfX2X3);

                //Creating matrix of coefficents
                double[][] valu=new double[1][4];
                double[][] orgi=new double[4][4];
                valu[0][0]=sumiOfY; valu[0][1]=sumiOfXnY[0]; valu[0][2]=sumiOfXnY[1]; valu[0][3]=sumiOfXnY[2];
                orgi[0][0]=variRows; orgi[0][1]=sumiOfXn[0]; orgi[0][2]=sumiOfXn[1]; orgi[0][3]=sumiOfXn[2];
                orgi[1][0]=sumiOfXn[0]; orgi[1][1]=sumiOfXnSqr[0]; orgi[1][2]=sumiOfX1X2; orgi[1][3]=sumiOfX1X3;
                orgi[2][0]=sumiOfXn[1]; orgi[2][1]=sumiOfX1X2; orgi[2][2]=sumiOfXnSqr[1]; orgi[2][3]=sumiOfX2X3;
                orgi[3][0]=sumiOfXn[2]; orgi[3][1]=sumiOfX1X3; orgi[3][2]=sumiOfX2X3; orgi[3][3]=sumiOfXnSqr[2];

                //Checking if inverse exists
                double dete=determinant(orgi,4);
                if(dete==0) {
                    System.out.println(dete);
                    System.out.println("Inverse does not exist");
                } else {
                    //Getting adjoint matrix
                    double[][] adjo=getAdjointMatrix(orgi);

                    //Calculating inverse matrix
                    double[][] inve=new double[4][4];
                    for(int j=0;j<4;j++) {
                        for(int k=0;k<4;k++) {
                            inve[j][k]=adjo[j][k]/dete;
                        }
                    }

                    //Calculating slope and intercept
                    double[] slopeAndIntercept=getSlopeAndIntercepts(inve,valu);
                    System.out.println("The slope of "+contVariNames[i]+" with respect to "+contVariNames[posi[0]]+" is "+slopeAndIntercept[1]);
                    System.out.println("The slope of "+contVariNames[i]+" with respect to "+contVariNames[posi[1]]+" is "+slopeAndIntercept[2]);
                    System.out.println("The slope of "+contVariNames[i]+" with respect to "+contVariNames[posi[2]]+" is "+slopeAndIntercept[3]);
                    System.out.println("The intercept of "+contVariNames[i]+" is "+slopeAndIntercept[0]);

                    //Filling missing values
                    for(int j=0;j<totalRows;j++) {
                        if(table.column(contVariNames[i]).isMissing(j) && !table.column(contVariNames[posi[0]]).isMissing(j) && !table.column(contVariNames[posi[1]]).isMissing(j) && !table.column(contVariNames[posi[2]]).isMissing(j)) {
                            double x1val=0,x2val=0,x3val=0,yval=0;
                            Column<?> xCol1=table.column(contVariNames[posi[0]]);
                            Column<?> xCol2=table.column(contVariNames[posi[1]]);
                            Column<?> xCol3=table.column(contVariNames[posi[2]]);
                            Column<?> yCol=table.column(contVariNames[i]);
                            String xType1=xCol1.type().name();
                            String xType2=xCol2.type().name();
                            String xType3=xCol3.type().name();
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
                            if(xType3.equals("INTEGER")) {
                                x3val=table.intColumn(contVariNames[posi[2]]).getInt(j);
                            }
                            else if(xType3.equals("DOUBLE")) {
                                x3val=table.doubleColumn(contVariNames[posi[2]]).getDouble(j);
                            }
                            yval=slopeAndIntercept[1]*x1val+slopeAndIntercept[2]*x2val+slopeAndIntercept[3]*x3val+slopeAndIntercept[0];
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
        }
    }
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
        fillMissingValuesUsing3rdMLR(table);
    }
}