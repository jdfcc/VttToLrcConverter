
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.File;
import java.io.BufferedWriter;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description BatchVttToLrcConverter
 * @DateTime 2023/9/5 11:11
 */

public class BatchVttToLrcConverter {
    public static void main(String[] args) {

        String inputFolder = "file_input_dir"; // 输入文件夹路径
        String outputFolder = "file_output_dir"; // 输出文件夹路径

        File topFolder = new File(inputFolder);
        convertVttFilesInFolder(topFolder, outputFolder);

        System.out.println("批量转换完成。");
    }

    private static void convertVttFilesInFolder(File folder, String outputFolder) {
        File[] files = folder.listFiles();

        if (files == null) {
            System.err.println("无法读取文件夹或文件夹为空。");
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是子文件夹，递归处理
                String subOutputFolder = outputFolder + File.separator + file.getName();
                convertVttFilesInFolder(file, subOutputFolder);
            } else if (file.getName().endsWith(".vtt")) {
                // 如果是VTT文件，进行转换
                convertVttToLrc(file, outputFolder);
            }
        }
    }

    private static void convertVttToLrc(File vttFile, String outputFolder) {
        try (BufferedReader reader = new BufferedReader(new FileReader(vttFile))) {
            String lrcFileName = vttFile.getName().replace(".vtt", ".lrc");
            File lrcFile = new File(outputFolder, lrcFileName);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(lrcFile))) {
                String line;
                String currentTime = "";
                Pattern timePattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}) -->");

                while ((line = reader.readLine()) != null) {
                    if (line.matches("\\d+")) {
                        // 如果行只包含数字，跳过该行（标识段落的数字）
                        continue;
                    } else if (line.contains("-->")) {
                        // 匹配VTT格式的时间戳，提取第一个时间
                        Matcher matcher = timePattern.matcher(line);
                        if (matcher.find()) {
                            currentTime = "[" + matcher.group(1) + "]";
                        }
                    } else if (!line.isEmpty()) {
                        // 写入LRC格式的歌词行
                        writer.write(currentTime + line + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

