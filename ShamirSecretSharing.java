import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        try {
        
            FileReader reader = new FileReader("input.json");
            StringBuilder jsonData = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                jsonData.append((char) i);
            }
            reader.close();

            String content = jsonData.toString();
            String kValue = extractValue(content, "\"k\":");
            int k = Integer.parseInt(kValue.trim()); 
            List<Point> points = new ArrayList<>();
            String[] rootEntries = extractSection(content, "\"1\":", "\"keys\":");
            
            for (String rootEntry : rootEntries) {
                if (rootEntry.trim().isEmpty()) continue;
                String xStr = extractValue(rootEntry, "\"base\":");
                int base = Integer.parseInt(extractValue(rootEntry, "\"base\":").trim());
                String valueStr = extractValue(rootEntry, "\"value\":");
                int y = Integer.parseInt(valueStr.trim(), base);
                String xValue = rootEntry.split(":")[0].replace("\"", "").trim();
                System.out.println("xValue: " + xValue); 
                int x = Integer.parseInt(xValue);  
                points.add(new Point(x, y));
            }

            
            int secret = findConstantTerm(points, k);
            System.out.println("The secret (constant term c) is: " + secret);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String extractValue(String content, String key) {
        int start = content.indexOf(key) + key.length();
        int end = content.indexOf(",", start);
        if (end == -1) {
            end = content.indexOf("}", start);
        }
        return content.substring(start, end).trim().replace("\"", "");
    }

    private static String[] extractSection(String content, String startMarker, String endMarker) {
        int start = content.indexOf(startMarker);
        int end = content.indexOf(endMarker, start);
        String section = content.substring(start, end).trim();
        return section.split("\\},\\{");  
    }

    public static int findConstantTerm(List<Point> points, int k) {
        int result = 0;

        for (int i = 0; i < k; i++) {
            Point p_i = points.get(i);
            int numerator = 1;
            int denominator = 1;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    Point p_j = points.get(j);
                    numerator *= -p_j.x;
                    denominator *= (p_i.x - p_j.x);
                }
            }

            int lagrangeTerm = (p_i.y * numerator) / denominator;
            result += lagrangeTerm;
        }

        return result;
    }
}
