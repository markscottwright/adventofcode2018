package adventofcode2018;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

public class Day8 {
    
    static int parseNode(StreamTokenizer licenseFileTokens) throws IOException {
        if (licenseFileTokens.nextToken() == StreamTokenizer.TT_EOF)
            return 0;
        int numChildNodes = (int) licenseFileTokens.nval;
        if (licenseFileTokens.nextToken() == StreamTokenizer.TT_EOF)
            return 0;
        int numMetadataNodes = (int) licenseFileTokens.nval;
        
        int metadataSum = 0;
        for (int i = 0; i < numChildNodes; ++i) {
            metadataSum += parseNode(licenseFileTokens);
        }
        for (int i=0; i < numMetadataNodes; ++i) {
            licenseFileTokens.nextToken();
            assert(licenseFileTokens.ttype == StreamTokenizer.TT_NUMBER);
            metadataSum += (int) licenseFileTokens.nval;
        }
        return metadataSum;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        try (FileReader dataFile = new FileReader("data/day8.txt");
                BufferedReader dataFileReader = new BufferedReader(dataFile);
                ) {
            StreamTokenizer tokenReader = new StreamTokenizer(dataFileReader);
            int metadataTotal = parseNode(tokenReader);
            System.out.println(metadataTotal);
        }
    }
}
