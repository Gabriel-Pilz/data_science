# data_science

* download the .jar file
* run it in a console of your choice with java -jar extract.jar
* use the following parameters: pathToFile1 pathToFile2 columnOfCosts columnsToAggregate

The script expects .csv files. If you don't want to create them yourself, you find them in the /resources folder.

Please bear in mind that the files have to have the same structure for their data sets. The number for the column of costs has to be the same for all given files. There can only be given one column for costs. If there are more than one cost columns, you have to run the jar multiple times.

You can copy the .jar to the location of your .csv files. Then you don't have to specify the complete path but only the filename.
