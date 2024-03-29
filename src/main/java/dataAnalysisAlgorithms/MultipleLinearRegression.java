package dataAnalysisAlgorithms;

import java.lang.Math;
import java.util.Objects;
import java.util.Scanner;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class MultipleLinearRegression {
    private Table inputTable;
    private Table outputTable;
    MultipleLinearRegression() {
        this.inputTable=null;
        this.outputTable=null;
    }
    MultipleLinearRegression(Table inputTable) {
        this.inputTable=inputTable;
        this.outputTable=(this.calculateOutputTable());
    }
    public Table getInputTable() {
        return this.inputTable;
    }
    public Table getOutputTable() {
        return this.outputTable;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    public void setOutputTable(Table outputTable) {
        this.outputTable=outputTable;
    }
    public Table calculateOutputTable() {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the number of predictor variables to be used: ");
        int predictorVariables=sc.nextInt();
        if(predictorVariables==2) {
            return (this.fillMissingValuesUsing2ndMLR());
        } else if(predictorVariables==3) {
            return (this.fillMissingValuesUsing3rdMLR());
        } else {
            System.out.println("Invalid Input! Please enter 02 or 03....");
            return null;
        }
    }
    public static void getCofactorMatrix(double[][] matrix,double[][] temp,int p,int q,int n) {
        int i=0,j=0;
        for(int row=0;row<n;row++) {
            for(int col=0;col<n;col++) {
                if(row!=p && col!=q) {
                    temp[i][j++]=matrix[row][col];
                    if(j==n-1) {
                        j=0;
                        i++;
                    }
                }
            }
        }
    }
    public static double getDeterminant(double[][] matrix,int n) {
        if(n==1) {
            return matrix[0][0];
        } else {
            double determinantMatrix=0;
            double[][] temp=new double[n][n];
            int sign=1;
            for(int f=0;f<n;f++) {
                getCofactorMatrix(matrix,temp,0,f,n);
                determinantMatrix+=sign*matrix[0][f]*getDeterminant(temp,n-1);
                sign=-sign;
            }
            return determinantMatrix;
        }
    }
    public static double[][] getAdjointMatrix(double[][] matrix) {
        int n=matrix.length;
        double[][] adjointMatrix=new double[n][n];
        double[][] temp=new double[n][n];
        int sign=1;
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                getCofactorMatrix(matrix,temp,i,j,n);
                sign=((i+j)%2==0)?1:-1;
                adjointMatrix[j][i]=(sign)*(getDeterminant(temp,n-1));
            }
        }
        return adjointMatrix;
    }
    public static double[] getSlopeAndIntercepts(double[][] inverseMatrix,double[][] valueMatrix) {
        double[] slopeAndIntercept=new double[valueMatrix[0].length];
        for(int i=0;i<valueMatrix[0].length;i++) {
            for(int j=0;j<valueMatrix[0].length;j++) {
                slopeAndIntercept[i]+=inverseMatrix[i][j]*valueMatrix[0][j];
            }
        }
        return slopeAndIntercept;
    }
    public static int getContinuousVariablesCount(Table table) {
        Table structureOfTable=table.structure();
        int continuousVariablesCount=structureOfTable.stringColumn(2).isEqualTo("INTEGER").size();
        continuousVariablesCount+=structureOfTable.stringColumn(2).isEqualTo("DOUBLE").size();
        return continuousVariablesCount;
    }
    public static String[] getContinuousVariablesNames(Table table) {
        Table structureOfTable=table.structure();
        int continuousVariablesCount=getContinuousVariablesCount(table);
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
    public Table fillMissingValuesUsing3rdMLR() {
        //Creating variables
        Table table=this.inputTable;
        CorrelationMatrix correlationMatrix01=new CorrelationMatrix(table);
        double[][] correlationMatrix=correlationMatrix01.getCorrelationMatrix();
        Table structureOfTable=table.structure();
        int totalRows=table.rowCount();

        //Storing attributes
        int continuousVariablesCount=getContinuousVariablesCount(table);

        //Storing continuous variable name
        String[] continuousVariablesNames=getContinuousVariablesNames(table);

        //Iterating through each and every variable
        for(int i=0;i<continuousVariablesCount;i++) {
            if(table.column(continuousVariablesNames[i]).countMissing()!=0) {
                int[] posi=new int[3];
                double[] maxi=new double[3];
                for(int j=0;j<continuousVariablesCount;j++) {
                    if(i!=j) {
                        double curr=Math.abs(correlationMatrix[i][j]);
                        if(curr>maxi[0]) {
                            maxi[2]=maxi[1];
                            posi[2]=posi[1];
                            maxi[1]=maxi[0];
                            posi[1]=posi[0];
                            maxi[0]=correlationMatrix[i][j];
                            posi[0]=j;
                        }
                        else if(curr>maxi[1]) {
                            maxi[2]=maxi[1];
                            posi[2]=posi[1];
                            maxi[1]=correlationMatrix[i][j];
                            posi[1]=j;
                        }
                        else if(curr>maxi[2]) {
                            maxi[2]=correlationMatrix[i][j];
                            posi[2]=j;
                        }
                    }
                }
                System.out.println("The variable "+continuousVariablesNames[i]+" has maximum correlation with "+continuousVariablesNames[posi[0]]+" with correlation value "+maxi[0]);
                System.out.println("The variable "+continuousVariablesNames[i]+" has second maximum correlation with "+continuousVariablesNames[posi[1]]+" with correlation value "+maxi[1]);
                System.out.println("The variable "+continuousVariablesNames[i]+" has third maximum correlation with "+continuousVariablesNames[posi[2]]+" with correlation value "+maxi[2]);

                //Calculating sum required for calculating slope and intercept
                double[] sumOfXn=new double[3];
                double[] sumOfXnY=new double[3];
                double[] sumOfXnSquare=new double[3];
                double variRows=0,sumOfY=0,sumOfX1X2=0,sumOfX1X3=0,sumOfX2X3=0;
                for(int j=0;j<totalRows;j++) {
                    if(!table.column(continuousVariablesNames[i]).isMissing(j) && !table.column(continuousVariablesNames[posi[0]]).isMissing(j) && !table.column(continuousVariablesNames[posi[1]]).isMissing(j) && !table.column(continuousVariablesNames[posi[2]]).isMissing(j)) {
                        variRows+=1;
                        double yValue=0,x1Value=0,x2Value=0,x3Value=0;
                        Column<?> yColumn=table.column(continuousVariablesNames[i]);
                        Column<?> x1Column=table.column(continuousVariablesNames[posi[0]]);
                        Column<?> x2Column=table.column(continuousVariablesNames[posi[1]]);
                        Column<?> x3Column=table.column(continuousVariablesNames[posi[2]]);
                        String yType=yColumn.type().name();
                        String x1Type=x1Column.type().name();
                        String x2Type=x2Column.type().name();
                        String x3Type=x3Column.type().name();
                        if(yType.equals("INTEGER")) {
                            yValue=table.intColumn(continuousVariablesNames[i]).getInt(j);
                        }
                        else if(yType.equals("DOUBLE")) {
                            yValue=table.doubleColumn(continuousVariablesNames[i]).getDouble(j);
                        }
                        if(x1Type.equals("INTEGER")) {
                            x1Value=table.intColumn(continuousVariablesNames[posi[0]]).getInt(j);
                        }
                        else if(x1Type.equals("DOUBLE")) {
                            x1Value=table.doubleColumn(continuousVariablesNames[posi[0]]).getDouble(j);
                        }
                        if(x2Type.equals("INTEGER")) {
                            x2Value=table.intColumn(continuousVariablesNames[posi[1]]).getInt(j);
                        }
                        else if(x2Type.equals("DOUBLE")) {
                            x2Value=table.doubleColumn(continuousVariablesNames[posi[1]]).getDouble(j);
                        }
                        if(x3Type.equals("INTEGER")) {
                            x3Value=table.intColumn(continuousVariablesNames[posi[2]]).getInt(j);
                        }
                        else if(x3Type.equals("DOUBLE")) {
                            x3Value=table.doubleColumn(continuousVariablesNames[posi[2]]).getDouble(j);
                        }
                        sumOfY+=yValue;
                        sumOfXn[0]+=x1Value;
                        sumOfXn[1]+=x2Value;
                        sumOfXn[2]+=x3Value;
                        sumOfX1X2+=x1Value*x2Value;
                        sumOfX1X3+=x1Value*x3Value;
                        sumOfX2X3+=x2Value*x3Value;
                        sumOfXnY[0]+=x1Value*yValue;
                        sumOfXnY[1]+=x2Value*yValue;
                        sumOfXnY[2]+=x3Value*yValue;
                        sumOfXnSquare[0]+=x1Value*x1Value;
                        sumOfXnSquare[1]+=x2Value*x2Value;
                        sumOfXnSquare[2]+=x3Value*x3Value;
                    }
                }

                System.out.println("The sum of "+continuousVariablesNames[i]+" is "+sumOfY);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+" is "+sumOfXn[0]);
                System.out.println("The sum of "+continuousVariablesNames[posi[1]]+" is "+sumOfXn[1]);
                System.out.println("The sum of "+continuousVariablesNames[posi[2]]+" is "+sumOfXn[2]);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[i]+" is "+sumOfXnY[0]);
                System.out.println("The sum of "+continuousVariablesNames[posi[1]]+"*"+continuousVariablesNames[i]+" is "+sumOfXnY[1]);
                System.out.println("The sum of "+continuousVariablesNames[posi[2]]+"*"+continuousVariablesNames[i]+" is "+sumOfXnY[2]);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[posi[0]]+" is "+sumOfXnSquare[0]);
                System.out.println("The sum of "+continuousVariablesNames[posi[1]]+"*"+continuousVariablesNames[posi[1]]+" is "+sumOfXnSquare[1]);
                System.out.println("The sum of "+continuousVariablesNames[posi[2]]+"*"+continuousVariablesNames[posi[2]]+" is "+sumOfXnSquare[2]);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[posi[1]]+" is "+sumOfX1X2);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[posi[2]]+" is "+sumOfX1X3);
                System.out.println("The sum of "+continuousVariablesNames[posi[1]]+"*"+continuousVariablesNames[posi[2]]+" is "+sumOfX2X3);

                //Creating matrix of coefficents
                double[][] valueMatrix=new double[1][4];
                double[][] originalMatrix=new double[4][4];
                valueMatrix[0][0]=sumOfY; valueMatrix[0][1]=sumOfXnY[0]; valueMatrix[0][2]=sumOfXnY[1]; valueMatrix[0][3]=sumOfXnY[2];
                originalMatrix[0][0]=variRows; originalMatrix[0][1]=sumOfXn[0]; originalMatrix[0][2]=sumOfXn[1]; originalMatrix[0][3]=sumOfXn[2];
                originalMatrix[1][0]=sumOfXn[0]; originalMatrix[1][1]=sumOfXnSquare[0]; originalMatrix[1][2]=sumOfX1X2; originalMatrix[1][3]=sumOfX1X3;
                originalMatrix[2][0]=sumOfXn[1]; originalMatrix[2][1]=sumOfX1X2; originalMatrix[2][2]=sumOfXnSquare[1]; originalMatrix[2][3]=sumOfX2X3;
                originalMatrix[3][0]=sumOfXn[2]; originalMatrix[3][1]=sumOfX1X3; originalMatrix[3][2]=sumOfX2X3; originalMatrix[3][3]=sumOfXnSquare[2];

                //Checking if inverse exists
                double determinantMatrix=getDeterminant(originalMatrix,4);
                if(determinantMatrix==0) {
                    System.out.println(determinantMatrix);
                    System.out.println("Inverse does not exist");
                } else {
                    //Getting adjoint matrix
                    double[][] adjointMatrix=getAdjointMatrix(originalMatrix);

                    //Calculating inverse matrix
                    double[][] inverseMatrix=new double[4][4];
                    for(int j=0;j<4;j++) {
                        for(int k=0;k<4;k++) {
                            inverseMatrix[j][k]=adjointMatrix[j][k]/determinantMatrix;
                        }
                    }

                    //Calculating slope and intercept
                    double[] slopeAndIntercept=getSlopeAndIntercepts(inverseMatrix,valueMatrix);
                    System.out.println("The slope of "+continuousVariablesNames[i]+" with respect to "+continuousVariablesNames[posi[0]]+" is "+slopeAndIntercept[1]);
                    System.out.println("The slope of "+continuousVariablesNames[i]+" with respect to "+continuousVariablesNames[posi[1]]+" is "+slopeAndIntercept[2]);
                    System.out.println("The slope of "+continuousVariablesNames[i]+" with respect to "+continuousVariablesNames[posi[2]]+" is "+slopeAndIntercept[3]);
                    System.out.println("The intercept of "+continuousVariablesNames[i]+" is "+slopeAndIntercept[0]);

                    //Filling missing values
                    for(int j=0;j<totalRows;j++) {
                        if(table.column(continuousVariablesNames[i]).isMissing(j) && !table.column(continuousVariablesNames[posi[0]]).isMissing(j) && !table.column(continuousVariablesNames[posi[1]]).isMissing(j) && !table.column(continuousVariablesNames[posi[2]]).isMissing(j)) {
                            double x1Value=0,x2Value=0,x3Value=0,yValue=0;
                            Column<?> x1Column=table.column(continuousVariablesNames[posi[0]]);
                            Column<?> x2Column=table.column(continuousVariablesNames[posi[1]]);
                            Column<?> x3Column=table.column(continuousVariablesNames[posi[2]]);
                            Column<?> yColumn=table.column(continuousVariablesNames[i]);
                            String x1Type=x1Column.type().name();
                            String x2Type=x2Column.type().name();
                            String x3Type=x3Column.type().name();
                            String yType=yColumn.type().name();
                            if(x1Type.equals("INTEGER")) {
                                x1Value=table.intColumn(continuousVariablesNames[posi[0]]).getInt(j);
                            }
                            else if(x1Type.equals("DOUBLE")) {
                                x1Value=table.doubleColumn(continuousVariablesNames[posi[0]]).getDouble(j);
                            }
                            if(x2Type.equals("INTEGER")) {
                                x2Value=table.intColumn(continuousVariablesNames[posi[1]]).getInt(j);
                            }
                            else if(x2Type.equals("DOUBLE")) {
                                x2Value=table.doubleColumn(continuousVariablesNames[posi[1]]).getDouble(j);
                            }
                            if(x3Type.equals("INTEGER")) {
                                x3Value=table.intColumn(continuousVariablesNames[posi[2]]).getInt(j);
                            }
                            else if(x3Type.equals("DOUBLE")) {
                                x3Value=table.doubleColumn(continuousVariablesNames[posi[2]]).getDouble(j);
                            }
                            yValue=slopeAndIntercept[1]*x1Value+slopeAndIntercept[2]*x2Value+slopeAndIntercept[3]*x3Value+slopeAndIntercept[0];
                            if(yType.equals("INTEGER")) {
                                table.intColumn(continuousVariablesNames[i]).set(j,(int)yValue);
                            }
                            else if(yType.equals("DOUBLE")) {
                                table.doubleColumn(continuousVariablesNames[i]).set(j,yValue);
                            }
                            System.out.println("The predicted value of "+continuousVariablesNames[i]+" for row "+j+" is "+yValue);
                        }
                    }
                }
            }
        }
        return table;
    }
    public Table fillMissingValuesUsing2ndMLR() {
        //Creating variables
        Table table=this.inputTable;
        CorrelationMatrix correlationMatrix01=new CorrelationMatrix(table);
        double[][] correlationMatrix=correlationMatrix01.getCorrelationMatrix();
        Table structureOfTable=table.structure();
        int totalRows=table.rowCount();

        //Storing attributes
        String[] attr=table.columnNames().toArray(new String[0]);
        int continuousVariablesCount=structureOfTable.stringColumn(2).isEqualTo("INTEGER").size();
        continuousVariablesCount+=structureOfTable.stringColumn(2).isEqualTo("DOUBLE").size();

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

        //Iterating through each and every variable
        for(int i=0;i<continuousVariablesCount;i++) {
            if(table.column(continuousVariablesNames[i]).countMissing()!=0) {
                int[] posi=new int[2];
                double[] maxi=new double[2];
                for(int j=0;j<continuousVariablesCount;j++) {
                    if(i!=j) {
                        double curr=Math.abs(correlationMatrix[i][j]);
                        if(curr>maxi[0]) {
                            maxi[1]=maxi[0];
                            posi[1]=posi[0];
                            maxi[0]=correlationMatrix[i][j];
                            posi[0]=j;
                        }
                        else if(curr>maxi[1]) {
                            maxi[1]=correlationMatrix[i][j];
                            posi[1]=j;
                        }
                    }
                }
                System.out.println("The variable "+continuousVariablesNames[i]+" has maximum correlation with "+continuousVariablesNames[posi[0]]+" with correlation value "+maxi[0]);
                System.out.println("The variable "+continuousVariablesNames[i]+" has second maximum correlation with "+continuousVariablesNames[posi[1]]+" with correlation value "+maxi[1]);

                //Calculating sum required for calculating slope and intercept
                double[] sumOfXn=new double[2];
                double[] sumOfXnY=new double[2];
                double[] sumOfXnSquare=new double[2];
                double variRows=0,sumOfY=0,sumOfX1X2=0;
                for(int j=0;j<totalRows;j++) {
                    if(!table.column(continuousVariablesNames[i]).isMissing(j) && !table.column(continuousVariablesNames[posi[0]]).isMissing(j) && !table.column(continuousVariablesNames[posi[1]]).isMissing(j)) {
                        variRows+=1;
                        double yValue=0,x1Value=0,x2Value=0;
                        Column<?> yColumn=table.column(continuousVariablesNames[i]);
                        Column<?> x1Column=table.column(continuousVariablesNames[posi[0]]);
                        Column<?> x2Column=table.column(continuousVariablesNames[posi[1]]);
                        String yType=yColumn.type().name();
                        String x1Type=x1Column.type().name();
                        String x2Type=x2Column.type().name();
                        if(yType.equals("INTEGER")) {
                            yValue=table.intColumn(continuousVariablesNames[i]).getInt(j);
                        }
                        else if(yType.equals("DOUBLE")) {
                            yValue=table.doubleColumn(continuousVariablesNames[i]).getDouble(j);
                        }
                        if(x1Type.equals("INTEGER")) {
                            x1Value=table.intColumn(continuousVariablesNames[posi[0]]).getInt(j);
                        }
                        else if(x1Type.equals("DOUBLE")) {
                            x1Value=table.doubleColumn(continuousVariablesNames[posi[0]]).getDouble(j);
                        }
                        if(x2Type.equals("INTEGER")) {
                            x2Value=table.intColumn(continuousVariablesNames[posi[1]]).getInt(j);
                        }
                        else if(x2Type.equals("DOUBLE")) {
                            x2Value=table.doubleColumn(continuousVariablesNames[posi[1]]).getDouble(j);
                        }
                        sumOfY+=yValue;
                        sumOfXn[0]+=x1Value;
                        sumOfXn[1]+=x2Value;
                        sumOfX1X2+=x1Value*x2Value;
                        sumOfXnY[0]+=x1Value*yValue;
                        sumOfXnY[1]+=x2Value*yValue;
                        sumOfXnSquare[0]+=x1Value*x1Value;
                        sumOfXnSquare[1]+=x2Value*x2Value;
                    }
                }

                System.out.println("The sum of "+continuousVariablesNames[i]+" is "+sumOfY);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+" is "+sumOfXn[0]);
                System.out.println("The sum of "+continuousVariablesNames[posi[1]]+" is "+sumOfXn[1]);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[i]+" is "+sumOfXnY[0]);
                System.out.println("The sum of "+continuousVariablesNames[posi[1]]+"*"+continuousVariablesNames[i]+" is "+sumOfXnY[1]);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[posi[0]]+" is "+sumOfXnSquare[0]);
                System.out.println("The sum of "+continuousVariablesNames[posi[1]]+"*"+continuousVariablesNames[posi[1]]+" is "+sumOfXnSquare[1]);
                System.out.println("The sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[posi[1]]+" is "+sumOfX1X2);

                //Calculating regression sums
                sumOfXnSquare[0]-=(sumOfXn[0]*sumOfXn[0])/variRows;
                sumOfXnSquare[1]-=(sumOfXn[1]*sumOfXn[1])/variRows;
                sumOfXnY[0]-=(sumOfXn[0]*sumOfY)/variRows;
                sumOfXnY[1]-=(sumOfXn[1]*sumOfY)/variRows;
                sumOfX1X2-=(sumOfXn[0]*sumOfXn[1])/variRows;

                System.out.println("The regression sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[posi[0]]+" is "+sumOfXnSquare[0]);
                System.out.println("The regression sum of "+continuousVariablesNames[posi[1]]+"*"+continuousVariablesNames[posi[1]]+" is "+sumOfXnSquare[1]);
                System.out.println("The regression sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[i]+" is "+sumOfXnY[0]);
                System.out.println("The regression sum of "+continuousVariablesNames[posi[1]]+"*"+continuousVariablesNames[i]+" is "+sumOfXnY[1]);
                System.out.println("The regression sum of "+continuousVariablesNames[posi[0]]+"*"+continuousVariablesNames[posi[1]]+" is "+sumOfX1X2);

                //Calculating slope and intercept
                double slope1=(((sumOfXnSquare[1])*(sumOfXnY[0]))-((sumOfX1X2)*(sumOfXnY[1])))/(((sumOfXnSquare[0])*(sumOfXnSquare[1]))-((sumOfX1X2)*(sumOfX1X2)));
                double slope2=(((sumOfXnSquare[0])*(sumOfXnY[1]))-((sumOfX1X2)*(sumOfXnY[0])))/(((sumOfXnSquare[0])*(sumOfXnSquare[1]))-((sumOfX1X2)*(sumOfX1X2)));
                double intercept=(sumOfY-(slope1*sumOfXn[0])-(slope2*sumOfXn[1]))/variRows;

                System.out.println("The slope of "+continuousVariablesNames[i]+" with respect to "+continuousVariablesNames[posi[0]]+" is "+slope1);
                System.out.println("The slope of "+continuousVariablesNames[i]+" with respect to "+continuousVariablesNames[posi[1]]+" is "+slope2);
                System.out.println("The intercept of "+continuousVariablesNames[i]+" is "+intercept);

                //Filling missing values
                for(int j=0;j<totalRows;j++) {
                    if(table.column(continuousVariablesNames[i]).isMissing(j) && !table.column(continuousVariablesNames[posi[0]]).isMissing(j) && !table.column(continuousVariablesNames[posi[1]]).isMissing(j)) {
                        double x1Value=0,x2Value=0,yValue=0;
                        Column<?> x1Column=table.column(continuousVariablesNames[posi[0]]);
                        Column<?> x2Column=table.column(continuousVariablesNames[posi[1]]);
                        Column<?> yColumn=table.column(continuousVariablesNames[i]);
                        String x1Type=x1Column.type().name();
                        String x2Type=x2Column.type().name();
                        String yType=yColumn.type().name();
                        if(x1Type.equals("INTEGER")) {
                            x1Value=table.intColumn(continuousVariablesNames[posi[0]]).getInt(j);
                        }
                        else if(x1Type.equals("DOUBLE")) {
                            x1Value=table.doubleColumn(continuousVariablesNames[posi[0]]).getDouble(j);
                        }
                        if(x2Type.equals("INTEGER")) {
                            x2Value=table.intColumn(continuousVariablesNames[posi[1]]).getInt(j);
                        }
                        else if(x2Type.equals("DOUBLE")) {
                            x2Value=table.doubleColumn(continuousVariablesNames[posi[1]]).getDouble(j);
                        }
                        yValue=slope1*x1Value+slope2*x2Value+intercept;
                        if(yType.equals("INTEGER")) {
                            table.intColumn(continuousVariablesNames[i]).set(j,(int)yValue);
                        }
                        else if(yType.equals("DOUBLE")) {
                            table.doubleColumn(continuousVariablesNames[i]).set(j,yValue);
                        }
                        System.out.println("The predicted value of "+continuousVariablesNames[i]+" for row "+j+" is "+yValue);
                    }
                }
            }
        }
        return table;
    }
    public static void main(String[] args) {
        MultipleLinearRegression multipleLinearRegression01=new MultipleLinearRegression(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println(multipleLinearRegression01.getOutputTable());
    }
}