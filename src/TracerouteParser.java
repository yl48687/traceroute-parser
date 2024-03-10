import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

public class TracerouteParser {
	private static String icmpLine1;
	private static String icmpLine2;
	private static String icmpLine3;
	private static String tcpLine1;
	private static String tcpLine2;
	private static String ip = null;
	private static double timestampICMP = 0.0;
	private static double timestampTCP = 0.0;
	private static double timeDifference;
	private static String timeDiff;
	private static int ttl = 0;
	private static int idCount = 0;
	private static String temp1 = null;
	private static String temp2 = null;
	private static String temp3 = null;
	
    public static void main(String[] args) {
    	try {
    		BufferedReader br = new BufferedReader(new FileReader("sampletcpdump.txt"));
            List<String> fileContent = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {
                fileContent.add(line);
            } // while

            String[] fileArray = fileContent.toArray(new String[0]);

            List<String> idList = extractIds(fileArray);

            Map<String, Integer> idCountMap = new HashMap<>();
            for (String id : idList) {
                idCountMap.put(id, idCountMap.getOrDefault(id, 0) + 1);
            } // for

            idList.removeIf(id -> idCountMap.get(id) != 2);

            Set<String> uniqueIds = new HashSet<>(idList);
            idList.clear();
            idList.retainAll(uniqueIds);
            
            String[] uniqueIdsArray = uniqueIds.toArray(new String[0]);           
            
            List<String> firstOutput = new ArrayList<>();
            
            for (String uniqueId : uniqueIdsArray) {
            	idCount = 0;
	            for (int i = 0; i < fileArray.length; i++) {
	            	if (fileArray[i].contains("id " + uniqueId)) {
	            		idCount++;
	            		if (fileArray[i].startsWith("\t")) {
		                    icmpLine3 = fileArray[i];
		                    icmpLine2 = fileArray[i - 1];
		                    icmpLine1 = fileArray[i - 2];
		                    timestampICMP = extractTimestamp(icmpLine1);
		                    ip = extractIP(icmpLine2);
	                    } else {
	                        tcpLine1 = fileArray[i];
	                        tcpLine2 = fileArray[i + 1];
	                        timestampTCP = extractTimestamp(tcpLine1);
	                        ttl = extractTTL(tcpLine1, 5);
	                    } // if
	            		if (idCount == 2) {
		            		timeDifference = (timestampICMP - timestampTCP) * 1000;
		            		timeDiff = Double.toString(timeDifference);
		            		firstOutput.add("TTL " + ttl);
		            		firstOutput.add(ip);
		            		firstOutput.add(timeDiff);
	            		} // if
	            	} // if
	            } // for
            } // for
            
            for (int i = 1; i < firstOutput.size() / 9 + 1; i++) {
            	print(i, firstOutput);
            } // for
	    } catch (IOException e) {
	        e.printStackTrace();
	    } //try
	} // main
    	
	private static double extractTimestamp(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length > 0) {
            try {
                String timestampString = parts[0].replaceAll("[^\\d.]", "");
                return Double.parseDouble(timestampString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } // try
        } // if
        return 0.0;
    } // extractTimestamp

    private static int extractTTL(String line, int position) {
        String[] parts = line.split("\\s+");
        if (parts.length > position) {
            try {
                String timestampString = parts[position].replaceAll("[^\\d.]", "");
                return Integer.parseInt(timestampString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } // try
        } // if
        return 0;
    } // extractTTL

    private static String extractIP(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length > 0) {
            return parts[1];
        } // if
        return "";
    } // extractIP
    
    private static List<String> extractIds(String[] fileArray) {
        List<String> idList = new ArrayList<>();
        for (int i = 0; i < fileArray.length; i++) {
            if (fileArray[i].contains("id ")) {
                String id = extractId(fileArray[i]);
                idList.add(id);
            } // if
        } // for
        return idList;
    } // extractIds

    private static String extractId(String line) {
    	String[] parts = line.split("\\s+");
        if (parts.length == 16) {
            return parts[6];
        } else {
        	return parts[7];
        } // if
    } // extractId
    
    private static void print(int ttl, List<String> firstOutput) {        
        String[] firstArray = firstOutput.toArray(new String[0]);
        List<String> found = new ArrayList<>();

        for (int i = 0; i + 2 < firstArray.length; i++) {
        	if (firstArray[i].equals("TTL " + ttl)) {
        		temp1 = firstArray[i];
        		temp2 = firstArray[i + 1];
        		temp3 = firstArray[i + 2];
        		found.add(temp1);
        		found.add(temp2);
        		found.add(temp3);
        	} // if
        } // for
        String[] foundArray = found.toArray(new String[0]);
        
        double firstDiff = Double.valueOf(foundArray[2]);
        double secondDiff = Double.valueOf(foundArray[5]);
        double thirdDiff = Double.valueOf(foundArray[8]);

        double max = Math.max(firstDiff, Math.max(secondDiff, thirdDiff));
        double min = Math.min(firstDiff, Math.min(secondDiff, thirdDiff));
        double mid = firstDiff + secondDiff + thirdDiff - max - min;
       
        System.out.println(foundArray[0]);
        System.out.println(foundArray[1]);
        System.out.printf("%.3f ms\n", min);
        System.out.printf("%.3f ms\n", mid);
        System.out.printf("%.3f ms\n", max);
    } // print

} // TracerouteParser