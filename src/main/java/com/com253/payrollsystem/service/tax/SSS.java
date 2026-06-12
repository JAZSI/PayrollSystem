package com.com253.payrollsystem.service.tax;

/**
 * Service for resolving SSS contributions from monthly salary brackets.
 */
public final class SSS {

    // Each row is {salary_upper_bound_exclusive, contribution}
    private static final double[][] BRACKETS = {
        {5_250, 250}, {5_750, 275}, {6_250, 300}, {6_750, 325}, {7_250, 350},
        {7_750, 375}, {8_250, 400}, {8_750, 425}, {9_250, 450}, {9_750, 475},
        {10_250, 500}, {10_750, 525}, {11_250, 550}, {11_750, 575}, {12_250, 600},
        {12_750, 625}, {13_250, 650}, {13_750, 675}, {14_250, 700}, {14_750, 725},
        {15_250, 750}, {15_750, 775}, {16_250, 800}, {16_750, 825}, {17_250, 850},
        {17_750, 875}, {18_250, 900}, {18_750, 925}, {19_250, 950}, {19_750, 975},
        {20_250, 1_000}, {20_750, 1_025}, {21_250, 1_050}, {21_750, 1_075}, {22_250, 1_100},
        {22_750, 1_125}, {23_250, 1_150}, {23_750, 1_175}, {24_250, 1_200}, {24_750, 1_225},
        {25_250, 1_250}, {25_750, 1_275}, {26_250, 1_300}, {26_750, 1_325}, {27_250, 1_350},
        {27_750, 1_375}, {28_250, 1_400}, {28_750, 1_425}, {29_250, 1_450}, {29_750, 1_475},
        {30_250, 1_500}, {30_750, 1_525}, {31_250, 1_550}, {31_750, 1_575}, {32_250, 1_600},
        {32_750, 1_625}, {33_250, 1_650}, {33_750, 1_675}, {34_250, 1_700}, {34_750, 1_725}
    };

    private static final double MAX_CONTRIBUTION = 1_750;

    private SSS() {
    }

    /**
     * Computes SSS monthly contribution from monthly salary.
     *
     * @param salary monthly salary basis
     * @return SSS monthly contribution
     */
    public static double monthlyContribution(double salary) {
        for (double[] bracket : BRACKETS) {
            if (salary < bracket[0]) {
                return bracket[1];
            }
        }

        return MAX_CONTRIBUTION;
    }
}
