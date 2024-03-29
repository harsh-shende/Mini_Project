package dataAnalysisAlgorithms;

import tech.tablesaw.api.Table;

public class SplitData {
    private Table validationTable;
    private Table inputTable;
    private Table trainData;
    private Table testData;
    SplitData() {
        this.validationTable=null;
        this.inputTable=null;
        this.trainData=null;
        this.testData=null;
    }
    public SplitData(Table inputTable) {
        this.inputTable=inputTable;
        this.validationTable=(this.calculateValidationTable());
        this.trainData=(this.calculateTrainData());
        this.testData=(this.calculateTestData());
    }
    public Table getInputTable() {
        return inputTable;
    }
    public Table getTrainData() {
        return trainData;
    }
    public Table getTestData() {
        return testData;
    }
    public Table getValidationTable() {
        return validationTable;
    }
    public void setInputTable(Table inputTable) {
        this.inputTable=inputTable;
    }
    public void setTrainData(Table trainData) {
        this.trainData=trainData;
    }
    public void setTestData(Table testData) {
        this.testData=testData;
    }
    public void setValidationTable(Table validationTable) {
        this.validationTable=validationTable;
    }
    public Table calculateTrainData() {
        int rows=(this.inputTable.rowCount());
        return (this.inputTable.first((int)(0.8*rows)));
    }
    public Table calculateTestData() {
        int rows=(this.inputTable.rowCount());
        Table tempTable=(this.inputTable.last((int)(0.2*rows)));
        rows=(tempTable.rowCount());
        return (tempTable.first((int)(0.5*rows)));
    }
    public Table calculateValidationTable() {
        int rows=(this.inputTable.rowCount());
        Table tempTable=(this.inputTable.last((int)(0.2*rows)));
        rows=(tempTable.rowCount());
        return (tempTable.last((int)(0.5*rows)));
    }
    public static void main(String[] args) {
        SplitData splitData01=new SplitData(Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/pokemonStatsData.csv"));
        System.out.println("Original Data");
        System.out.println(splitData01.getInputTable().summary());
        System.out.println();
        System.out.println("Train Data");
        System.out.println(splitData01.getTrainData().summary());
        System.out.println();
        System.out.println("Test Data");
        System.out.println(splitData01.getTestData().summary());
        System.out.println();
        System.out.println("Validation Data");
        System.out.println(splitData01.getValidationTable().summary());
    }
}
