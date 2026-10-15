
package busbooking.data;

import java.io.*;
import java.util.*;

public class CSVManager {

    public static List<String[]> loadFromCSV(String fileName) {

        List<String[]> data = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(new FileInputStream(fileName), java.nio.charset.StandardCharsets.UTF_8))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                data.add(values);
            }

        } catch (IOException e) {
            System.out.println("Cannot read file: " + fileName);
        }

        return data;
    }

    public static void saveToCSV(
            String fileName,
            List<String[]> data) {

        try (PrintWriter pw =
                     new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), java.nio.charset.StandardCharsets.UTF_8))) {

            for (String[] row : data) {

                pw.println(String.join(",", row));
            }

        } catch (IOException e) {
            System.out.println("Cannot save file: " + fileName);
        }
    }
}