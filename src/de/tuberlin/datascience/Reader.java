package de.tuberlin.datascience;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reader {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("No arguments given.");
			return;
		}

		int argumentsAfterFiles = 0;
		List<File> files = new ArrayList<File>();
		while (args[argumentsAfterFiles].endsWith(".csv")) {
			files.add(new File(args[argumentsAfterFiles++]));
		}
		if (argumentsAfterFiles == 0) {
			// read all from current directory
			argumentsAfterFiles++;
			File folder = new File(args[0]);
			if (folder == null || !folder.isDirectory()) {
				System.out
						.println("First argument has to be a .csv file or a folder name.");
				return;
			}
			for (File file : folder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().endsWith(".cvs")) {
						return true;
					}
					return false;
				}
			})) {
				files.add(file);
			}
		}
		int columnAnsatz = Integer.parseInt(args[argumentsAfterFiles++]);
		List<Integer> columnsToAggregate = new ArrayList<Integer>();
		List<String> columnsToAggregateNames = new ArrayList<String>();
		List<HashMap<String, Double>> listOfAggregatedColumns = new ArrayList<HashMap<String, Double>>();
		for (int i = argumentsAfterFiles; i < args.length; i++) {
			columnsToAggregate.add(Integer.parseInt(args[i]));
			listOfAggregatedColumns.add(new HashMap<String, Double>());
		}

		String line;

		for (File file : files) {
			Double ansatzGesamt = new Double(0);
			try (InputStream fis = new FileInputStream(file);
					InputStreamReader isr = new InputStreamReader(fis,
							Charset.forName("UTF-8"));
					BufferedReader br = new BufferedReader(isr);) {
				int columnNr = 0;
				if ((line = br.readLine()) != null) {
					// first line
					line = line.replace("\"", "");
					String[] columns = line.split(":");
					columnNr = columns.length;
					System.out.println("Zu aggregierende Spalten: ");
					for (int columnToAggregate : columnsToAggregate) {
						columnsToAggregateNames.add(columns[columnToAggregate]);
						System.out.print(columns[columnToAggregate] + "; ");
					}
					System.out.println();
				}

				int rowNumber = 0;
				while ((line = br.readLine()) != null) {
					rowNumber++;
					line = line.replace(",", "");
					line = line.replace("\"", "");
					String[] columns = line.split(":");
					if (columns.length < columnNr) {
						System.out.println("Row " + rowNumber
								+ " not in the same format as the header.");
						continue;
					}

					ansatzGesamt += Double.parseDouble(columns[columnAnsatz]);

					for (int i = 0; i < columnsToAggregate.size(); i++) {
						HashMap<String, Double> map = listOfAggregatedColumns
								.get(i);
						Double ansatzProKatWert = map
								.get(columns[columnsToAggregate.get(i)]);

						if (ansatzProKatWert == null) {
							ansatzProKatWert = Double
									.parseDouble(columns[columnAnsatz]);
						} else {
							ansatzProKatWert += Double
									.parseDouble(columns[columnAnsatz]);
						}
						map.put(columns[columnsToAggregate.get(i)],
								ansatzProKatWert);
					}
				}
			} catch (IOException e) {
				System.out.println("whoops");
				e.printStackTrace();
				return;
			} catch (NumberFormatException e) {
				System.out.println("whoops");
				e.printStackTrace();
				return;
			}
			
			DecimalFormat df2 = new DecimalFormat( "###,###,###,##0.00" );
			System.out.println("Ansatz gesamt (in Tsd): " + df2.format(ansatzGesamt));
			int i = 0;
			for (Map<String, Double> aggregatedColumn : listOfAggregatedColumns) {
				System.out.println("Nach "
						+ columnsToAggregateNames.get(i++) + " aggregiert:");
				for (String key : aggregatedColumn.keySet()) {
					System.out.println("Ansatz für Kategorie " + key + " (in Tsd): "
							+ df2.format(aggregatedColumn.get(key)));
				}
			}
		}
	}
}
