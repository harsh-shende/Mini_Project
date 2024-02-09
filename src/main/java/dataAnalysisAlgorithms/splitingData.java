package dataAnalysisAlgorithms;

import tech.tablesaw.api.Table;

public class splitingData {
    public static Table getTrainData(Table table) {
        int size=table.rowCount();
        return table.first(size/2);
    }
    public static Table getTestData(Table table) {
        int size=table.rowCount();
        return table.last(size/2);
    }
    public static void main(String[] args) {
        Table table=Table.read().csv("/home/tendopain/IdeaProjects/Mini_Project/Datasets/sample_corr_01.csv");
        Table trainData=getTrainData(table);
        Table testData=getTestData(table);
        System.out.println("Train Data");
        System.out.println(trainData);
        System.out.println();
        System.out.println("Test Data");
        System.out.println(testData);

    }
}
