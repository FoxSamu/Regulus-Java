package test;

import javax.swing.JRadioButtonMenuItem;

public class TestCaseMenuItem extends JRadioButtonMenuItem {
    private final TestCase testCase;
    public TestCaseMenuItem( TestCase tc ) {
        super( tc.getName() );
        testCase = tc;
    }

    public TestCase getTestCase() {
        return testCase;
    }
}
