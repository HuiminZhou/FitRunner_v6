package com.example.huimin_zhou.fitrunner.Util;

/**
 * Created by Lucidity on 17/2/12.
 */

public class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N68067c820(i);
        return p;
    }
    static double N68067c820(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 51.082367) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 51.082367) {
            p = WekaClassifier.N6015b05b1(i);
        }
        return p;
    }
    static double N6015b05b1(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 650.933309) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 650.933309) {
            p = 2;
        }
        return p;
    }
}
