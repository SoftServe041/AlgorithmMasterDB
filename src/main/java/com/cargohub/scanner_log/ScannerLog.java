package com.cargohub.scanner_log;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Class scans file in the source of project. Inspects with pattern time and meaningful row
 * @author F_Dauphin
 * @version 0.0.1
 */
public class ScannerLog {
    private static File logFile = new File ("demoLog.log");
    public static boolean isLogFileExisted() {
        return logFile.exists();
    }

    public static boolean isLogFileExisted(String fileName) {
        logFile = new File(fileName);
        return logFile.exists();
    }

    public static List<String[]>  getLogs() {
        List<String[]> logs = new ArrayList<>();
        if (!isLogFileExisted()) {
            logs.add(new String[]{"Logs are gathering or processing. Try later.", LocalDate.now().toString()});
        }
        try (Scanner scanner = new Scanner(logFile)) {
            String regex = "Transporter[ ]([A-z0-9 ]+)+";
            regex = "([0-9]{4}-[0-9]{2}-[0-9]{2}[ ][0-9]{2}:[0-9]{2}:[0-9.]{6})(.+\n.*)(Transporter[ ]([A-z0-9 ]+)+)";
            scanner.findAll(regex).forEach(s -> {
                logs.add(new String[] {s.group(3), s.group(1)});
            });
        } catch (FileNotFoundException e) {
            // it is less possible than something could appear here.
        }
        logs.forEach(strings -> {
            System.out.println(Arrays.toString(strings));
        });
        return logs;
    }
}
