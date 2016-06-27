package com.boguenon.service.modules.bayes.common;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.State;

public class DiscretizeTablePanel {
	private static final String   INFINITY                        = "\u221E";
    private static final String   NEGATIVE_INFINITY               = "-" + "\u221E";
    
	public DiscretizeTablePanel() {
		
	}

	public Object[][] setDataFromPartitionedInterval(PartitionedInterval partitionInterval, State[] states, Node node) {
        Object[][] data;
        int i = 0;
        int numIntervals = 0;
        int numColumns = 6; // name-symbol-value-separator-value-symbol
        String[] limits;
        boolean[] belongsToLeftSide;
        numIntervals = partitionInterval.getNumSubintervals();
        double values[] = partitionInterval.getLimits();
        limits = convertToStringLimitValues(values,
                Double.toString(node.getVariable().getPrecision()));
        belongsToLeftSide = partitionInterval.getBelongsToLeftSide();
        data = new Object[numIntervals][numColumns];
        for (i = 0; i < numIntervals; i++) {
            int row = numIntervals - i - 1;
            data[row][0] = states[i].getName(); // name
            data[row][1] = (belongsToLeftSide[i] ? "(" : "["); // low interval
                                                               // symbol
            data[row][2] = limits[i]; // low interval value
            data[row][3] = ","; // separator ","
            data[row][4] = limits[i + 1]; // high interval value
            data[row][5] = (belongsToLeftSide[i + 1] ? "]" : ")"); // high
                                                                   // interval
                                                                   // symbol
        }
        return data;
    }
	
	public String[] convertToStringLimitValues(double[] limits, String precision) {
        String[] tableLimits = new String[limits.length];
        String rounded = "";
        int numDecimals;
        int indexE = precision.indexOf('E');
        if (indexE != -1) {
            numDecimals = Integer.parseInt(precision.substring(indexE + 2, indexE + 3));
        } else {
            int decimalPoint = precision.indexOf('.');
            int one = precision.indexOf('1');
            if (decimalPoint != -1 && one != -1) {
                numDecimals = one - decimalPoint;
            } else {
                numDecimals = 0;
            }
        }
        for (int i = 0; i < limits.length; i++) {
            if (limits[i] == Double.POSITIVE_INFINITY) {
                tableLimits[i] = INFINITY;
            } else if (limits[i] == Double.NEGATIVE_INFINITY) {
                tableLimits[i] = NEGATIVE_INFINITY;
            } else {
                rounded = Double.toString(limits[i]);
                // adding final zeros
                int roundedStringDecimalPlace = rounded.indexOf('.');
                if (roundedStringDecimalPlace == -1) {
                    rounded += ".0";
                }
                roundedStringDecimalPlace = rounded.indexOf('.');
                int finalLength = roundedStringDecimalPlace + numDecimals + 1;
                if (finalLength <= rounded.length()) {
                    rounded = rounded.substring(0, finalLength);
                } else {
                    while (finalLength > rounded.length()) {
                        rounded += "0";
                    }
                }
                // rounded = rounded.replace(',', '.');
                tableLimits[i] = rounded;
            }
        }
        return tableLimits;
    }
}
