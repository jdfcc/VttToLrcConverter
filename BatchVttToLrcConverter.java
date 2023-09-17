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

    private static int count = 0;

    public static void main(String[] args) {
        String inputFolder = "file_path"; // 输入文件夹路径
        String outputFolder = inputFolder; // 输出文件夹路径


        if (args.length != 0) {        // 提取目录路径
            inputFolder = args[0].replace("\\", "\\\\"); // 去掉 "dir=" 前缀
            outputFolder = inputFolder;
            System.out.println("指定的目录路径是: " + inputFolder);
        }


        File topFolder = new File(inputFolder);
        boolean isSuccessful = convertVttFilesInFolder(topFolder, outputFolder);

        if (isSuccessful) {
            System.out.println("批量转换完成,共转换了" + count + "条");
            return;
        }
        System.out.println("批量转换失败");
    }

    private static boolean convertVttFilesInFolder(File folder, String outputFolder) {
        File[] files = folder.listFiles();

        if (files == null) {
            System.err.println("无法读取文件夹或文件夹为空。");
            return false;
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
        return true;
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
        count++;
    }


}

