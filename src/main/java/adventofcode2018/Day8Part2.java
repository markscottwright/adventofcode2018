package adventofcode2018;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;

public class Day8Part2 {
    static int getNodeValue(StreamTokenizer licenseFileTokens) throws IOException {
        if (licenseFileTokens.nextToken() == StreamTokenizer.TT_EOF)
            return 0;
        int numChildNodes = (int) licenseFileTokens.nval;
        if (licenseFileTokens.nextToken() == StreamTokenizer.TT_EOF)
            return 0;
        int numMetadataNodes = (int) licenseFileTokens.nval;

        ArrayList<Integer> childNodeValues = new ArrayList<>();
        for (int i = 0; i < numChildNodes; ++i) {
            childNodeValues.add(getNodeValue(licenseFileTokens));
        }

        int metadataSum = 0;

        for (int i = 0; i < numMetadataNodes; ++i) {
            licenseFileTokens.nextToken();
            assert (licenseFileTokens.ttype == StreamTokenizer.TT_NUMBER);
            // If a node has no child nodes, its value is the sum of its
            // metadata entries.

            if (childNodeValues.size() == 0) {
                metadataSum += (int) licenseFileTokens.nval;
            }

            // if a node does have child nodes, the metadata entries become
            // indexes which refer to those child nodes.
            else {
                int childNodeIndex = (int) licenseFileTokens.nval;

                // childNodeIndex starts at 1. Invalid references are just
                // skipped
                if (childNodeIndex > 0
                        && childNodeIndex <= childNodeValues.size())
                    metadataSum += childNodeValues.get(childNodeIndex - 1);

            }
        }
        return metadataSum;
    }

    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        try (FileReader dataFile = new FileReader("data/day8.txt");
                BufferedReader dataFileReader = new BufferedReader(dataFile);) {
            StreamTokenizer tokenReader = new StreamTokenizer(dataFileReader);
            System.out.println(getNodeValue(tokenReader));
        }
    }
}
